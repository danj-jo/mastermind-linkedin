package com.example.mastermind.controllers;

import com.example.mastermind.services.PlayerService;
import com.example.mastermind.models.entities.Player;
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

import java.util.List;
import java.util.UUID;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class PlayerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PlayerService playerService;

    @InjectMocks
    private PlayerController playerController;

    private ObjectMapper objectMapper;
    private Player testPlayer;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        testPlayer = new Player();
        testPlayer.setPlayerId(UUID.randomUUID());
        testPlayer.setUsername("testuser");
        testPlayer.setPassword("password");
        testPlayer.setEmail("test@example.com");
        testPlayer.setRole("ROLE_USER");
        
        mockMvc = MockMvcBuilders.standaloneSetup(playerController).build();
    }

    @Test
    void getPastGames() throws Exception {
        // Given
        try (MockedStatic<PlayerUtils> mockedPlayerUtils = mockStatic(PlayerUtils.class)) {
            mockedPlayerUtils.when(PlayerUtils::getCurrentUsername).thenReturn("testuser");
            
            when(playerService.findPlayerByUsername("testuser")).thenReturn(testPlayer);
            when(playerService.returnCurrentPlayersPastGames(any())).thenReturn(
                Map.of("finished", List.of(), "unfinished", List.of())
            );

            // When & Then
            mockMvc.perform(get("/me/games"))
                    .andExpect(status().isOk());
        }
    }

    @Test
    void getProfile() throws Exception {
        // Given
        try (MockedStatic<PlayerUtils> mockedPlayerUtils = mockStatic(PlayerUtils.class)) {
            mockedPlayerUtils.when(PlayerUtils::getCurrentUsername).thenReturn("testuser");
            
            when(playerService.findPlayerByUsername("testuser")).thenReturn(testPlayer);

            // When & Then
            mockMvc.perform(get("/me/profile"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("testuser"))
                    .andExpect(jsonPath("$.email").value("test@example.com"));
        }
    }
}
