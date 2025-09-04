package com.example.mastermind.models.entities;

import com.example.mastermind.models.Difficulty;
import com.example.mastermind.models.Result;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MultiplayerGameTest {

    @Test
    void createGame() {
        Player player1 = createPlayer("player1", "player1@test.com");
        Player player2 = createPlayer("player2", "player2@test.com");
        
        MultiplayerGame game = new MultiplayerGame();
        game.setGameId(UUID.randomUUID());
        game.setPlayer1(player1);
        game.setPlayer2(player2);
        game.setWinningNumber("1234");
        game.setDifficulty(Difficulty.EASY);
        
        assertNotNull(game.getGameId());
        assertEquals("1234", game.getWinningNumber());
        assertEquals(Difficulty.EASY, game.getDifficulty());
        assertEquals(player1, game.getPlayer1());
        assertEquals(player2, game.getPlayer2());
        assertFalse(game.isFinished());
        assertEquals(Result.PENDING, game.getResult());
    }

    @Test
    void submitGuess() {
        Player player1 = createPlayer("player1", "player1@test.com");
        Player player2 = createPlayer("player2", "player2@test.com");
        
        MultiplayerGame game = new MultiplayerGame();
        game.setGameId(UUID.randomUUID());
        game.setPlayer1(player1);
        game.setPlayer2(player2);
        game.setWinningNumber("1234");
        game.setDifficulty(Difficulty.EASY);
        
        String result = game.submitGuess(player1, "5678");
        
        assertNotNull(result);
        assertEquals(1, game.getGuesses().size());
        assertTrue(result.contains("numbers correct"));
    }

    @Test
    void submitInvalidGuess() {
        Player player1 = createPlayer("player1", "player1@test.com");
        Player player2 = createPlayer("player2", "player2@test.com");
        
        MultiplayerGame game = new MultiplayerGame();
        game.setGameId(UUID.randomUUID());
        game.setPlayer1(player1);
        game.setPlayer2(player2);
        game.setWinningNumber("1234");
        game.setDifficulty(Difficulty.EASY);
        
        String result = game.submitGuess(player1, "invalid");
        
        assertEquals("Guesses are numbers only", result);
        assertEquals(0, game.getGuesses().size());
    }

    @Test
    void submitCorrectGuess() {
        Player player1 = createPlayer("player1", "player1@test.com");
        Player player2 = createPlayer("player2", "player2@test.com");
        
        MultiplayerGame game = new MultiplayerGame();
        game.setGameId(UUID.randomUUID());
        game.setPlayer1(player1);
        game.setPlayer2(player2);
        game.setWinningNumber("1234");
        game.setDifficulty(Difficulty.EASY);
        
        String result = game.submitGuess(player1, "1234");
        
        assertEquals("You Win!", result);
        assertTrue(game.isFinished());
        assertEquals(Result.WIN, game.getResult());
    }

    private Player createPlayer(String username, String email) {
        Player player = new Player();
        player.setPlayerId(UUID.randomUUID());
        player.setUsername(username);
        player.setPassword("password");
        player.setEmail(email);
        player.setRole("ROLE_USER");
        return player;
    }
}