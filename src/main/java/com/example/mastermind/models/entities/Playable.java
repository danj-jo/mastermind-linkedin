package com.example.mastermind.models.entities;

import java.util.UUID;

public interface Playable <T> {
    String submitGuess(UUID playerId, String guess);
    String submitGuess(UUID gameId, UUID playerID, String guess);
}
