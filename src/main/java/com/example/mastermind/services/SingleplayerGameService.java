package com.example.mastermind.services;

import com.example.mastermind.models.PastGame;
import com.example.mastermind.repositoryLayer.SingleplayerGameRepository;
import com.example.mastermind.repositoryLayer.PlayerRepository;
import com.example.mastermind.models.Difficulty;
import com.example.mastermind.models.entities.SinglePlayerGame;
import com.example.mastermind.models.entities.Player;
import com.example.mastermind.utils.GameUtils;
import lombok.AllArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.example.mastermind.customExceptions.GameNotFoundException;
import com.example.mastermind.customExceptions.PlayerDataAccessException;
import com.example.mastermind.customExceptions.PlayerNotFoundException;

import java.util.*;

import org.slf4j.Logger;
/**
 * Service class for managing single-player Mastermind games.
 * <p>
 * This service handles the creation, management, and gameplay logic for single-player games,
 * including game creation, guess submission, and game state persistence. It provides
 * methods for retrieving game history and managing game lifecycle.
 * <p>
 * Note: All exceptions thrown by methods in this service are automatically handled 
 * by the GlobalExceptionHandler, which converts them to appropriate HTTP responses 
 * with status codes and error messages when called from controllers.
 * 
 */

@Service
@Component
@AllArgsConstructor
public class SingleplayerGameService {
    private final SingleplayerGameRepository singleplayerGameRepository;
    private final PlayerRepository playerRepository;
    private static final Logger logger = LoggerFactory.getLogger(SingleplayerGameService.class);
    private final PlayerService playerService;


    /**
     * Creates a new single-player game for the specified player.
     * <p>
     * This method initializes a new game by first validating that the player exists,
     * then creating a new SinglePlayerGame instance with the specified difficulty level.
     * The game is configured with the requesting player, a randomly generated winning number
     * based on the difficulty, and an empty list to store future guesses. The game is
     * persisted to the database to allow for later resumption.
     * 
     * @param playerDifficulty the difficulty level for the game (e.g., "EASY", "MEDIUM", "HARD")
     * @param playerId the unique identifier of the player creating the game
     * @return the newly created SinglePlayerGame instance
     * @throws UsernameNotFoundException if no player is found with the specified player ID
     */
    public SinglePlayerGame createNewGame(String playerDifficulty, UUID playerId){

        Player player = playerRepository.findById(playerId).orElseThrow(() -> new UsernameNotFoundException("Player Not found."));
        SinglePlayerGame singlePlayerGame = new SinglePlayerGame();

        singlePlayerGame.setDifficulty(GameUtils.selectUserDifficulty(playerDifficulty));
        singlePlayerGame.setPlayer(player);
        singlePlayerGame.setWinningNumber(GameUtils.generateWinningNumber(Difficulty.valueOf(playerDifficulty)));
        singlePlayerGame.setGuesses(new ArrayList<>());

        singleplayerGameRepository.saveAndFlush(singlePlayerGame);
        return singlePlayerGame;

    }
    /**
     * Submits a guess for a single-player game and returns feedback.
     * <p>
     * This method processes a player's guess by finding the game using the provided game ID,
     * applying the guess to the game, and persisting the updated game state to the database.
     * The method returns feedback indicating how close the guess is to the winning number.
     * 
     * @param gameId the unique identifier of the game to submit the guess to
     * @param guess the player's guess as a string representation
     * @return a string containing feedback about the guess (e.g., "2 correct, 1 in wrong position") as well as the completion status of the game.
     * @throws GameNotFoundException if no game is found with the specified game ID
     */
    public String submitGuess(UUID gameId, String guess){
        SinglePlayerGame currentGame = singleplayerGameRepository.findById(gameId)
                                                                 .orElseThrow(() -> new GameNotFoundException("Game not found"));
        String feedback = currentGame.submitGuess(guess);

        singleplayerGameRepository.saveAndFlush(currentGame);
        return feedback;
    }

    /**
     * Retrieves all past games (both finished and unfinished) for a specific player.
     * <p>
     * This method queries the database to find all games associated with the given player ID,
     * separating them into two categories: finished games and unfinished games. The results
     * are organized into a map structure where finished games contain complete game data
     * including results, while unfinished games show partial information with a placeholder
     * message for the winning number until completion.
     * 
     * @param playerId - the unique identifier of the player whose games to retrieve
     * @return a Map containing two lists: "finished" games with games that are finished and "unfinished" 
     *         games for games that are not finished, both stored as CurrentUserPastGames objects
     * @throws PlayerDataAccessException if there's an error accessing the player's game data
*/

    public List<PastGame> getFinishedGamesByPlayerId(UUID playerId){
        if (!playerRepository.existsById(playerId)) {
            throw new PlayerNotFoundException("Player not found with ID: " + playerId);
        }
            // Find finished games and map to DTOs
            return singleplayerGameRepository.findFinishedGames(playerId).stream().map((singlePlayerGame -> {
                                                                                       return new PastGame(singlePlayerGame.getGameId()
                                                                                                                           .toString(),
                                                                                                           String.valueOf(singlePlayerGame.getDifficulty()),
                                                                                                           String.valueOf(singlePlayerGame.getResult()),
                                                                                                           singlePlayerGame.getWinningNumber(),
                                                                                                           singlePlayerGame.getGuesses()
                                                                                                                            .toString()
                                                                                                          );
                                                                                                            })).toList();
    }
    public List<PastGame> getUnfinishedGamesByPlayerId(UUID playerId){
        if (!playerRepository.existsById(playerId)) {
            throw new PlayerNotFoundException("Player not found with ID: " + playerId);
        }
        // Find finished games and map to DTOs
        return singleplayerGameRepository.findUnfinishedGames(playerId).stream().map((singlePlayerGame -> {
            return new PastGame(singlePlayerGame.getGameId()
                                                .toString(),
                                String.valueOf(singlePlayerGame.getDifficulty()),
                                String.valueOf(singlePlayerGame.getResult()),
                                "You must finish game to see results!",
                                singlePlayerGame.getGuesses()
                                                 .toString());
        })).toList();
    }

    public SinglePlayerGame findGameById(UUID gameId){
      return singleplayerGameRepository.findGameByGameId(gameId).orElseThrow(() -> new GameNotFoundException("Game not found."));
    }

    public boolean isGameFinished(UUID gameId){
        return singleplayerGameRepository.existsByGameIdAndIsFinishedTrue(gameId);
    }

}
