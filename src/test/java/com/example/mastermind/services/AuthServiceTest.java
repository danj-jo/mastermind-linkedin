package com.example.mastermind.services;

import com.example.mastermind.customExceptions.EmailExistsException;
import com.example.mastermind.customExceptions.PasswordTooShortException;
import com.example.mastermind.customExceptions.UsernameExistsException;
import com.example.mastermind.customExceptions.UsernameTooShortException;
import com.example.mastermind.repositoryLayer.PlayerRepository;
import com.example.mastermind.dataTransferObjects.PlayerDTOs.UserRegistrationRequest;
import com.example.mastermind.models.entities.Player;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private UserRegistrationRequest validRequest;
    public boolean isValidUsername(String username) {
        // 5-20 chars, letters/numbers/._ allowed, no consecutive dots
        String regex = "^(?!.*\\.\\.)[a-zA-Z0-9._]{5,20}$";
        return username.matches(regex);
    }

    @BeforeEach
    void setUp() {
        validRequest = new UserRegistrationRequest();
        validRequest.setUsername("testuser");
        validRequest.setPassword("password123");
        validRequest.setEmail("test@example.com");
    }

    @Test
    public void testInvalidUsernames() {
        assertFalse(isValidUsername("user..name")); // consecutive dots
        assertFalse(isValidUsername("us")); // too short
        assertFalse(isValidUsername("thisusernameiswaytoolongtobevalid")); // too long
        assertFalse(isValidUsername("user!name")); // invalid character
    }
    @Test
    void testRegisterNewUser_Success() {
        // Given
        when(playerRepository.existsByUsername("testuser")).thenReturn(false);
        when(playerRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedpassword");
        when(playerRepository.saveAndFlush(any(Player.class))).thenReturn(new Player());

        // When & Then
        assertDoesNotThrow(() -> authService.registerNewUser(validRequest));
        
        verify(passwordEncoder).encode("password123");
        verify(playerRepository).saveAndFlush(any(Player.class));
    }

    @Test
    void testRegisterNewUser_UsernameTooShort() {
        // Given
        validRequest.setUsername("abc");

        // When & Then
        assertThrows(UsernameTooShortException.class, () -> authService.registerNewUser(validRequest));
        verify(playerRepository, never()).saveAndFlush(any());
    }

    @Test
    void testRegisterNewUser_UsernameExists() {
        // Given
        when(playerRepository.existsByUsername("testuser")).thenReturn(true);

        // When & Then
        assertThrows(UsernameExistsException.class, () -> authService.registerNewUser(validRequest));
        verify(playerRepository, never()).saveAndFlush(any());
    }

    @Test
    void testRegisterNewUser_EmailExists() {
        // Given
        when(playerRepository.existsByUsername("testuser")).thenReturn(false);
        when(playerRepository.existsByEmail("test@example.com")).thenReturn(true);

        // When & Then
        assertThrows(EmailExistsException.class, () -> authService.registerNewUser(validRequest));
        verify(playerRepository, never()).saveAndFlush(any());
    }

    @Test
    void testRegisterNewUser_PasswordTooShort() {
        // Given
        validRequest.setPassword("123");

        // When & Then
        assertThrows(PasswordTooShortException.class, () -> authService.registerNewUser(validRequest));
        verify(playerRepository, never()).saveAndFlush(any());
    }
}
