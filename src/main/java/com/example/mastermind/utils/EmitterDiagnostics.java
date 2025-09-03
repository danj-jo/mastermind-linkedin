package com.example.mastermind.utils;

import com.example.mastermind.models.entities.Player;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

/**
 * Utility class for logging the state of SSE emitters.
 * Helps track which players have active emitters, all registered emitters,
 * and logs information during match attempts between players.
 */
@Component
@AllArgsConstructor
public class EmitterDiagnostics {
    private static final Logger logger = LoggerFactory.getLogger(EmitterDiagnostics.class);
    private final EmitterRegistry emitterRegistry;

    /**
     * Logs the state of the SSE emitter for a specific player.
     * - If no emitter exists, logs an error and prints a message.
     * - If an emitter exists, prints a success message.
     * @param playerId the UUID of the player whose emitter should be checked
     */

    public void logEmitterState(UUID playerId) {
        SseEmitter emitter = emitterRegistry.getEmitter(playerId);
        if (emitter == null) {
            logger.error("ERROR: No emitter registered for player: \" + playerId");
            // emoji used for enhanced readability in the console.
            System.out.println("❌ No emitter registered for player: " + playerId);
        } else {
            System.out.println("✅ Emitter exists for player: " + playerId);
        }
    }

    /**
     * Logs all currently registered SSE emitters.
     * Prints the UUIDs of all registered emitters.
     */

    public void logAllEmitters() {
        System.out.println("📡 Registered Emitters:");
        emitterRegistry.emitters.forEach((id, emitter) -> {
            System.out.println(" - " + id);
        });
    }

    /**
     * Logs information about a match attempt between two players.
     * - Prints the UUIDs of both players.
     * - Checks and logs the emitter state for each player.
     *
     * @param player1 the first player in the match attempt
     * @param player2 the second player in the match attempt
     */

    public void logMatchAttempt(Player player1, Player player2) {
        System.out.println("Match Attempt:");
        System.out.println(" - Player 1: " + player1.getPlayerId());
        System.out.println(" - Player 2: " + player2.getPlayerId());
        logEmitterState(player1.getPlayerId());
        logEmitterState(player2.getPlayerId());
    }
}
