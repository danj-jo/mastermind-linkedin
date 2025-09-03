package com.example.mastermind.controllers;

import com.example.mastermind.models.EmitterRegistry;
import com.example.mastermind.models.entities.Player;
import com.example.mastermind.services.MultiplayerGameService;
import com.example.mastermind.services.PlayerService;
import com.example.mastermind.utils.EmitterDiagnostics;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/multiplayer")
public class MultiplayerGameController {
    private final MultiplayerGameService multiplayerGameService;
    private final PlayerService playerService;
    private final EmitterRegistry emitterRegistry;
    private final EmitterDiagnostics emitterDiagnostics;
    private final SimpMessagingTemplate messagingTemplate;
    private static final Logger logger = LoggerFactory.getLogger(MultiplayerGameController.class);
    // when I call create game here, I simply add the player to the queue. when the player is added, i send the game to the active players map. So, I when I open a connection, I can simply check if activeGames[Game] contains the player's id, and if it also has two ids. If it does, I can tell the user they've been added, and send them to the w/s.

    // sse stops once connection is closed -> close connection once we get a match.


    @PreAuthorize("hasRole('USER')")
    @GetMapping(value = "join", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter joinGame(@RequestParam String difficulty,Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Unauthenticated access");
        }
        String username = auth.getName();
        Player player = playerService.findByUsername(username);
        if (player == null) {
            throw new RuntimeException("Player not found for username: " + username);
        }

        // Register emitter early
        long timeout = 60 * 60 * 1000L;
        SseEmitter emitter = new SseEmitter(timeout); // or a timeout value
        emitterRegistry.addEmitter(player.getPlayerId(), emitter);

        emitter.onCompletion(() -> emitterRegistry.removeEmitter(player.getPlayerId()));
        emitter.onTimeout(() -> emitterRegistry.removeEmitter(player.getPlayerId()));
        emitter.onError((ex) -> emitterRegistry.removeEmitter(player.getPlayerId()));

        // Optional: send ping to confirm readiness
        try {
            emitter.send(SseEmitter.event().name("ping").data("ready"));
        } catch (IOException e) {
            logger.warn("Failed to send ping to emitter for {}", player.getPlayerId(), e);
        }
       multiplayerGameService.joinGame(player, difficulty);

        return emitter;
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("{id}")
    public ResponseEntity<?> returnGameDetails(@PathVariable UUID id){
        try {
            return new ResponseEntity<>(multiplayerGameService.findMultiplayerGameDetails(id), HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>(new HashMap<>(Map.of("Error",e.getMessage())),HttpStatus.BAD_REQUEST);
        }

    }


}
