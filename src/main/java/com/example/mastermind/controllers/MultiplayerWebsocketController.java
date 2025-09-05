package com.example.mastermind.controllers;

import com.example.mastermind.dataTransferObjects.GameDTOs.multiplayer.MultiplayerTurnMetadata;
import com.example.mastermind.models.Result;
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
import java.util.Map;
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
     * @param gameId - the game ID of the current game. It is sent to the appropriate topic.
     * @param userGuess - the guess submitted by the user
     * @param auth - the authenticated user
     * @return information about the current game: who guessed, the feedback given, completion status, and each guess associated.
     */
    @SendTo("/topic/mp")
    @MessageMapping("/multiplayer/{gameId}/guess")
    public MultiplayerTurnMetadata submitGuess(@DestinationVariable String gameId, @Payload Map<String,String> userGuess, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthenticatedUserException("User is not authenticated");
        }
        String username = auth.getName();
        Player player = playerService.findPlayerByUsername(username);
        UUID gameID = UUID.fromString(gameId);
        MultiplayerGame game = multiplayerGameService.activeGames.get(gameID);
        if (game == null) {
            throw new GameNotFoundException("Game not found for id: " + gameId);
        }
        String guess = userGuess.get("guess");

        String feedback = game.submitGuess(player, guess);
        List<String> guesses = game.getGuesses().stream().map(MultiplayerGuess::getGuess).toList();
        boolean finished = game.isFinished();

        return new MultiplayerTurnMetadata(player.getUsername(), feedback, finished, guesses);
    }
}
