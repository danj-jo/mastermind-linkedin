package com.example.mastermind.models;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class EmitterRegistry {
    public final Map<UUID, SseEmitter> emitters = new ConcurrentHashMap<>();

    public void addEmitter(UUID playerId, SseEmitter emitter) {
        emitters.put(playerId, emitter);
        emitter.onCompletion(() -> emitters.remove(playerId));
        emitter.onTimeout(() -> emitters.remove(playerId));
    }

    public SseEmitter getEmitter(UUID playerId) {
        return emitters.get(playerId);
    }

    public void removeEmitter(UUID playerId) {
        emitters.remove(playerId);
    }
}
