package com.example.mastermind.controllers;

import com.example.mastermind.services.MultiplayerGameService;
import com.example.mastermind.services.PlayerService;
import com.example.mastermind.models.entities.MultiplayerGame;
import com.example.mastermind.models.Difficulty;
import com.example.mastermind.utils.EmitterRegistry;
import com.example.mastermind.utils.EmitterDiagnostics;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MultiplayerGameControllerTest {

    @Mock
    private MultiplayerGameService multiplayerGameService;

    @Mock
    private PlayerService playerService;

    @Mock
    private EmitterRegistry emitterRegistry;

    @Mock
    private EmitterDiagnostics emitterDiagnostics;

    @InjectMocks
    private MultiplayerGameController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private MultiplayerGame testGame;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
        
        testGame = new MultiplayerGame();
        testGame.setGameId(UUID.randomUUID());
        testGame.setDifficulty(Difficulty.EASY);
        testGame.setWinningNumber("1234");
    }


    @Test
    void getGameDetails() throws Exception {
        try (MockedStatic<PlayerUtils> mockedPlayerUtils = mockStatic(PlayerUtils.class)) {
            // Given
            UUID gameId = testGame.getGameId();
            mockedPlayerUtils.when(PlayerUtils::getCurrentUsername).thenReturn("testuser");
            when(multiplayerGameService.findMultiplayerGameDetails(any())).thenReturn(
                Map.of("numbersToGuess", 4)
            );

            // When & Then - The actual endpoint is just /multiplayer/{gameId}
            mockMvc.perform(get("/multiplayer/{gameId}", gameId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.numbersToGuess").value(4));
        }
    }
}