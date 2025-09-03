package com.example.mastermind.controllers;

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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class MultiplayerWebsocketController {

    private final MultiplayerGameService multiplayerGameService;
    private final PlayerService playerService;
    private final SimpMessagingTemplate messagingTemplate;

    // Client sends to /app/game/{gameId}/guess with payload {"guess":"1234"}

    @SendTo("/topic/mp")
    @MessageMapping("/multiplayer/{gameId}/guess")
    public Map<String,Object> submitGuess(@DestinationVariable String gameId, @Payload Map<String,String> userGuess, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return new HashMap<>(Map.of("Error", "Unauthenticated"));
        }
        String username = auth.getName();
        Player player = playerService.findByUsername(username);
        UUID gameID = UUID.fromString(gameId);
        MultiplayerGame game = multiplayerGameService.activeGames.get(gameID);
        if (game == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("type", "error");
            err.put("message", "Game not found");
           return err;
        }
        String guess = userGuess.get("guess");
        String feedback = game.submitGuess(player, guess);
        List<String> guesses = game.getGuesses().stream().map(MultiplayerGuess::getGuess).toList();
        boolean finished = game.isFinished();
        Map<String, Object> turnMetadata = new HashMap<>();
        turnMetadata.put("player", player.getUsername());
        turnMetadata.put("feedback", feedback);
        turnMetadata.put("finished", finished);
        turnMetadata.put("guesses", guesses);
        turnMetadata.put("winningNumber", game.getWinningNumber());
        System.out.println(game.getWinningNumber());
        return turnMetadata;
    }
}
