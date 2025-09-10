package com.example.mastermind.services;

import com.example.mastermind.models.entities.Player;
import com.example.mastermind.repositoryLayer.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
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
        testPlayer = new Player(
                UUID.randomUUID(),
                "testuser",
                "password123",
                "test@example.com",
                "USER"
        );
    }

    @Test
    void findPlayerByUsername_WithExistingUsername_ReturnsPlayer() {
        // Given
        when(playerRepository.findByUsername("testuser")).thenReturn(Optional.of(testPlayer));

        // When
        Player result = playerService.findPlayerByUsername("testuser");

        // Then
        assertEquals(testPlayer, result);
        verify(playerRepository).findByUsername("testuser");
    }

    @Test
    void findPlayerByUsername_WithNonExistentUsername_ThrowsException() {
        // Given
        when(playerRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> 
                playerService.findPlayerByUsername("nonexistent"));
        verify(playerRepository).findByUsername("nonexistent");
    }

    @Test
    void findPlayerById_WithExistingId_ReturnsPlayer() {
        // Given
        UUID playerId = testPlayer.getPlayerId();
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(testPlayer));

        // When
        Player result = playerService.findPlayerById(playerId);

        // Then
        assertEquals(testPlayer, result);
        verify(playerRepository).findById(playerId);
    }

    @Test
    void findPlayerById_WithNonExistentId_ThrowsException() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(playerRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> 
                playerService.findPlayerById(nonExistentId));
        verify(playerRepository).findById(nonExistentId);
    }
}