package com.example.mastermind.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Registry for managing SSE (Server-Sent Events) emitters for players.
 * - Stores emitters mapped by player UUID.
 * - Automatically removes emitters when they complete or time out.
 */
@Component
public class EmitterRegistry {

    /** Map of player IDs to their SSE emitters */
    public final Map<UUID, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * Adds a new emitter for the given player.
     * - Stores the emitter in the map.
     * - Automatically removes it from the map on completion or timeout.
     *
     * @param playerId - the UUID of the player
     * @param emitter the SSE emitter associated with the player
     */

    public void addEmitter(UUID playerId, SseEmitter emitter) {
        emitters.put(playerId, emitter);
        emitter.onCompletion(() -> emitters.remove(playerId));
        emitter.onTimeout(() -> emitters.remove(playerId));
    }


    /**
     * Retrieves the emitter for a given player.
     *
     * @param playerId - the UUID of the player
     * @return the SseEmitter associated with the player, or null if none exists
     */

    public SseEmitter getEmitter(UUID playerId) {
        return emitters.get(playerId);
    }

    /**
     * Removes the emitter associated with a given player.
     *
     * @param playerId - the UUID of the player
     */

    public void removeEmitter(UUID playerId) {
        emitters.remove(playerId);
    }
}
