package com.example.mastermind.controllers;

import com.example.mastermind.dataTransferObjects.GameDTOs.multiplayer.MultiplayerGuessSubmission;
import com.example.mastermind.dataTransferObjects.GameDTOs.multiplayer.MultiplayerTurnMetadata;
import com.example.mastermind.models.entities.MultiplayerGame;
import com.example.mastermind.models.entities.MultiplayerGuess;
import com.example.mastermind.models.entities.Player;
import com.example.mastermind.services.MultiplayerGameService;
import com.example.mastermind.services.PlayerService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import java.util.List;
import java.util.UUID;
import com.example.mastermind.customExceptions.UnauthenticatedUserException;
import com.example.mastermind.customExceptions.GameNotFoundException;

@Controller
@AllArgsConstructor
public class MultiplayerWebsocketController {

    private final MultiplayerGameService multiplayerGameService;
    private final PlayerService playerService;


    /**
     *
     * @param gameId - The game ID of the current game. This ensures that messages are published to the correct endpoint.
     * @param guessSubmission - DTO that represents the guess coming from the user. Its fields are playerID, which represents the player who is currently guessing, and the guess itself.
     * @param auth - the current authenticated user
     * @return information about the current game: who guessed, the feedback given, completion status, and each guess associated.
     */
    @SendTo("/topic/mp")
    @MessageMapping("/multiplayer/{gameId}/guess")
    public MultiplayerTurnMetadata submitGuess(@DestinationVariable String gameId, @Payload MultiplayerGuessSubmission guessSubmission) {
        Player player = playerService.findPlayerById(guessSubmission.getPlayerId());
        UUID gameID = UUID.fromString(gameId);
        MultiplayerGame game = multiplayerGameService.activeGames.get(gameID);
        if (game == null) {
            throw new GameNotFoundException("Game not found for id: " + gameId);
        }
        String guess = guessSubmission.getGuess();
        UUID playerId = guessSubmission.getPlayerId();


        String feedback = multiplayerGameService.submitMultiplayerGuess(gameID,playerId,guess);
        List<String> guesses = game.getGuesses().stream().map(MultiplayerGuess::getGuess).toList();
        boolean finished = game.isFinished();
        return new MultiplayerTurnMetadata(player.getUsername(), feedback, finished, guesses);
    }
}