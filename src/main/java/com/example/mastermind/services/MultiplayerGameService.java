package com.example.mastermind.services;

import com.example.mastermind.dataTransferObjects.GameDTOs.multiplayer.GameAssignmentData;
import com.example.mastermind.models.GameMode;
import com.example.mastermind.models.Result;
import com.example.mastermind.models.entities.MultiplayerGuess;
import com.example.mastermind.repositoryLayer.MultiplayerGameRepository;
import com.example.mastermind.models.Difficulty;
import com.example.mastermind.utils.EmitterRegistry;
import com.example.mastermind.models.entities.MultiplayerGame;
import com.example.mastermind.models.entities.Player;
import com.example.mastermind.utils.EmitterDiagnostics;
import com.example.mastermind.utils.GameUtils;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import com.example.mastermind.customExceptions.GameNotFoundException;

/**
 * Service for managing multiplayer Mastermind games and player matchmaking.
 * <p>
 * This service handles the complete lifecycle of multiplayer games including:
 * - Player queue management by difficulty level
 * - Automatic game creation when two players are matched
 * - Real-time game state tracking for active multiplayer sessions
 * - Server-Sent Events (SSE) for player communication
 * - Game completion and cleanup
 * <p>
 * The service uses concurrent data structures to ensure thread safety:
 * - ConcurrentLinkedQueue for player waiting lists (thread-safe queues)
 * - ConcurrentHashMap for active games (prevents race conditions)
 * - Synchronized blocks for queue operations (prevents double-polling)
 * <p>
 * Games are stored in memory for real-time performance and only persisted to
 * the database upon completion.
 */
@Service
@Component
@AllArgsConstructor
public class MultiplayerGameService {
    private final MultiplayerGameRepository multiplayerGameRepository;
    private static final Logger logger = LoggerFactory.getLogger(MultiplayerGameService.class);
    private final EmitterDiagnostics emitterDiagnostics;
    private final EmitterRegistry emitterRegistry;
    private final Queue<UUID> playersWaitingForEasyGame = new ConcurrentLinkedQueue<>();
    private final Queue<UUID> playersWaitingForMediumGame = new ConcurrentLinkedQueue<>();
    private final Queue<UUID> playersWaitingForHardGame = new ConcurrentLinkedQueue<>();
    //storing each queue in a map makes them easier and cleaner to iterate through. This could be done with if statements, but would make code cluttered.
    private final Map<String,Queue<UUID>> waitingPlayerMap = new ConcurrentHashMap<>(Map.of(
            "EASY",playersWaitingForEasyGame,
            "MEDIUM", playersWaitingForMediumGame,
            "HARD",playersWaitingForHardGame
    ));
    public final Map<UUID, MultiplayerGame> activeGames = new ConcurrentHashMap<>();
    private final PlayerService playerService;

    /**
     * Adds a player to the waiting queue for their chosen difficulty level.
     * <p>
     * When a player joins, they are added to the appropriate difficulty queue.
     * If the queue reaches 2 or more players, a new multiplayer game is automatically
     * created and both players are removed from the queue. The game is then added
     * to the active games map, and both players receive "matched" events via SSE.
     *
     * @param playerId the player joining the multiplayer queue. They enter via playerID to ensure uniqueness.
     * @param difficulty the difficulty level for the game (EASY, MEDIUM, HARD)
     */
    public void joinMultiplayerGame(UUID playerId, String difficulty){
        for(String difficultyLevel: waitingPlayerMap.keySet()){
            Queue<UUID> playerQueue = waitingPlayerMap.get(difficultyLevel);
            synchronized (playerQueue){
                if(difficultyLevel.equalsIgnoreCase(difficulty) && !playerQueue.contains(playerId)){
                    playerQueue.add(playerId);
                }
            }
        }

        waitingPlayerMap.forEach((key, value) -> {
            synchronized (value){
                if (value.size() >= 2){
                UUID playerOne = value.poll();
                UUID playerTwo = value.poll();
                Player playerOneObject = playerService.findPlayerById(playerOne);
                Player playerTwoObject = playerService.findPlayerById(playerTwo);
                assert playerTwo != null;
                MultiplayerGame game = MultiplayerGame.builder()
                                                      .difficulty(GameUtils.selectUserDifficulty(key))
                                                      .winningNumber(GameUtils.generateWinningNumber(Difficulty.valueOf(difficulty.toUpperCase())))
                                                      .player1(playerOneObject)
                                                      .player2(playerTwoObject)
                                                      .mode(GameMode.MULTIPLAYER)
                                                      .build();

                activeGames.put(game.getGameId(),game);
                emitterDiagnostics.logMatchAttempt(playerOneObject, playerTwoObject);
                // get emitter for player 1 and send them a "matched" event
                try {
                    emitterRegistry.getEmitter(playerOne)
                                   .send(SseEmitter.event()
                                                   .name("matched")
                                                   .data(new GameAssignmentData(game.getGameId(),playerOne)));
                }
                catch(Exception e) {
                    // remove emitter from player 1 to prevent the storage of stale emitters.
                    emitterRegistry.removeEmitter(playerOne);
                    logger.error("Player 1's emitter disconnected. Removed.");
                    try {
                        // if player 1 disconnects, emit this event and tell player 2.
                        emitterRegistry.getEmitter(playerTwo).send(SseEmitter.event().name("disconnect").data("Player 1 has disconnected."));
                    } catch (IOException ey) {
                        logger.error("Player 1 was also disconnected. No notification sent.");
                    }

                }
                try {
                    emitterRegistry.getEmitter(playerTwo).send(SseEmitter.event().name("matched").data(new GameAssignmentData(game.getGameId(),playerTwo)));
                } catch (IOException e) {
                    // remove emitter from player 2 to prevent the storage of stale emitters.
                    emitterRegistry.removeEmitter(playerTwo);
                    logger.error("Player 2's emitter disconnected. Removed.");
                    try {
                        // if player 2 is disconnected, send this event and tell player 1.
                        emitterRegistry.getEmitter(playerOne).send(SseEmitter.event().name("disconnect").data("Player 2 has disconnected."));
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

    public String submitMultiplayerGuess(UUID gameId, UUID playerId, String guess) {
        MultiplayerGame game = activeGames.get(gameId);
        Player currentPlayer = playerService.findPlayerById(playerId);
        MultiplayerGuess newGuess = new MultiplayerGuess();
        // links guess to this game
        newGuess.setGame(game);
        // links guess to player
        newGuess.setPlayer(currentPlayer);
        // the guess itself
        newGuess.setGuess(guess);
        synchronized (game) {
            if (game.getDifficulty() == Difficulty.EASY && game.guessIsOverLimit(guess)) {
                return "Only numbers 0-7 are allowed. Please try again.";
            }

            if (game.guessContainsInvalidCharacters(guess)) {
                return "Guesses are numbers only";
            }

            if (game.inappropriateLength(guess)) {
                return String.format(
                        "Guess is not the appropriate length. Please try again. Guess must be %d numbers",
                        game.getWinningNumber()
                            .length()
                );
            }
            if (game.guessAlreadyExists(guess)) {
                return "We don't allow duplicate guesses here.";
            }

            if (game.isFinished()) {  // guard for already finished
                return "Game is finished.";
            }
            if (game.userLostGame()) {
                game.setFinished(true);
                game.setResult(Result.LOSS);
                multiplayerGameRepository.save(game);
                return String.format("Game Over! The correct number was: %s", game.getWinningNumber());
            }
            game.getGuesses()
                .add(newGuess);
            if (game.userWonGame(guess)) {
                game.setFinished(true);
                game.setResult(Result.WIN);
                multiplayerGameRepository.save(game);
                return String.format("Victory! %s gave the winning guess.", currentPlayer);
            }
            return game.generateHint(currentPlayer,guess);
        }

    }

}