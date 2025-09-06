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
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

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
        when(playerRepository.findByUsername("testuser")).thenReturn(Optional.of(testPlayer));

        // When
        Player result = playerService.findPlayerByUsername("testuser");

        // Then
        assertNotNull(result);
        assertEquals(testPlayer, result);
        verify(playerRepository).findByUsername("testuser");
    }

    @Test
    void testFindPlayerByUsername_NotFound() {
        // Given
        when(playerRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NoSuchElementException.class, () -> 
            playerService.findPlayerByUsername("nonexistent"));
        verify(playerRepository).findByUsername("nonexistent");
    }

    // Note: returnPlayerPastGames method no longer exists in PlayerService
    // This functionality has been moved to SingleplayerGameService
}
