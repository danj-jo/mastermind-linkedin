package com.example.mastermind.services;

import com.example.mastermind.customExceptions.*;
import com.example.mastermind.dataTransferObjects.PlayerDTOs.UserRegistrationRequest;
import com.example.mastermind.models.entities.Player;
import com.example.mastermind.repositoryLayer.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PlayerService playerService;

    @InjectMocks
    private AuthService authService;

    private UserRegistrationRequest validRequest;
    private Player testPlayer;

    @BeforeEach
    void setUp() {
        validRequest = new UserRegistrationRequest();
        validRequest.setUsername("validuser");
        validRequest.setPassword("password123");
        validRequest.setEmail("valid@example.com");

        testPlayer = new Player(
                UUID.randomUUID(),
                "validuser",
                "encodedpassword",
                "valid@example.com",
                "USER"
        );
    }

    @Test
    void registerNewUser_WithValidData_RegistersUser() {
        // Given
        when(playerRepository.existsByUsername("validuser")).thenReturn(false);
        when(playerRepository.existsByEmail("valid@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedpassword");

        // When
        authService.registerNewUser(validRequest);

        // Then
        verify(playerRepository).existsByUsername("validuser");
        verify(playerRepository).existsByEmail("valid@example.com");
        verify(passwordEncoder).encode("password123");
        verify(playerRepository).saveAndFlush(any(Player.class));
    }

    @Test
    void registerNewUser_WithExistingUsername_ThrowsUsernameExistsException() {
        // Given
        when(playerRepository.existsByUsername("validuser")).thenReturn(true);

        // When & Then
        assertThrows(UsernameExistsException.class, () -> 
                authService.registerNewUser(validRequest));
        verify(playerRepository).existsByUsername("validuser");
        verify(playerRepository, never()).saveAndFlush(any());
    }

    @Test
    void registerNewUser_WithExistingEmail_ThrowsEmailExistsException() {
        // Given
        when(playerRepository.existsByUsername("validuser")).thenReturn(false);
        when(playerRepository.existsByEmail("valid@example.com")).thenReturn(true);

        // When & Then
        assertThrows(EmailExistsException.class, () -> 
                authService.registerNewUser(validRequest));
        verify(playerRepository).existsByUsername("validuser");
        verify(playerRepository).existsByEmail("valid@example.com");
        verify(playerRepository, never()).saveAndFlush(any());
    }

    @Test
    void registerNewUser_WithShortPassword_ThrowsPasswordTooShortException() {
        // Given
        validRequest.setPassword("123");

        // When & Then
        assertThrows(PasswordTooShortException.class, () -> 
                authService.registerNewUser(validRequest));
        verify(playerRepository, never()).saveAndFlush(any());
    }

    @Test
    void registerNewUser_WithInvalidUsername_ThrowsIllegalArgumentException() {
        // Given
        validRequest.setUsername("ab"); // Too short

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
                authService.registerNewUser(validRequest));
        verify(playerRepository, never()).saveAndFlush(any());
    }

    @Test
    void getCurrentAuthenticatedPlayerId_WithValidAuth_ReturnsPlayerId() {
        // Given
        when(playerRepository.findByUsername("testuser")).thenReturn(Optional.of(testPlayer));

        try (MockedStatic<AuthService> mockedStatic = Mockito.mockStatic(AuthService.class)) {
            mockedStatic.when(AuthService::getCurrentAuthenticatedPlayerUsername).thenReturn("testuser");

            // When
            UUID result = authService.getCurrentAuthenticatedPlayerId();

            // Then
            assertEquals(testPlayer.getPlayerId(), result);
            verify(playerRepository).findByUsername("testuser");
        }
    }

    @Test
    void getCurrentAuthenticatedPlayer_WithValidAuth_ReturnsPlayer() {
        // Given
        when(playerService.findPlayerByUsername("testuser")).thenReturn(testPlayer);

        try (MockedStatic<AuthService> mockedStatic = Mockito.mockStatic(AuthService.class)) {
            mockedStatic.when(AuthService::getCurrentAuthenticatedPlayerUsername).thenReturn("testuser");

            // When
            Player result = authService.getCurrentAuthenticatedPlayer();

            // Then
            assertEquals(testPlayer, result);
            verify(playerService).findPlayerByUsername("testuser");
        }
    }
}