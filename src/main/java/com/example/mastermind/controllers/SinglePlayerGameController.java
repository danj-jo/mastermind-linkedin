package com.example.mastermind.controllers;

import com.example.mastermind.dataTransferObjects.GameDTOs.Request.NewGameRequest;
import com.example.mastermind.dataTransferObjects.GameDTOs.Response.GuessResponse;
import com.example.mastermind.models.entities.SinglePlayerGame;
import com.example.mastermind.models.entities.Player;
import com.example.mastermind.services.AuthService;
import com.example.mastermind.services.SingleplayerGameService;
import com.example.mastermind.services.PlayerService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.mastermind.customExceptions.UnauthenticatedUserException;
import com.example.mastermind.customExceptions.GameCreationException;
import com.example.mastermind.customExceptions.GuessProcessingException;
import com.example.mastermind.customExceptions.UnauthorizedGameAccessException;
import com.example.mastermind.customExceptions.GameUpdateException;
import com.example.mastermind.customExceptions.GameNotFoundException;

import java.util.*;

import static com.example.mastermind.services.AuthService.getCurrentAuthenticatedPlayerUsername;

/**
 * Controller for managing single-player game operations.
 * Handles creating a new game, submitting guesses, resuming games,
 * and retrieving details about guesses.
 * All endpoints produce and consume JSON where applicable.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/singleplayer/games")
public class SinglePlayerGameController {

    private final SingleplayerGameService singleplayerGameService;
    private final PlayerService playerService;
    private final AuthService authService;


    /**
     * Creates a new single-player game for the authenticated player.
     * <p>
     * - Retrieves the authenticated player from the security context.
     * - Converts the requested difficulty to match the enum values.
     * - Creates a new single-player game and stores the game ID in the HTTP session.
     * - Returns the number of numbers to guess, so the frontend can set up the game board.
     *
     * @param newGameRequest DTO containing information needed to start a new game, the difficulty
     * @return a ResponseEntity containing the number of numbers to guess on success,
     *         or an error message if game creation fails
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping(value = "/new", produces = "application/json")
    // Object is used instead of a int here beacyse the game id is a UUID, and int types aren't boxed (Integer for Maps)
    public ResponseEntity<Map<String,Object>> createNewSingleplayerGame(@RequestBody NewGameRequest newGameRequest) {
        try {

            Player player = authService.getCurrentAuthenticatedPlayer();
            UUID playerId = player.getPlayerId();

             // Difficulty is converted to uppercase to match the enum value of the corresponding difficulty.
            String difficulty = newGameRequest.getDifficulty().toUpperCase();

            // creates a singleplayer game with the return value of the createNewGame method
            SinglePlayerGame singlePlayerGame = singleplayerGameService.createNewGame(difficulty, playerId);
            int amountOfnumbersToGuess = singlePlayerGame.getWinningNumber().length();
            // stores the gameID to the session to be used later.
            UUID gameId = singlePlayerGame.getGameId();
            // respond with the number of numbers to guess to lay out the game board on the frontend.
            return new ResponseEntity<>(new HashMap<>(Map.of("numbersToGuess", amountOfnumbersToGuess, "gameId",gameId)), HttpStatus.CREATED);
        } catch (Exception e) {
            throw new GameCreationException(e.getMessage());
        }
    }

    /**
     * Processes a player's guess for the current single-player game.
     * <p>
     * - Retrieves the authenticated player from the security context.
     * - Validates that the player is logged in and has an active game session.
     * - Submits the guess to the game service and gets feedback.
     * - Returns the feedback and indicates whether the game is finished.
     * @return a ResponseEntity containing feedback on the guess and game status,
     *         or an error message if the player is not logged in or no game is active
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping(value = "{gameId}/guess", produces = "application/json")
    public ResponseEntity<GuessResponse> handleGuessSubmission(@PathVariable UUID gameId, @RequestBody Map<String,String> guess) {
        try {
            String username = getCurrentAuthenticatedPlayerUsername();
            Player currentPlayer = playerService.findPlayerByUsername(username);
            UUID currentPlayerId = currentPlayer.getPlayerId();
            if (currentPlayerId == null) {
                throw new UnauthenticatedUserException();
            }  
            SinglePlayerGame game = singleplayerGameService.findGameById(gameId);
            if (game.getPlayer().getPlayerId() != currentPlayerId) {
                throw new UnauthorizedGameAccessException("Error: User is not authorized to submit guesses to this game.");
            }
           
            String guessFeedback = singleplayerGameService.handleGuess(gameId, guess.get("guess"));
            boolean finished = singleplayerGameService.isGameFinished(gameId);
            Set<String> guesses = game.getGuesses();
            return ResponseEntity.ok(new GuessResponse(guessFeedback, guesses, finished));
        } catch (Exception e) {
            throw new GuessProcessingException(e.getMessage());
        }
    }

    /**
     * Updates an existing single-player game with a new guess.
     * <p>
     * - Accepts a request containing the game ID and the player's guess.
     * - Validates that the authenticated player is authorized to update the game.
     * - Submits the guess to the game service and retrieves feedback.
     * - Returns the feedback and indicates whether the game is finished.
     * <p>
     * This method has similar functionality to the guess submission endpoint,
     * but is intended for resuming or updating ongoing games.
     *
     * @param updatedGameRequest DTO containing the game ID and the player's guess
     * @return a ResponseEntity containing feedback on the guess and game status,
     *         or an error if the player is unauthorized or another issue occurs
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/singleplayer/games/{gameId}/resume")
    public ResponseEntity<?> resumeGame(@PathVariable UUID gameId, String guess) {
        try {
            String username = getCurrentAuthenticatedPlayerUsername();
            Player player = playerService.findPlayerByUsername(username);
            UUID playerId = player.getPlayerId();
            
            // Retrieve the game using the path variable
            SinglePlayerGame singlePlayerGame = singleplayerGameService.findGameById(gameId);
            if (singlePlayerGame.getPlayer().getPlayerId() != playerId) {
                throw new UnauthorizedGameAccessException();
            }
            String feedback = singleplayerGameService.handleGuess(singlePlayerGame.getGameId(),guess);
            Set<String> guesses = singlePlayerGame.getGuesses();
            boolean finished = singlePlayerGame.isFinished();
            // Ensure the current player owns this game
            
            return ResponseEntity.ok(new GuessResponse(feedback, guesses, finished));
            
        } catch (Exception e) {
            throw new GameUpdateException(e.getMessage());
        }
    }

    /**
     * Retrieves details of an existing single-player game by its ID.
     * <p>
     * - Uses the game ID provided by the client to find the corresponding game.
     * - Validates that the authenticated player is the owner of the game.
     * - Returns information needed by the frontend, specifically the number of numbers to guess,
     *   so the game board can be properly displayed.
     *
     * @param gameId the UUID of the game to retrieve
     * @return a ResponseEntity containing the number of numbers to guess if successful,
     *         or an error message if the game is not found or the player is unauthorized
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{gameId}")
    public ResponseEntity<?> findGameDetails(@PathVariable UUID gameId){
    try{


    String username = getCurrentAuthenticatedPlayerUsername();
    Player currentPlayer = playerService.findPlayerByUsername(username);
    SinglePlayerGame singlePlayerGame = singleplayerGameService.findGameById(gameId);
    UUID currentPlayerId = currentPlayer.getPlayerId();
    UUID idAssociatedWithGame = singlePlayerGame.getPlayer().getPlayerId();

    if(idAssociatedWithGame != currentPlayerId){
        throw new UnauthorizedGameAccessException();
    }
        return new ResponseEntity<>(new HashMap<>(Map.of("numbersToGuess", singlePlayerGame.getWinningNumber().length(), "guesses", singlePlayerGame.getGuesses())), HttpStatus.OK);
} catch(Exception e){
    throw new GameNotFoundException(e.getMessage());
}
}
}