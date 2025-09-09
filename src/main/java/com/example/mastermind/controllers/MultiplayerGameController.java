package com.example.mastermind.controllers;

import com.example.mastermind.services.AuthService;
import com.example.mastermind.utils.EmitterRegistry;
import com.example.mastermind.models.entities.Player;
import com.example.mastermind.services.MultiplayerGameService;
import com.example.mastermind.services.PlayerService;
import com.example.mastermind.utils.EmitterDiagnostics;
import com.example.mastermind.customExceptions.UnauthenticatedUserException;
import com.example.mastermind.customExceptions.PlayerNotFoundException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/multiplayer")
public class MultiplayerGameController {
    private final AuthService authService;
    private final MultiplayerGameService multiplayerGameService;
    private final PlayerService playerService;
    private final EmitterRegistry emitterRegistry;
    private final EmitterDiagnostics emitterDiagnostics;
    private static final Logger logger = LoggerFactory.getLogger(MultiplayerGameController.class);

    /**
     *  This method is used to add the current user to a queue, which will send them into a game when a match is found. Upon matching, the user will be sent a "match" event, and a game will be created for them. I have to produce TEXT_EVENT_STREAM_VALUE in order for the browser to accept the event messages.
     * @param difficulty - the desired difficulty to play.
     * @return an emitter used to send events to the client
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping(value = "join", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter joinMultiplayerGame(@RequestParam String difficulty) {
        Player currentPlayer = authService.getCurrentAuthenticatedPlayer();
        /*
         Register the emitter immediately after creating it to ensure any events that occur right after the client connects are not missed.
        Early registration allows the system to send events to this emitter as soon as they happen and also makes cleanup (on completion, timeout, or error) easier.
         */
        long timeout = 60 * 60 * 1000L;
        SseEmitter emitter = new SseEmitter(timeout);
        emitterRegistry.addEmitter(currentPlayer.getPlayerId(), emitter);
        // callbacks that define what to do with the emitter in the event of completion, timeout, and error.
        emitter.onCompletion(() -> emitterRegistry.removeEmitter(currentPlayer.getPlayerId()));
        emitter.onTimeout(() -> emitterRegistry.removeEmitter(currentPlayer.getPlayerId()));
        emitter.onError((ex) -> emitterRegistry.removeEmitter(currentPlayer.getPlayerId()));

        // sends ping to confirm readiness. This is done to ensure that emitters work properly and are ready to receive the next event.
        try {
            emitter.send(SseEmitter.event().name("ping").data("ready"));
        } catch (IOException e) {
            emitterDiagnostics.logEmitterState(currentPlayer.getPlayerId());
            logger.warn("Failed to send ping to emitter for {}", currentPlayer.getUsername(), e);
        }
       multiplayerGameService.joinMultiplayerGame(currentPlayer.getPlayerId(), difficulty);
        return emitter;
    }

    /**
     *
     * @param multiplayerGameId - the ID of the game that the user is requesting information about.
     * @return either the game details or the appropriate error message (error is returned by the exception thrown in the service method)
     */

    @PreAuthorize("hasRole('USER')")
    @GetMapping("{multiplayerGameId}")
    public ResponseEntity<?> returnGameDetails(@PathVariable UUID multiplayerGameId){
        return ResponseEntity.ok(multiplayerGameService.findMultiplayerGameDetails(multiplayerGameId));
    }


}
