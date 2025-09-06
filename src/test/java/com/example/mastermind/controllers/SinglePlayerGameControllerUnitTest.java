package com.example.mastermind.controllers;

import com.example.mastermind.services.SingleplayerGameService;
import com.example.mastermind.services.PlayerService;
import com.example.mastermind.models.entities.SinglePlayerGame;
import com.example.mastermind.models.entities.Player;
import com.example.mastermind.models.Difficulty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class SinglePlayerGameControllerUnitTest {

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

//    @Test
//    void createGame() throws Exception {
//        try (MockedStatic<AuthenticationUtils> mockedPlayerUtils = mockStatic(AuthenticationUtils.class)) {
//            // Given
//            mockedPlayerUtils.when(AuthenticationUtils::getCurrentAuthenticatedPlayerUsername).thenReturn("testuser");
//            when(playerService.findPlayerByUsername("testuser")).thenReturn(testPlayer);
//            when(singleplayerGameService.createNewGame(any(), any())).thenReturn(testGame);
//
//            // When & Then
//            mockMvc.perform(post("/singleplayer/games/new")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(objectMapper.writeValueAsString(Map.of("difficulty", "EASY"))))
//                    .andExpect(status().isCreated());
//        }
//    }

//    @Test
//    void submitGuess() throws Exception {
//        try (MockedStatic<AuthenticationUtils> mockedPlayerUtils = mockStatic(AuthenticationUtils.class)) {
//            // Given
//            UUID gameId = testGame.getGameId();
//            testGame.setPlayer(testPlayer); // Set the player to avoid null pointer
//            mockedPlayerUtils.when(AuthenticationUtils::getCurrentAuthenticatedPlayerUsername).thenReturn("testuser");
//            when(playerService.findPlayerByUsername("testuser")).thenReturn(testPlayer);
//            when(singleplayerGameService.findGameById(any())).thenReturn(testGame);
//            when(singleplayerGameService.submitGuess(any(), any())).thenReturn("2 correct, 1 in wrong position");
//            when(singleplayerGameService.isGameFinished(any())).thenReturn(false);
//
//            // When & Then
//            mockMvc.perform(post("/singleplayer/games/{gameId}/guess", gameId)
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content("{\"guess\":\"5678\"}"))
//                    .andExpect(status().isOk());
//        }
}