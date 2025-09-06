package com.example.mastermind.models.entities;

import com.example.mastermind.models.Difficulty;
import com.example.mastermind.models.GameMode;
import com.example.mastermind.models.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        game.setGuesses(new ArrayList<>());
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
        String result = game.submitGuess("0123");
        
        assertNotNull(result);
        assertEquals(1, game.getGuesses().size());
        assertTrue(result.contains("correct"));
    }

    @Test
    void submitInvalidGuess() {
        String result = game.submitGuess("invalid");
        
        assertEquals("Only numbers 0-7 are allowed. Please try again.", result);
        assertEquals(0, game.getGuesses().size());
    }

    @Test
    void submitCorrectGuess() {
        String result = game.submitGuess("1234");
        
        assertNotNull(result);
        assertTrue(game.isFinished());
        assertEquals(Result.WIN, game.getResult());
    }

    @Test
    void attemptMaxNumberOfGuesses(){
        game.setDifficulty(Difficulty.EASY);
        game.setPlayer(testPlayer);
        game.setResult(Result.LOSS);
        game.setFinished(false);
        game.setMode(GameMode.SINGLE_PLAYER);
        game.setWinningNumber("1234");
        List<String> combinations = new ArrayList<>();
        combinations.add("0123");
        combinations.add("0124");
        combinations.add("0125");
        combinations.add("0126");
        combinations.add("0127");
        combinations.add("0134");
        combinations.add("0135");
        combinations.add("0136");
        combinations.add("0137");
        combinations.add("0138");
        combinations.forEach(n -> game.submitGuess(n));
        game.setGuesses(combinations);
        assertEquals(Result.LOSS, game.getResult());
    }
}
