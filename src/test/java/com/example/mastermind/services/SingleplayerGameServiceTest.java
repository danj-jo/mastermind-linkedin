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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

        testGame = new SinglePlayerGame();
        testGame.setGameId(UUID.randomUUID());
        testGame.setPlayer(testPlayer);
        testGame.setDifficulty(Difficulty.EASY);
        testGame.setWinningNumber("1234");
        testGame.setGuesses(new ArrayList<>());
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
        assertEquals(Difficulty.EASY, result.getDifficulty());
        assertNotNull(result.getWinningNumber());
        verify(gameRepository).saveAndFlush(any(SinglePlayerGame.class));
    }

    @Test
    void testCreateNewMediumGame_Success() {
        // Given
        when(playerRepository.findById(any())).thenReturn(Optional.of(testPlayer));
        when(gameRepository.saveAndFlush(any(SinglePlayerGame.class))).thenReturn(testGame);

        // When
        SinglePlayerGame result = gameService.createNewGame("MEDIUM", testPlayer.getPlayerId());

        // Then
        assertNotNull(result);
        assertEquals(testPlayer, result.getPlayer());
        assertEquals(Difficulty.EASY, result.getDifficulty());
        assertNotNull(result.getWinningNumber());
        verify(gameRepository).saveAndFlush(any(SinglePlayerGame.class));
    }
    @Test
    void testCreateNewHardGame_Success() {
        // Given
        when(playerRepository.findById(any())).thenReturn(Optional.of(testPlayer));
        when(gameRepository.saveAndFlush(any(SinglePlayerGame.class))).thenReturn(testGame);

        // When
        SinglePlayerGame result = gameService.createNewGame("HARD", testPlayer.getPlayerId());

        // Then
        assertNotNull(result);
        assertEquals(testPlayer, result.getPlayer());
        assertEquals(Difficulty.EASY, result.getDifficulty());
        assertNotNull(result.getWinningNumber());
        verify(gameRepository).saveAndFlush(any(SinglePlayerGame.class));
    }
    @Test
    void playFullEasyGameTest(){
        testGame.setDifficulty(Difficulty.EASY);
        testGame.setWinningNumber("4235");
        testGame.submitGuess("1234");
        testGame.submitGuess("1134");
        testGame.submitGuess("1424");
        testGame.submitGuess("1534");
        testGame.submitGuess("1634");
        testGame.submitGuess("1734");
        testGame.submitGuess("1777");
        testGame.submitGuess("1434");
        testGame.submitGuess("1214");
        testGame.submitGuess("1264");
         assertTrue(testGame.isFinished());
    }

    @Test
    void guessLengthMoreThanEasyWinningNumberLengthTest(){
        testGame.setDifficulty(Difficulty.EASY);
        testGame.submitGuess("11112222111222333");
        assertEquals(0,testGame.getGuesses().size());
    }
    @Test
    void guessLengthMoreThanMediumWinningNumberLengthTest(){
        testGame.setDifficulty(Difficulty.EASY);
        testGame.submitGuess("11112222111222333");
        assertEquals(0,testGame.getGuesses().size());
    }
    @Test
    void guessLengthMoreThanHardWinningNumberLengthTest(){
        testGame.setDifficulty(Difficulty.EASY);
        testGame.submitGuess("11112222111222333");
        assertEquals(0,testGame.getGuesses().size());
    }
    @Test
    void extraGuessesAreNotSavedTest(){
        testGame.setDifficulty(Difficulty.EASY);
        testGame.setWinningNumber("4235");
        testGame.submitGuess("1234");
        testGame.submitGuess("1134");
        testGame.submitGuess("1424");
        testGame.submitGuess("1534");
        testGame.submitGuess("1634");
        testGame.submitGuess("1734");
        testGame.submitGuess("1777");
        testGame.submitGuess("1434");
        testGame.submitGuess("1214");
        testGame.submitGuess("1264");
        testGame.submitGuess("0000");
        testGame.submitGuess("0023");
        testGame.submitGuess("0503");
        testGame.submitGuess("5001");
        assertEquals(10,testGame.getGuesses().size());
    }


    @Test
    void noDuplicateGuessTest(){
        testGame.setDifficulty(Difficulty.EASY);
        testGame.setWinningNumber("1456");
        int testPointer = 0;
        while(testPointer < 10){
            testGame.submitGuess("1234");
            testPointer++;
        }
        assertEquals(1,testGame.getGuesses().size());
    }

    @Test
    void noInvalidCharacterTest(){
        testGame.submitGuess(".28ss");
        assertEquals(0,testGame.getGuesses().size());
    }

    @Test
    void testCreateNewGame_PlayerNotFound() {
        // Given
        when(playerRepository.findById(any())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> 
            gameService.createNewGame("EASY", UUID.randomUUID()));
        verify(gameRepository, never()).saveAndFlush(any());
    }

    @Test
    void testSubmitGuess_Success() {
        // Given
        when(gameRepository.findById(any())).thenReturn(Optional.of(testGame));
        when(gameRepository.saveAndFlush(any(SinglePlayerGame.class))).thenReturn(testGame);

        // When
        String result = gameService.submitGuess(testGame.getGameId(), "5678");

        // Then
        assertNotNull(result);
        verify(gameRepository).saveAndFlush(any(SinglePlayerGame.class));
    }

    @Test
    void testSubmitGuess_GameNotFound() {
        // Given
        when(gameRepository.findById(any())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(GameNotFoundException.class, () -> 
            gameService.submitGuess(UUID.randomUUID(), "5678"));
        verify(gameRepository, never()).saveAndFlush(any());
    }

    @Test
    void testFindGameById_Success() {
        // Given
        when(gameRepository.findGameByGameId(any())).thenReturn(Optional.of(testGame));

        // When
        SinglePlayerGame result = gameService.findGameById(testGame.getGameId());

        // Then
        assertNotNull(result);
        assertEquals(testGame.getGameId(), result.getGameId());
    }

    @Test
    void testFindGameById_NotFound() {
        // Given
        when(gameRepository.findGameByGameId(any())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(GameNotFoundException.class, () -> 
            gameService.findGameById(UUID.randomUUID()));
    }

    @Test
    void testGetFinishedGamesByPlayerId() {
        // Given
        List<SinglePlayerGame> finishedGames = List.of(testGame);
        when(playerRepository.existsById(any())).thenReturn(true);
        when(gameRepository.findFinishedGames(any())).thenReturn(finishedGames);

        // When
        List<PastGame> result = gameService.getFinishedGamesByPlayerId(testPlayer.getPlayerId());

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(gameRepository).findFinishedGames(testPlayer.getPlayerId());
    }

    @Test
    void testGetUnfinishedGamesByPlayerId() {
        // Given
        List<SinglePlayerGame> unfinishedGames = List.of(testGame);
        when(playerRepository.existsById(any())).thenReturn(true);
        when(gameRepository.findUnfinishedGames(any())).thenReturn(unfinishedGames);

        // When
        List<PastGame> result = gameService.getUnfinishedGamesByPlayerId(testPlayer.getPlayerId());

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(gameRepository).findUnfinishedGames(testPlayer.getPlayerId());
    }

    @Test
    void testGetFinishedGamesByPlayerId_PlayerNotFound() {
        // Given
        when(playerRepository.existsById(any())).thenReturn(false);

        // When & Then
        assertThrows(PlayerNotFoundException.class, () -> 
            gameService.getFinishedGamesByPlayerId(UUID.randomUUID()));
    }

    @Test
    void testGetUnfinishedGamesByPlayerId_PlayerNotFound() {
        // Given
        when(playerRepository.existsById(any())).thenReturn(false);

        // When & Then
        assertThrows(PlayerNotFoundException.class, () -> 
            gameService.getUnfinishedGamesByPlayerId(UUID.randomUUID()));
    }

    @Test
    void testIsGameFinished() {
        // Given
        when(gameRepository.existsByGameIdAndIsFinishedTrue(any())).thenReturn(true);

        // When
        boolean result = gameService.isGameFinished(testGame.getGameId());

        // Then
        assertTrue(result);
        verify(gameRepository).existsByGameIdAndIsFinishedTrue(testGame.getGameId());
    }
}
