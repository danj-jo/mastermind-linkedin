package com.example.mastermind.repositoryLayer;

import com.example.mastermind.models.Difficulty;
import com.example.mastermind.models.GameMode;
import com.example.mastermind.models.Result;
import com.example.mastermind.models.entities.Player;
import com.example.mastermind.models.entities.SinglePlayerGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class SingleplayerGameRepositoryTest {

    @Autowired
    private SingleplayerGameRepository gameRepository;

    @Autowired
    private PlayerRepository playerRepository;

    private Player testPlayer;
    private SinglePlayerGame testGame;

    @BeforeEach
    void setUp() {
        testPlayer = new Player(
                UUID.randomUUID(),
                "testuser",
                "password123",
                "test@example.com",
                "USER"
        );
        playerRepository.save(testPlayer);

        testGame = SinglePlayerGame.builder()
                .gameId(UUID.randomUUID())
                .player(testPlayer)
                .winningNumber("1234")
                .difficulty(Difficulty.EASY)
                .mode(GameMode.SINGLE_PLAYER)
                .result(Result.PENDING)
                .finished(false)
                .guesses(new java.util.HashSet<>())
                .build();
    }

    @Test
    void existsByGameId_WithExistingGame_ReturnsTrue() {
        // Given
        gameRepository.save(testGame);

        // When
        boolean exists = gameRepository.existsByGameId(testGame.getGameId());

        // Then
        assertTrue(exists);
    }

    @Test
    void existsByGameId_WithNonExistentGame_ReturnsFalse() {
        // When
        boolean exists = gameRepository.existsByGameId(UUID.randomUUID());

        // Then
        assertFalse(exists);
    }

    @Test
    void findGameByGameId_WithExistingGame_ReturnsGame() {
        // Given
        gameRepository.save(testGame);

        // When
        Optional<SinglePlayerGame> result = gameRepository.findGameByGameId(testGame.getGameId());

        // Then
        assertTrue(result.isPresent());
        assertEquals(testGame.getGameId(), result.get().getGameId());
    }

    @Test
    void findFinishedGames_WithFinishedGames_ReturnsFinishedGames() {
        // Given
        testGame.setFinished(true);
        testGame.setResult(Result.WIN);
        gameRepository.save(testGame);

        // When
        List<SinglePlayerGame> result = gameRepository.findFinishedGames(testPlayer.getPlayerId());

        // Then
        assertEquals(1, result.size());
        assertTrue(result.get(0).isFinished());
    }

    @Test
    void findUnfinishedGames_WithUnfinishedGames_ReturnsUnfinishedGames() {
        // Given
        gameRepository.save(testGame);

        // When
        List<SinglePlayerGame> result = gameRepository.findUnfinishedGames(testPlayer.getPlayerId());

        // Then
        assertEquals(1, result.size());
        assertFalse(result.get(0).isFinished());
    }
}