package com.example.mastermind.services;

import com.example.mastermind.customExceptions.GameNotFoundException;
import com.example.mastermind.customExceptions.PlayerNotFoundException;
import com.example.mastermind.repositoryLayer.SingleplayerGameRepository;
import com.example.mastermind.repositoryLayer.PlayerRepository;
import com.example.mastermind.models.PastGame;
import com.example.mastermind.models.Difficulty;
import com.example.mastermind.models.entities.Player;
import com.example.mastermind.models.entities.SinglePlayerGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SingleplayerGameServiceTest {

    @Mock
    private SingleplayerGameRepository gameRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private PlayerService playerService;

    @InjectMocks
    private SingleplayerGameService gameService;

    private Player testPlayer;
    private SinglePlayerGame testGame;

    @BeforeEach
    void setUp() {
        testPlayer = new Player();
        testPlayer.setPlayerId(UUID.randomUUID());
        testPlayer.setUsername("testuser");
        testPlayer.setPassword("password");
        testPlayer.setEmail("test@example.com");
        testPlayer.setRole("ROLE_USER");

        testGame = SinglePlayerGame.builder().gameId(UUID.randomUUID()).player(testPlayer).guesses(new HashSet<>())
                .winningNumber("2435").build();

    }



    @Test
    void testCreateNewEasyGame_Success() {
        // Given
        when(playerRepository.findById(any())).thenReturn(Optional.of(testPlayer));
        when(gameRepository.saveAndFlush(any(SinglePlayerGame.class))).thenReturn(testGame);

        // When
        SinglePlayerGame result = gameService.createNewGame("EASY", testPlayer.getPlayerId());

        // Then
        assertNotNull(result);
        assertEquals(testPlayer, result.getPlayer());
        assertNotNull(result.getResult());
        verify(gameRepository).saveAndFlush(any(SinglePlayerGame.class));
    }

//    @Test
//    void testCreateNewMediumGame_Success() {
//        // Given
//        when(playerRepository.findById(any())).thenReturn(Optional.of(testPlayer));
//        when(gameRepository.saveAndFlush(any(SinglePlayerGame.class))).thenReturn(testGame);
//
//        // When
//        SinglePlayerGame result = gameService.createNewGame("MEDIUM", testPlayer.getPlayerId());
//
//        // Then
//        assertNotNull(result);
//        assertEquals(testPlayer, result.getPlayer());
//        assertEquals(Difficulty.EASY, result.getDifficulty());
//        assertNotNull(result.getWinningNumber());
//        verify(gameRepository).saveAndFlush(any(SinglePlayerGame.class));
//    }
//    @Test
//    void testCreateNewHardGame_Success() {
//        // Given
//        when(playerRepository.findById(any())).thenReturn(Optional.of(testPlayer));
//        when(gameRepository.saveAndFlush(any(SinglePlayerGame.class))).thenReturn(testGame);
//
//        // When
//        SinglePlayerGame result = gameService.createNewGame("HARD", testPlayer.getPlayerId());
//
//        // Then
//        assertNotNull(result);
//        assertEquals(testPlayer, result.getPlayer());
//        assertEquals(Difficulty.EASY, result.getDifficulty());
//        assertNotNull(result.getWinningNumber());
//        verify(gameRepository).saveAndFlush(any(SinglePlayerGame.class));
//    }
//
//
//
//
//
//
//    @Test
//    void testCreateNewGame_PlayerNotFound() {
//        // Given
//        when(playerRepository.findById(any())).thenReturn(Optional.empty());
//
//        // When & Then
//        assertThrows(UsernameNotFoundException.class, () ->
//            gameService.createNewGame("EASY", UUID.randomUUID()));
//        verify(gameRepository, never()).saveAndFlush(any());
//    }
//
//
//
//
//    @Test
//    void testFindGameById_Success() {
//        // Given
//        when(gameRepository.findGameByGameId(any())).thenReturn(Optional.of(testGame));
//
//        // When
//        SinglePlayerGame result = gameService.findGameById(testGame.getGameId());
//
//        // Then
//        assertNotNull(result);
//        assertEquals(testGame.getGameId(), result.getGameId());
//    }
//
//    @Test
//    void testFindGameById_NotFound() {
//        // Given
//        when(gameRepository.findGameByGameId(any())).thenReturn(Optional.empty());
//
//        // When & Then
//        assertThrows(GameNotFoundException.class, () ->
//            gameService.findGameById(UUID.randomUUID()));
//    }
//
//    @Test
//    void testGetFinishedGamesByPlayerId() {
//        // Given
//        List<SinglePlayerGame> finishedGames = List.of(testGame);
//        when(playerRepository.existsById(any())).thenReturn(true);
//        when(gameRepository.findFinishedGames(any())).thenReturn(finishedGames);
//
//        // When
//        List<PastGame> result = gameService.getFinishedGamesByPlayerId(testPlayer.getPlayerId());
//
//        // Then
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        verify(gameRepository).findFinishedGames(testPlayer.getPlayerId());
//    }
//
//    @Test
//    void testGetUnfinishedGamesByPlayerId() {
//        // Given
//        List<SinglePlayerGame> unfinishedGames = List.of(testGame);
//        when(playerRepository.existsById(any())).thenReturn(true);
//        when(gameRepository.findUnfinishedGames(any())).thenReturn(unfinishedGames);
//
//        // When
//        List<PastGame> result = gameService.getUnfinishedGamesByPlayerId(testPlayer.getPlayerId());
//
//        // Then
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        verify(gameRepository).findUnfinishedGames(testPlayer.getPlayerId());
//    }
//
//    @Test
//    void testGetFinishedGamesByPlayerId_PlayerNotFound() {
//        // Given
//        when(playerRepository.existsById(any())).thenReturn(false);
//
//        // When & Then
//        assertThrows(PlayerNotFoundException.class, () ->
//            gameService.getFinishedGamesByPlayerId(UUID.randomUUID()));
//    }
//
//    @Test
//    void testGetUnfinishedGamesByPlayerId_PlayerNotFound() {
//        // Given
//        when(playerRepository.existsById(any())).thenReturn(false);
//
//        // When & Then
//        assertThrows(PlayerNotFoundException.class, () ->
//            gameService.getUnfinishedGamesByPlayerId(UUID.randomUUID()));
//    }


}
