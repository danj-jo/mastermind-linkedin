package com.example.mastermind.services;

import com.example.mastermind.models.PastGame;
import com.example.mastermind.repositoryLayer.PlayerRepository;
import com.example.mastermind.repositoryLayer.SingleplayerGameRepository;
import com.example.mastermind.models.entities.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private SingleplayerGameService singleplayerGameService;

    @Mock
    private SingleplayerGameRepository singleplayerGameRepository;

    @InjectMocks
    private PlayerService playerService;

    private Player testPlayer;

    @BeforeEach
    void setUp() {
        testPlayer = new Player();
        testPlayer.setPlayerId(UUID.randomUUID());
        testPlayer.setUsername("testuser");
        testPlayer.setPassword("encodedpassword");
        testPlayer.setEmail("test@example.com");
        testPlayer.setRole("ROLE_USER");
    }

    @Test
    void testFindPlayerByUsername_Success() {
        // Given
        when(playerRepository.existsByUsername("testuser")).thenReturn(true);
        when(playerRepository.findByUsername("testuser")).thenReturn(Optional.of(testPlayer));

        // When
        Player result = playerService.findPlayerByUsername("testuser");

        // Then
        assertNotNull(result);
        assertEquals(testPlayer, result);
        verify(playerRepository).existsByUsername("testuser");
        verify(playerRepository).findByUsername("testuser");
    }

    @Test
    void testFindPlayerByUsername_NotFound() {
        // Given
        when(playerRepository.existsByUsername("nonexistent")).thenReturn(false);

        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> 
            playerService.findPlayerByUsername("nonexistent"));
        verify(playerRepository).existsByUsername("nonexistent");
        verify(playerRepository, never()).findByUsername(any());
    }

    @Test
    void testReturnPlayerPastGames() {
        // Given
        UUID playerId = testPlayer.getPlayerId();
        Map<String, List<PastGame>> expectedGames = Map.of(
            "finished", List.of(),
            "unfinished", List.of()
        );
        when(singleplayerGameService.getPastGamesByPlayerID(playerId)).thenReturn(expectedGames);

        // When
        Map<String, List<PastGame>> result = playerService.returnPlayerPastGames(playerId);

        // Then
        assertNotNull(result);
        assertEquals(expectedGames, result);
        verify(singleplayerGameService).getPastGamesByPlayerID(playerId);
    }
}
