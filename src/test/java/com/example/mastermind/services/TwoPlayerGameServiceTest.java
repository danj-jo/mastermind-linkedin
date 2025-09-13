package com.example.mastermind.services;

import com.example.mastermind.customExceptions.GameNotFoundException;
import com.example.mastermind.customExceptions.PlayerNotFoundException;
import com.example.mastermind.dataTransferObjects.GameDTOs.multiplayer.GameAssignmentData;
import com.example.mastermind.models.Difficulty;
import com.example.mastermind.models.GameMode;
import com.example.mastermind.models.Result;
import com.example.mastermind.models.entities.MultiplayerGame;
import com.example.mastermind.models.entities.MultiplayerGuess;
import com.example.mastermind.models.entities.Player;
import com.example.mastermind.repositoryLayer.MultiplayerGameRepository;
import com.example.mastermind.repositoryLayer.PlayerRepository;
import com.example.mastermind.utils.EmitterDiagnostics;
import com.example.mastermind.utils.EmitterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MultiplayerGameServiceTest {

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

    @Mock
    private SseEmitter sseEmitter;

    @InjectMocks
    private MultiplayerGameService multiplayerGameService;

    private Player player1;
    private Player player2;
    private MultiplayerGame testGame;

    @BeforeEach
    void setUp() {
        player1 = new Player(
                UUID.randomUUID(),
                "player1",
                "password1",
                "player1@example.com",
                "USER"
        );
        
        player2 = new Player(
                UUID.randomUUID(),
                "player2",
                "password2",
                "player2@example.com",
                "USER"
        );

        testGame = MultiplayerGame.builder()
                .gameId(UUID.randomUUID())
                .player1(player1)
                .player2(player2)
                .currentPlayerId(player1.getPlayerId())
                .winningNumber("1234")
                .difficulty(Difficulty.EASY)
                .mode(GameMode.MULTIPLAYER)
                .result(Result.PENDING)
                .finished(false)
                .guesses(new HashSet<>())
                .build();
    }

    @Test
    void joinMultiplayerGame_WithTwoPlayers_CreatesGameAndNotifiesPlayers() throws IOException {
        // Given
        when(playerService.findPlayerById(player1.getPlayerId())).thenReturn(player1);
        when(playerService.findPlayerById(player2.getPlayerId())).thenReturn(player2);
        when(emitterRegistry.getEmitter(player1.getPlayerId())).thenReturn(sseEmitter);
        when(emitterRegistry.getEmitter(player2.getPlayerId())).thenReturn(sseEmitter);

        try (MockedStatic<com.example.mastermind.utils.GameUtils> mockedUtils = Mockito.mockStatic(com.example.mastermind.utils.GameUtils.class)) {
            mockedUtils.when(() -> com.example.mastermind.utils.GameUtils.selectUserDifficulty("EASY")).thenReturn(Difficulty.EASY);
            mockedUtils.when(() -> com.example.mastermind.utils.GameUtils.generateWinningNumber(Difficulty.EASY)).thenReturn("1234");

            // When
            multiplayerGameService.joinMultiplayerGame(player1.getPlayerId(), "EASY");
            multiplayerGameService.joinMultiplayerGame(player2.getPlayerId(), "EASY");

            // Then
            verify(emitterDiagnostics).logMatchAttempt(player1, player2);
            verify(sseEmitter, times(2)).send(any(SseEmitter.SseEventBuilder.class));
            assertEquals(1, multiplayerGameService.activeGames.size());
        }
    }

    @Test
    void findMultiplayerGameDetails_WithExistingGame_ReturnsGameDetails() {
        // Given
        multiplayerGameService.activeGames.put(testGame.getGameId(), testGame);

        // When
        Map<String, Object> result = multiplayerGameService.findMultiplayerGameDetails(testGame.getGameId());

        // Then
        assertNotNull(result);
        assertEquals(4, result.get("numbersToGuess"));
    }

    @Test
    void findMultiplayerGameDetails_WithNonExistentGame_ThrowsException() {
        // Given
        UUID nonExistentGameId = UUID.randomUUID();

        // When & Then
        assertThrows(GameNotFoundException.class, () -> 
                multiplayerGameService.findMultiplayerGameDetails(nonExistentGameId));
    }

    @Test
    void submitMultiplayerGuess_WithValidGuess_ReturnsHint() {
        // Given
        multiplayerGameService.activeGames.put(testGame.getGameId(), testGame);
        when(playerService.findPlayerById(player1.getPlayerId())).thenReturn(player1);

        // When
        String result = multiplayerGameService.submitMultiplayerGuess(testGame.getGameId(), player1.getPlayerId(), "1234");

        // Then
        assertNotNull(result);
        assertTrue(result.contains("numbers correct"));
        assertEquals(player2.getPlayerId(), testGame.getCurrentPlayerId());
    }

    @Test
    void submitMultiplayerGuess_WithWinningGuess_ReturnsVictoryMessage() {
        // Given
        multiplayerGameService.activeGames.put(testGame.getGameId(), testGame);
        when(playerService.findPlayerById(player1.getPlayerId())).thenReturn(player1);

        // When
        String result = multiplayerGameService.submitMultiplayerGuess(testGame.getGameId(), player1.getPlayerId(), "1234");

        // Then
        assertTrue(result.contains("Victory"));
        assertTrue(testGame.isFinished());
        assertEquals(Result.WIN, testGame.getResult());
        verify(multiplayerGameRepository).save(testGame);
    }

    @Test
    void submitMultiplayerGuess_WithWrongTurn_ReturnsNotYourTurnMessage() throws IOException {
        // Given
        testGame.setCurrentPlayerId(player2.getPlayerId());
        multiplayerGameService.activeGames.put(testGame.getGameId(), testGame);
        when(playerService.findPlayerById(player1.getPlayerId())).thenReturn(player1);
        when(emitterRegistry.getEmitter(player1.getPlayerId())).thenReturn(sseEmitter);

        // When
        String result = multiplayerGameService.submitMultiplayerGuess(testGame.getGameId(), player1.getPlayerId(), "1234");

        // Then
        assertEquals("It's not your turn.", result);
        verify(sseEmitter).send(any(SseEmitter.SseEventBuilder.class));
    }

    @Test
    void submitMultiplayerGuess_WithInvalidGuess_ReturnsValidationMessage() {
        // Given
        multiplayerGameService.activeGames.put(testGame.getGameId(), testGame);
        when(playerService.findPlayerById(player1.getPlayerId())).thenReturn(player1);

        // When
        String result = multiplayerGameService.submitMultiplayerGuess(testGame.getGameId(), player1.getPlayerId(), "abc");

        // Then
        assertEquals("Guesses are numbers only", result);
    }

    @Test
    void submitMultiplayerGuess_WithDuplicateGuess_ReturnsDuplicateMessage() {
        // Given
        multiplayerGameService.activeGames.put(testGame.getGameId(), testGame);
        when(playerService.findPlayerById(player1.getPlayerId())).thenReturn(player1);
        
        // Add existing guess
        MultiplayerGuess existingGuess = new MultiplayerGuess();
        existingGuess.setGuess("1234");
        testGame.getGuesses().add(existingGuess);

        // When
        String result = multiplayerGameService.submitMultiplayerGuess(testGame.getGameId(), player1.getPlayerId(), "1234");

        // Then
        assertEquals("We don't allow duplicate guesses here.", result);
    }

    @Test
    void submitMultiplayerGuess_WithNonExistentGame_ThrowsException() {
        // Given
        UUID nonExistentGameId = UUID.randomUUID();

        // When & Then
        assertThrows(GameNotFoundException.class, () -> 
                multiplayerGameService.submitMultiplayerGuess(nonExistentGameId, player1.getPlayerId(), "1234"));
    }

    @Test
    void getFinishedMultiplayerGamesByPlayerId_WithValidPlayer_ReturnsGameHistory() {
        // Given
        when(playerRepository.existsById(player1.getPlayerId())).thenReturn(true);
        when(multiplayerGameRepository.findFinishedGames(player1.getPlayerId())).thenReturn(List.of(testGame));

        // When
        List<com.example.mastermind.models.PastGame> result = multiplayerGameService.getFinishedMultiplayerGamesByPlayerId(player1.getPlayerId());

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testGame.getGameId().toString(), result.get(0).getGameId());
    }

    @Test
    void getFinishedMultiplayerGamesByPlayerId_WithNonExistentPlayer_ThrowsException() {
        // Given
        UUID nonExistentPlayerId = UUID.randomUUID();
        when(playerRepository.existsById(nonExistentPlayerId)).thenReturn(false);

        // When & Then
        assertThrows(PlayerNotFoundException.class, () -> 
                multiplayerGameService.getFinishedMultiplayerGamesByPlayerId(nonExistentPlayerId));
    }
}