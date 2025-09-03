package com.example.mastermind.services;

import com.example.mastermind.models.Difficulty;
import com.example.mastermind.utils.EmitterRegistry;
import com.example.mastermind.models.entities.MultiplayerGame;
import com.example.mastermind.models.entities.Player;
import com.example.mastermind.utils.EmitterDiagnostics;
import com.example.mastermind.utils.GameUtils;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import com.example.mastermind.customExceptions.GameNotFoundException;


/**
 * Service for managing multiplayer Mastermind games and player matchmaking.
 * 
 * This service handles the complete lifecycle of multiplayer games including:
 * - Player queue management by difficulty level
 * - Automatic game creation when two players are matched
 * - Real-time game state tracking for active multiplayer sessions
 * - Server-Sent Events (SSE) for player communication
 * - Game completion and cleanup
 * 
 * The service uses concurrent data structures to ensure thread safety:
 * - ConcurrentLinkedQueue for player waiting lists (thread-safe queues)
 * - ConcurrentHashMap for active games (prevents race conditions)
 * - Synchronized blocks for queue operations (prevents double-polling)
 * 
 * Games are stored in memory for real-time performance and only persisted to
 * the database upon completion.
 */
@Service
@AllArgsConstructor
public class MultiplayerGameService {

    private static final Logger logger = LoggerFactory.getLogger(MultiplayerGameService.class);
    private final EmitterDiagnostics emitterDiagnostics;
    private final EmitterRegistry emitterRegistry;
    private final Queue<Player> playersWaitingForEasyGame = new ConcurrentLinkedQueue<>();
    private final Queue<Player> playersWaitingForMediumGame = new ConcurrentLinkedQueue<>();
    private final Queue<Player> playersWaitingForHardGame = new ConcurrentLinkedQueue<>();
     //storing each queue in a map makes them easier and cleaner to iterate through. This could be done with if statements, but would make code cluttered.
    private final Map<String,Queue<Player>> waitingPlayerQueue = new ConcurrentHashMap<>(Map.of(
            "EASY",playersWaitingForEasyGame,
            "MEDIUM", playersWaitingForMediumGame,
            "HARD",playersWaitingForHardGame
    ));
    public final Map<UUID, MultiplayerGame> activeGames = new ConcurrentHashMap<>();

    /**
     * Adds a player to the waiting queue for their chosen difficulty level.
     * 
     * When a player joins, they are added to the appropriate difficulty queue.
     * If the queue reaches 2 or more players, a new multiplayer game is automatically
     * created and both players are removed from the queue. The game is then added
     * to the active games map and both players receive "matched" events via SSE.
     * 
     * @param player the player joining the multiplayer queue
     * @param difficulty the difficulty level for the game (EASY, MEDIUM, HARD)
     */
    public void joinMultiplayerGame(Player player, String difficulty){
// add the player to the guess queue that corresponds with their difficulty.
        for(String difficultyLevel: waitingPlayerQueue.keySet()){
            Queue<Player> playerQueue = waitingPlayerQueue.get(difficultyLevel);
            synchronized (playerQueue){
                if(difficultyLevel.equalsIgnoreCase(difficulty) && !playerQueue.contains(player)){
                    playerQueue.add(player);
                }
            }
        }

        waitingPlayerQueue.forEach((key, value) -> {
            /*
             * Synchronize to prevent race conditions when multiple players join simultaneously.
 Without synchronization, two players could enter at the same time and each poll
    2 users, leaving errors and empty lists on both ends.
             */
           synchronized (value){if(value.size() >= 2){
                Player player1 = value.poll();
                Player player2 = value.poll();
                assert player2 != null;
                List<Player> players = new ArrayList<>(List.of(player1, player2));
                MultiplayerGame game = new MultiplayerGame();
                game.setDifficulty(GameUtils.selectUserDifficulty(key));
                game.setWinningNumber(GameUtils.generateWinningNumber(Difficulty.valueOf(difficulty.toUpperCase())));
                game.setPlayers(players);
                activeGames.put(game.getGameId(),game);
                emitterDiagnostics.logMatchAttempt(player1, player2);
                // get emitter for player 1 and send them a "matched" event
                try {
                    emitterRegistry.getEmitter(player1.getPlayerId())
                                   .send(SseEmitter.event()
                                                    .name("matched")
                                                   .data(game.getGameId()));
                }
                catch(Exception e) {
                    // remove emitter from player 1 to prevent the storage of stale emitters.
                    emitterRegistry.removeEmitter(player1.getPlayerId());
                    logger.error("Player 1's emitter disconnected. Removed.");
                        try {
                            // if player 1 disconnects, emit this event and tell player 2.
                            emitterRegistry.getEmitter(player2.getPlayerId()).send(SseEmitter.event().name("disconnect").data("Player 1 has disconnected."));
                        } catch (IOException ey) {
                            logger.error("Player 1 was also disconnected. No notification sent.");
                        }

                }
                try {
                    emitterRegistry.getEmitter(player2.getPlayerId()).send(SseEmitter.event().name("matched").data(game.getGameId()));
                    System.out.println(player1.getPlayerId());
                } catch (IOException e) {
                     // remove emitter from player 2 to prevent the storage of stale emitters.
                    emitterRegistry.removeEmitter(player2.getPlayerId());
                    logger.error("Player 2's emitter disconnected. Removed.");
                    try {
                        // if player 2 is disconnected, send this event and tell player 1.
                        emitterRegistry.getEmitter(player1.getPlayerId()).send(SseEmitter.event().name("disconnect").data("Player 2 has disconnected."));
                    } catch (IOException ex) {
                        logger.error("player 1 is also disconnected.");
                    }
                }
            }}
        });
    }

    public Map<String,Object> findMultiplayerGameDetails(UUID id){
        MultiplayerGame game = activeGames.get(id);
        if (game == null) {
            throw new GameNotFoundException("Multiplayer game not found for id: " + id);
        }
        String winningNumber = game.getWinningNumber();
        return new HashMap<>(Map.of("numbersToGuess", winningNumber.length()));
    }
}
