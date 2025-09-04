package com.example.mastermind.controllers;

import com.example.mastermind.services.SingleplayerGameService;
import com.example.mastermind.services.PlayerService;
import com.example.mastermind.models.entities.SinglePlayerGame;
import com.example.mastermind.models.entities.Player;
import com.example.mastermind.models.Difficulty;
import com.example.mastermind.utils.PlayerUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;
import java.util.Map;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SinglePlayerGameControllerTest {

    @Mock
    private SingleplayerGameService singleplayerGameService;

    @Mock
    private PlayerService playerService;

    @InjectMocks
    private SinglePlayerGameController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private SinglePlayerGame testGame;
    private Player testPlayer;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
        
        testPlayer = new Player();
        testPlayer.setPlayerId(UUID.randomUUID());
        testPlayer.setUsername("testuser");
        testPlayer.setPassword("password");
        testPlayer.setEmail("test@example.com");
        testPlayer.setRole("ROLE_USER");

        testGame = new SinglePlayerGame();
        testGame.setGameId(UUID.randomUUID());
        testGame.setDifficulty(Difficulty.EASY);
        testGame.setWinningNumber("1234");
    }

    @Test
    void createGame() throws Exception {
        try (MockedStatic<PlayerUtils> mockedPlayerUtils = mockStatic(PlayerUtils.class)) {
            // Given
            mockedPlayerUtils.when(PlayerUtils::getCurrentUsername).thenReturn("testuser");
            when(playerService.findPlayerByUsername("testuser")).thenReturn(testPlayer);
            when(singleplayerGameService.createNewGame(any(), any())).thenReturn(testGame);

            // When & Then
            mockMvc.perform(post("/singleplayer/games/new")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"difficulty\":\"EASY\"}"))
                    .andExpect(status().isCreated());
        }
    }

    @Test
    void submitGuess() throws Exception {
        try (MockedStatic<PlayerUtils> mockedPlayerUtils = mockStatic(PlayerUtils.class)) {
            // Given
            UUID gameId = testGame.getGameId();
            testGame.setPlayer(testPlayer); // Set the player to avoid null pointer
            mockedPlayerUtils.when(PlayerUtils::getCurrentUsername).thenReturn("testuser");
            when(playerService.findPlayerByUsername("testuser")).thenReturn(testPlayer);
            when(singleplayerGameService.findGameById(any())).thenReturn(testGame);
            when(singleplayerGameService.submitGuess(any(), any())).thenReturn("2 correct, 1 in wrong position");
            when(singleplayerGameService.isGameFinished(any())).thenReturn(false);

            // When & Then
            mockMvc.perform(post("/singleplayer/games/{gameId}/guess", gameId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"guess\":\"5678\"}"))
                    .andExpect(status().isOk());
        }
    }


}