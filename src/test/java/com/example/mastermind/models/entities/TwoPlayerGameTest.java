package com.example.mastermind.models.entities;

import com.example.mastermind.models.Difficulty;
import com.example.mastermind.models.GameMode;
import com.example.mastermind.models.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MultiplayerGameTest {

    private MultiplayerGame multiplayerGame;
    private Player player1;
    private Player player2;
    private MultiplayerGuess guess1;
    private MultiplayerGuess guess2;

    @BeforeEach
    void setUp() {
        player1 = new Player(
                UUID.randomUUID(),
                "player1",
                "password1",
                "player1@example.com",
                "USER"
        );
        
        player2 = new Player(
                UUID.randomUUID(),
                "player2",
                "password2",
                "player2@example.com",
                "USER"
        );

        multiplayerGame = MultiplayerGame.builder()
                .gameId(UUID.randomUUID())
                .player1(player1)
                .player2(player2)
                .currentPlayerId(player1.getPlayerId())
                .winningNumber("1234")
                .difficulty(Difficulty.EASY)
                .mode(GameMode.MULTIPLAYER)
                .result(Result.PENDING)
                .finished(false)
                .guesses(new HashSet<>())
                .build();

        guess1 = new MultiplayerGuess();
        guess1.setGuess("1234");
        guess1.setPlayer(player1);
        guess1.setGame(multiplayerGame);

        guess2 = new MultiplayerGuess();
        guess2.setGuess("5678");
        guess2.setPlayer(player2);
        guess2.setGame(multiplayerGame);
    }

    @Test
    void generateHint_WithValidGuess_ReturnsFormattedHint() {
        // When
        String hint = multiplayerGame.generateHint(player1, "1234");

        // Then
        assertTrue(hint.contains("player1"));
        assertTrue(hint.contains("numbers correct"));
        assertTrue(hint.contains("locations"));
        assertTrue(hint.contains("guesses remaining"));
    }

    @Test
    void guessAlreadyExists_WithExistingGuess_ReturnsTrue() {
        // Given
        multiplayerGame.getGuesses().add(guess1);

        // When
        boolean exists = multiplayerGame.guessAlreadyExists("1234");

        // Then
        assertTrue(exists);
    }

    @Test
    void guessAlreadyExists_WithNonExistentGuess_ReturnsFalse() {
        // Given
        multiplayerGame.getGuesses().add(guess1);

        // When
        boolean exists = multiplayerGame.guessAlreadyExists("5678");

        // Then
        assertFalse(exists);
    }

    @Test
    void userLostGame_WithTenGuesses_ReturnsTrue() {
        // Given
        for (int i = 0; i < 10; i++) {
            MultiplayerGuess guess = new MultiplayerGuess();
            guess.setGuess("000" + i);
            guess.setPlayer(player1);
            guess.setGame(multiplayerGame);
            multiplayerGame.getGuesses().add(guess);
        }

        // When
        boolean lost = multiplayerGame.userLostGame();

        // Then
        assertTrue(lost);
    }

    @Test
    void userLostGame_WithLessThanTenGuesses_ReturnsFalse() {
        // Given
        multiplayerGame.getGuesses().add(guess1);

        // When
        boolean lost = multiplayerGame.userLostGame();

        // Then
        assertFalse(lost);
    }

    @Test
    void multiplayerGameBuilder_CreatesCorrectInstance() {
        // When
        MultiplayerGame game = MultiplayerGame.builder()
                .gameId(UUID.randomUUID())
                .player1(player1)
                .player2(player2)
                .currentPlayerId(player1.getPlayerId())
                .winningNumber("5678")
                .difficulty(Difficulty.MEDIUM)
                .mode(GameMode.MULTIPLAYER)
                .result(Result.PENDING)
                .finished(false)
                .guesses(new HashSet<>())
                .build();

        // Then
        assertNotNull(game);
        assertEquals(player1, game.getPlayer1());
        assertEquals(player2, game.getPlayer2());
        assertEquals(player1.getPlayerId(), game.getCurrentPlayerId());
        assertEquals("5678", game.getWinningNumber());
        assertEquals(Difficulty.MEDIUM, game.getDifficulty());
        assertEquals(GameMode.MULTIPLAYER, game.getMode());
        assertEquals(Result.PENDING, game.getResult());
        assertFalse(game.isFinished());
    }

    @Test
    void multiplayerGameSetters_UpdateValuesCorrectly() {
        // Given
        UUID newGameId = UUID.randomUUID();
        Player newPlayer1 = new Player(UUID.randomUUID(), "newplayer1", "pass", "new1@example.com", "USER");
        Player newPlayer2 = new Player(UUID.randomUUID(), "newplayer2", "pass", "new2@example.com", "USER");

        // When
        multiplayerGame.setGameId(newGameId);
        multiplayerGame.setPlayer1(newPlayer1);
        multiplayerGame.setPlayer2(newPlayer2);
        multiplayerGame.setCurrentPlayerId(newPlayer1.getPlayerId());
        multiplayerGame.setFinished(true);
        multiplayerGame.setResult(Result.WIN);

        // Then
        assertEquals(newGameId, multiplayerGame.getGameId());
        assertEquals(newPlayer1, multiplayerGame.getPlayer1());
        assertEquals(newPlayer2, multiplayerGame.getPlayer2());
        assertEquals(newPlayer1.getPlayerId(), multiplayerGame.getCurrentPlayerId());
        assertTrue(multiplayerGame.isFinished());
        assertEquals(Result.WIN, multiplayerGame.getResult());
    }

    @Test
    void multiplayerGameNoArgsConstructor_CreatesEmptyInstance() {
        // When
        MultiplayerGame emptyGame = new MultiplayerGame();

        // Then
        assertNotNull(emptyGame);
        assertNull(emptyGame.getGameId());
        assertNull(emptyGame.getPlayer1());
        assertNull(emptyGame.getPlayer2());
        assertNull(emptyGame.getCurrentPlayerId());
    }
}