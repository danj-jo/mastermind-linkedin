package com.example.mastermind.services;

import com.example.mastermind.customExceptions.GameNotFoundException;
import com.example.mastermind.models.Difficulty;
import com.example.mastermind.models.entities.MultiplayerGame;
import com.example.mastermind.models.entities.Player;
import com.example.mastermind.utils.EmitterDiagnostics;
import com.example.mastermind.utils.EmitterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MultiplayerGameServiceTest {

    @Mock
    private EmitterDiagnostics emitterDiagnostics;

    @Mock
    private EmitterRegistry emitterRegistry;

    @InjectMocks
    private MultiplayerGameService multiplayerGameService;

    private Player testPlayer1;
    private Player testPlayer2;
    private MultiplayerGame testGame;

    @BeforeEach
    void setUp() {
        testPlayer1 = new Player();
        testPlayer1.setPlayerId(UUID.randomUUID());
        testPlayer1.setUsername("player1");
        testPlayer1.setPassword("password");
        testPlayer1.setEmail("player1@example.com");
        testPlayer1.setRole("ROLE_USER");

        testPlayer2 = new Player();
        testPlayer2.setPlayerId(UUID.randomUUID());
        testPlayer2.setUsername("player2");
        testPlayer2.setPassword("password");
        testPlayer2.setEmail("player2@example.com");
        testPlayer2.setRole("ROLE_USER");

        testGame = new MultiplayerGame();
        testGame.setGameId(UUID.randomUUID());
        testGame.setDifficulty(Difficulty.EASY);
        testGame.setWinningNumber("1234");
        testGame.setPlayer1(testPlayer1);
        testGame.setPlayer2(testPlayer1);

    }

    @Test
    void testJoinMultiplayerGame_Success() {
        // When
        multiplayerGameService.joinMultiplayerGame(testPlayer1, "EASY");

        // Then
        // Verify player was added to queue by checking active games size
        assertEquals(0, multiplayerGameService.activeGames.size());
    }


    @Test
    void testJoinMultiplayerGame_CreatesGameWhenTwoPlayers() {
        // Given
        SseEmitter emitter = new SseEmitter();
        when(emitterRegistry.getEmitter(any())).thenReturn(emitter);

        // When
        multiplayerGameService.joinMultiplayerGame(testPlayer1, "EASY");
        multiplayerGameService.joinMultiplayerGame(testPlayer2, "EASY");

        // Then
        // Verify game was created and added to active games
        assertEquals(1, multiplayerGameService.activeGames.size());
    }

    @Test
    void testFindMultiplayerGameDetails_Success() {
        // Given
        multiplayerGameService.activeGames.put(testGame.getGameId(), testGame);

        // When
        Map<String, Object> result = multiplayerGameService.findMultiplayerGameDetails(testGame.getGameId());

        // Then
        assertNotNull(result);
        assertEquals(4, result.get("numbersToGuess"));
        assertTrue(result.containsKey("numbersToGuess"));
    }

    @Test
    void testFindMultiplayerGameDetails_GameNotFound() {
        // Given
        UUID nonExistentGameId = UUID.randomUUID();

        // When & Then
        assertThrows(GameNotFoundException.class, () -> 
            multiplayerGameService.findMultiplayerGameDetails(nonExistentGameId));
    }

    @Test
    void testJoinMultiplayerGame_DifferentDifficulties() {
        // When
        multiplayerGameService.joinMultiplayerGame(testPlayer1, "EASY");
        multiplayerGameService.joinMultiplayerGame(testPlayer2, "MEDIUM");

        // Then
        // Verify no game was created since players are in different difficulty queues
        assertEquals(0, multiplayerGameService.activeGames.size());
    }

    @Test
    void testJoinMultiplayerGame_DuplicatePlayer() {
        // When
        multiplayerGameService.joinMultiplayerGame(testPlayer1, "EASY");
        multiplayerGameService.joinMultiplayerGame(testPlayer1, "EASY");

        // Then
        // Should not create a game with duplicate player
        assertEquals(0, multiplayerGameService.activeGames.size());
    }
}
