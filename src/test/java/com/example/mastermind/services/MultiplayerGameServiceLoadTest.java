package com.example.mastermind.services;

import com.example.mastermind.models.Difficulty;
import com.example.mastermind.models.GameMode;
import com.example.mastermind.models.entities.MultiplayerGame;
import com.example.mastermind.models.entities.Player;
import com.example.mastermind.repositoryLayer.MultiplayerGameRepository;
import com.example.mastermind.repositoryLayer.PlayerRepository;
import com.example.mastermind.utils.EmitterDiagnostics;
import com.example.mastermind.utils.EmitterRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MultiplayerGameServiceLoadTest {

    @Mock
    private MultiplayerGameRepository multiplayerGameRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private EmitterDiagnostics emitterDiagnostics;

    @Mock
    private EmitterRegistry emitterRegistry;

    @Mock
    private PlayerService playerService;

    @InjectMocks
    private MultiplayerGameService multiplayerGameService;

    private Player p1;
    private Player p2;
    private UUID gameId;

    @BeforeEach
    void setup() {
        // Create players
        p1 = new Player();
        p1.setPlayerId(UUID.randomUUID());
        p1.setUsername("alice");
        p1.setPassword("x");
        p1.setEmail("a@example.com");
        p1.setRole("ROLE_USER");

        p2 = new Player();
        p2.setPlayerId(UUID.randomUUID());
        p2.setUsername("bob");
        p2.setPassword("x");
        p2.setEmail("b@example.com");
        p2.setRole("ROLE_USER");

        // Provide SseEmitters so service can .send() without NPE
        when(emitterRegistry.getEmitter(p1.getPlayerId())).thenReturn(new SseEmitter(60_000L));
        when(emitterRegistry.getEmitter(p2.getPlayerId())).thenReturn(new SseEmitter(60_000L));

        // PlayerService lookups
        when(playerService.findPlayerById(p1.getPlayerId())).thenReturn(p1);
        when(playerService.findPlayerById(p2.getPlayerId())).thenReturn(p2);

        // Persist calls are no-ops in tests
        when(multiplayerGameRepository.save(any(MultiplayerGame.class))).thenAnswer(inv -> inv.getArgument(0));

        // Create a game with known state
        MultiplayerGame game = MultiplayerGame.builder()
                .gameId(UUID.randomUUID())
                .player1(p1)
                .player2(p2)
                .currentPlayerId(p1.getPlayerId())
                .winningNumber("0123")
                .difficulty(Difficulty.EASY)
                .mode(GameMode.MULTIPLAYER)
                .build();

        gameId = game.getGameId();

        // Register active game
        multiplayerGameService.activeGames.put(gameId, game);
    }

    @AfterEach
    void tearDown() {
        multiplayerGameService.activeGames.clear();
    }

    @Test
    @DisplayName("Submit method under concurrent load: correctness and basic performance metrics")
    void submitUnderLoad_collectsMetrics_andPreservesState() throws Exception {
        final int threads = 32;       // concurrency level
        final int totalCalls = 500;   // total submissions across all threads

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        List<Future<String>> futures = new ArrayList<>(totalCalls);

        AtomicInteger guessSeq = new AtomicInteger(0);
        List<Long> latencies = new CopyOnWriteArrayList<>();

        Instant start = Instant.now();

        for (int i = 0; i < totalCalls; i++) {
            futures.add(pool.submit(() -> {
                // Alternate between players intentionally to cause contention and turn rejections
                boolean useP1 = ThreadLocalRandom.current().nextBoolean();
                UUID pid = useP1 ? p1.getPlayerId() : p2.getPlayerId();

                // Generate 4-digit guess within EASY constraints (0-7)
                int n = guessSeq.getAndIncrement();
                String g = String.format("%d%d%d%d", (n) % 8, (n / 8) % 8, (n / 64) % 8, (n / 512) % 8);

                long t0 = System.nanoTime();
                String response = multiplayerGameService.submitMultiplayerGuess(gameId, pid, g);
                long t1 = System.nanoTime();
                latencies.add(TimeUnit.NANOSECONDS.toMillis(t1 - t0));
                return response;
            }));
        }

        // Gather results
        List<String> responses = new ArrayList<>(totalCalls);
        for (Future<String> f : futures) {
            responses.add(f.get(5, TimeUnit.SECONDS));
        }

        pool.shutdown();
        assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS), "Executor must terminate");

        Instant end = Instant.now();
        long totalMs = Duration.between(start, end).toMillis();

        MultiplayerGame game = multiplayerGameService.activeGames.get(gameId);
        assertNotNull(game);

        // Invariants:
        // - No more than 10 recorded guesses (loss threshold)
        // - All recorded guesses are unique by service rules
        int recorded = game.getGuesses().size();
        assertTrue(recorded <= 10, "At most 10 guesses should be recorded (loss threshold)");
        assertEquals(recorded, game.getGuesses().stream().map(g -> g.getGuess()).collect(Collectors.toSet()).size(),
                "Recorded guesses should be unique");

        // There should be many rejections due to turn enforcement or duplicates under contention
        long notYourTurn = responses.stream().filter(r -> r != null && r.contains("not your turn")).count();
        long duplicate = responses.stream().filter(r -> r != null && r.contains("We don't allow duplicate guesses here.")).count();
        long valid = responses.stream().filter(r -> r != null && r.contains("has ")).count(); // hint format
        assertEquals(recorded, valid, "Number of hints should equal guesses recorded");

        // Print basic performance metrics (does not assert on timing to avoid flakiness)
        printMetrics("submitUnderLoad_collectsMetrics_andPreservesState", threads, totalCalls, totalMs, latencies,
                recorded, notYourTurn, duplicate);
    }

    @Test
    @DisplayName("High-contention alternation still advances turns without deadlock")
    void highContention_turnsAdvance_withoutDeadlock() throws Exception {
        final int threads = 16;
        final int iterationsPerThread = 50;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch startGate = new CountDownLatch(1);
        List<Future<Void>> futures = new ArrayList<>();

        AtomicInteger guessSeq = new AtomicInteger(0);

        for (int t = 0; t < threads; t++) {
            futures.add(pool.submit(() -> {
                startGate.await();
                for (int i = 0; i < iterationsPerThread; i++) {
                    // alternate strictly by iteration parity to create heavy turn contention
                    UUID pid = (i % 2 == 0) ? p1.getPlayerId() : p2.getPlayerId();
                    int n = guessSeq.getAndIncrement();
                    String g = String.format("%d%d%d%d", (n) % 8, (n / 8) % 8, (n / 64) % 8, (n / 512) % 8);
                    try {
                        multiplayerGameService.submitMultiplayerGuess(gameId, pid, g);
                    } catch (Exception ex) {
                        fail("submitMultiplayerGuess threw exception under contention: " + ex.getMessage());
                    }
                }
                return null;
            }));
        }

        startGate.countDown();

        for (Future<Void> f : futures) {
            f.get(10, TimeUnit.SECONDS);
        }
        pool.shutdown();
        assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));

        MultiplayerGame game = multiplayerGameService.activeGames.get(gameId);
        assertNotNull(game);
        // The game should either have recorded up to 10 guesses and then be finished, or fewer if many duplicates
        assertTrue(game.getGuesses().size() <= 10);
        // No deadlock occurred if we reached here and executor terminated
        if (game.isFinished()) {
            assertTrue(game.getResult() != null);
        }
    }

    private void printMetrics(String testName,
                              int threads,
                              int totalCalls,
                              long totalMs,
                              List<Long> latencies,
                              int recorded,
                              long notYourTurn,
                              long duplicate) {
        List<Long> sorted = latencies.stream().sorted().toList();
        long p50 = percentile(sorted, 50);
        long p95 = percentile(sorted, 95);
        long p99 = percentile(sorted, 99);
        long max = sorted.isEmpty() ? 0 : sorted.get(sorted.size() - 1);
        double throughput = (totalMs > 0) ? (totalCalls * 1000.0 / totalMs) : 0.0;

        System.out.println("[DEBUG_LOG] Test: " + testName);
        System.out.println("[DEBUG_LOG] Threads=" + threads + ", totalCalls=" + totalCalls + ", durationMs=" + totalMs + ", throughput req/s=" + String.format("%.2f", throughput));
        System.out.println("[DEBUG_LOG] Latency ms: p50=" + p50 + ", p95=" + p95 + ", p99=" + p99 + ", max=" + max);
        System.out.println("[DEBUG_LOG] RecordedGuesses=" + recorded + ", NotYourTurnResponses=" + notYourTurn + ", DuplicateResponses=" + duplicate);
    }

    private long percentile(List<Long> sorted, int p) {
        if (sorted.isEmpty()) return 0;
        int idx = (int) Math.ceil((p / 100.0) * sorted.size()) - 1;
        idx = Math.max(0, Math.min(idx, sorted.size() - 1));
        return sorted.get(idx);
    }
}
