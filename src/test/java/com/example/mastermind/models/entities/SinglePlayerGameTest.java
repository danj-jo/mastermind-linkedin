package com.example.mastermind.models.entities;

import com.example.mastermind.models.Difficulty;
import com.example.mastermind.models.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SinglePlayerGameTest {

    private SinglePlayerGame game;
    private Player testPlayer;

    @BeforeEach
    void setUp() {
        testPlayer = new Player();
        testPlayer.setPlayerId(UUID.randomUUID());
        testPlayer.setUsername("testuser");
        testPlayer.setPassword("password");
        testPlayer.setEmail("test@example.com");
        testPlayer.setRole("ROLE_USER");

        game = new SinglePlayerGame();
        game.setGameId(UUID.randomUUID());
        game.setPlayer(testPlayer);
        game.setDifficulty(Difficulty.EASY);
        game.setWinningNumber("1234");
        game.setGuesses(new HashSet<>());

    }

    @Test
    void createGame() {
        assertNotNull(game.getGameId());
        assertEquals(testPlayer, game.getPlayer());
        assertEquals(Difficulty.EASY, game.getDifficulty());
        assertEquals("1234", game.getWinningNumber());
        assertNotNull(game.getGuesses());
        assertFalse(game.isFinished());
        assertEquals(Result.PENDING, game.getResult());
    }

    @Test
    void submitValidGuess() {

    }
}

