package com.example.mastermind.utils;

import com.example.mastermind.models.EmitterRegistry;
import com.example.mastermind.models.entities.Player;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@Component
@AllArgsConstructor
public class EmitterDiagnostics {

    private static final Logger logger = LoggerFactory.getLogger(EmitterDiagnostics.class);
    private final EmitterRegistry emitterRegistry;
    public void logEmitterState(UUID playerId) {
        SseEmitter emitter = emitterRegistry.getEmitter(playerId);
        if (emitter == null) {
            logger.error("❌ No emitter registered for player: \" + playerId");
            System.out.println("❌ No emitter registered for player: " + playerId);
        } else {
            System.out.println("✅ Emitter exists for player: " + playerId);
        }
    }

    public void logAllEmitters() {
        System.out.println("📡 Registered Emitters:");
        emitterRegistry.emitters.forEach((id, emitter) -> {
            System.out.println(" - " + id);
        });
    }

    public void logMatchAttempt(Player player1, Player player2) {
        System.out.println("🎮 Match Attempt:");
        System.out.println(" - Player 1: " + player1.getPlayerId());
        System.out.println(" - Player 2: " + player2.getPlayerId());
        logEmitterState(player1.getPlayerId());
        logEmitterState(player2.getPlayerId());
    }
}
