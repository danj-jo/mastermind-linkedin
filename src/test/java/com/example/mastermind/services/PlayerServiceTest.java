package com.example.mastermind.services;

import com.example.mastermind.dataAccessObjects.PlayerRepository;
import com.example.mastermind.dataAccessObjects.SingleplayerGameRepository;
import com.example.mastermind.dataTransferObjects.GameDTOs.Response.CurrentUserPastGames;
import com.example.mastermind.models.entities.Player;
import com.example.mastermind.services.SingleplayerGameService;
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
    void testReturnCurrentPlayersPastGames() {
        // Given
        UUID playerId = testPlayer.getPlayerId();
        Map<String, List<CurrentUserPastGames>> expectedGames = Map.of(
            "finished", List.of(),
            "unfinished", List.of()
        );
        when(singleplayerGameService.returnCurrentUsersPastGames(playerId)).thenReturn(expectedGames);

        // When
        Map<String, List<CurrentUserPastGames>> result = playerService.returnCurrentPlayersPastGames(playerId);

        // Then
        assertNotNull(result);
        assertEquals(expectedGames, result);
        verify(singleplayerGameService).returnCurrentUsersPastGames(playerId);
    }
}
