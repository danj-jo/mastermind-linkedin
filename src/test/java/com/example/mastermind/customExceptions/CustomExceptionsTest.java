package com.example.mastermind.customExceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomExceptionsTest {

    @Test
    void emailExistsException_WithDefaultConstructor_ReturnsDefaultMessage() {
        // When
        EmailExistsException exception = new EmailExistsException();

        // Then
        assertEquals("Email already exists.", exception.getMessage());
    }

    @Test
    void emailExistsException_WithCustomMessage_ReturnsCustomMessage() {
        // Given
        String customMessage = "Custom email error";

        // When
        EmailExistsException exception = new EmailExistsException(customMessage);

        // Then
        assertNull(exception.getMessage()); // Note: The constructor calls super() without message
    }

    @Test
    void gameNotFoundException_WithDefaultConstructor_ReturnsDefaultMessage() {
        // When
        GameNotFoundException exception = new GameNotFoundException();

        // Then
        assertEquals("Game not found.", exception.getMessage());
    }

    @Test
    void gameNotFoundException_WithCustomMessage_ReturnsCustomMessage() {
        // Given
        String customMessage = "Custom game not found error";

        // When
        GameNotFoundException exception = new GameNotFoundException(customMessage);

        // Then
        assertEquals(customMessage, exception.getMessage());
    }

    @Test
    void usernameExistsException_WithDefaultConstructor_ReturnsDefaultMessage() {
        // When
        UsernameExistsException exception = new UsernameExistsException();

        // Then
        assertEquals("Username already exists.", exception.getMessage());
    }

    @Test
    void usernameExistsException_WithCustomMessage_ReturnsCustomMessage() {
        // Given
        String customMessage = "Custom username error";

        // When
        UsernameExistsException exception = new UsernameExistsException(customMessage);

        // Then
        assertEquals(customMessage, exception.getMessage());
    }

    @Test
    void forbiddenActionException_WithDefaultConstructor_ReturnsDefaultMessage() {
        // When
        ForbiddenActionException exception = new ForbiddenActionException();

        // Then
        assertEquals("PlayerID is not stored. User must log in.", exception.getMessage());
    }

    @Test
    void forbiddenActionException_WithCustomMessage_ReturnsNullMessage() {
        // Given
        String customMessage = "Custom forbidden error";

        // When
        ForbiddenActionException exception = new ForbiddenActionException(customMessage);

        // Then
        assertNull(exception.getMessage()); // Note: The constructor calls super() without message
    }

    @Test
    void playerNotFoundException_WithDefaultConstructor_ReturnsDefaultMessage() {
        // When
        PlayerNotFoundException exception = new PlayerNotFoundException();

        // Then
        assertEquals("Player not found.", exception.getMessage());
    }

    @Test
    void playerNotFoundException_WithCustomMessage_ReturnsCustomMessage() {
        // Given
        String customMessage = "Custom player not found error";

        // When
        PlayerNotFoundException exception = new PlayerNotFoundException(customMessage);

        // Then
        assertEquals(customMessage, exception.getMessage());
    }

    @Test
    void gameCreationException_WithDefaultConstructor_ReturnsDefaultMessage() {
        // When
        GameCreationException exception = new GameCreationException();

        // Then
        assertEquals("Game creation failed.", exception.getMessage());
    }

    @Test
    void gameCreationException_WithCustomMessage_ReturnsCustomMessage() {
        // Given
        String customMessage = "Custom game creation error";

        // When
        GameCreationException exception = new GameCreationException(customMessage);

        // Then
        assertEquals(customMessage, exception.getMessage());
    }

    @Test
    void gameUpdateException_WithDefaultConstructor_ReturnsDefaultMessage() {
        // When
        GameUpdateException exception = new GameUpdateException();

        // Then
        assertEquals("Game update failed.", exception.getMessage());
    }

    @Test
    void gameUpdateException_WithCustomMessage_ReturnsCustomMessage() {
        // Given
        String customMessage = "Custom game update error";

        // When
        GameUpdateException exception = new GameUpdateException(customMessage);

        // Then
        assertEquals(customMessage, exception.getMessage());
    }

    @Test
    void guessProcessingException_WithDefaultConstructor_ReturnsDefaultMessage() {
        // When
        GuessProcessingException exception = new GuessProcessingException();

        // Then
        assertEquals("Guess processing failed.", exception.getMessage());
    }

    @Test
    void guessProcessingException_WithCustomMessage_ReturnsCustomMessage() {
        // Given
        String customMessage = "Custom guess processing error";

        // When
        GuessProcessingException exception = new GuessProcessingException(customMessage);

        // Then
        assertEquals(customMessage, exception.getMessage());
    }

    @Test
    void noActiveGameSessionException_WithDefaultConstructor_ReturnsDefaultMessage() {
        // When
        NoActiveGameSessionException exception = new NoActiveGameSessionException();

        // Then
        assertEquals("No active game session found.", exception.getMessage());
    }

    @Test
    void noActiveGameSessionException_WithCustomMessage_ReturnsCustomMessage() {
        // Given
        String customMessage = "Custom no active game session error";

        // When
        NoActiveGameSessionException exception = new NoActiveGameSessionException(customMessage);

        // Then
        assertEquals(customMessage, exception.getMessage());
    }

    @Test
    void passwordTooShortException_WithDefaultConstructor_ReturnsDefaultMessage() {
        // When
        PasswordTooShortException exception = new PasswordTooShortException();

        // Then
        assertEquals("Password is too short.", exception.getMessage());
    }

    @Test
    void passwordTooShortException_WithCustomMessage_ReturnsCustomMessage() {
        // Given
        String customMessage = "Custom password too short error";

        // When
        PasswordTooShortException exception = new PasswordTooShortException(customMessage);

        // Then
        assertEquals(customMessage, exception.getMessage());
    }

    @Test
    void playerDataAccessException_WithDefaultConstructor_ReturnsDefaultMessage() {
        // When
        PlayerDataAccessException exception = new PlayerDataAccessException();

        // Then
        assertEquals("Player data access failed.", exception.getMessage());
    }

    @Test
    void playerDataAccessException_WithCustomMessage_ReturnsCustomMessage() {
        // Given
        String customMessage = "Custom player data access error";

        // When
        PlayerDataAccessException exception = new PlayerDataAccessException(customMessage);

        // Then
        assertEquals(customMessage, exception.getMessage());
    }

    @Test
    void unauthenticatedUserException_WithDefaultConstructor_ReturnsDefaultMessage() {
        // When
        UnauthenticatedUserException exception = new UnauthenticatedUserException();

        // Then
        assertEquals("User is not authenticated.", exception.getMessage());
    }

    @Test
    void unauthenticatedUserException_WithCustomMessage_ReturnsCustomMessage() {
        // Given
        String customMessage = "Custom unauthenticated user error";

        // When
        UnauthenticatedUserException exception = new UnauthenticatedUserException(customMessage);

        // Then
        assertEquals(customMessage, exception.getMessage());
    }

    @Test
    void unauthorizedGameAccessException_WithDefaultConstructor_ReturnsDefaultMessage() {
        // When
        UnauthorizedGameAccessException exception = new UnauthorizedGameAccessException();

        // Then
        assertEquals("Unauthorized access to game.", exception.getMessage());
    }

    @Test
    void unauthorizedGameAccessException_WithCustomMessage_ReturnsCustomMessage() {
        // Given
        String customMessage = "Custom unauthorized game access error";

        // When
        UnauthorizedGameAccessException exception = new UnauthorizedGameAccessException(customMessage);

        // Then
        assertEquals(customMessage, exception.getMessage());
    }

    @Test
    void usernameContainsInvalidCharactersException_WithDefaultConstructor_ReturnsDefaultMessage() {
        // When
        UsernameContainsInvalidCharactersException exception = new UsernameContainsInvalidCharactersException();

        // Then
        assertEquals("Username contains invalid characters.", exception.getMessage());
    }

    @Test
    void usernameContainsInvalidCharactersException_WithCustomMessage_ReturnsCustomMessage() {
        // Given
        String customMessage = "Custom username invalid characters error";

        // When
        UsernameContainsInvalidCharactersException exception = new UsernameContainsInvalidCharactersException(customMessage);

        // Then
        assertEquals(customMessage, exception.getMessage());
    }

    @Test
    void usernameTooShortException_WithDefaultConstructor_ReturnsDefaultMessage() {
        // When
        UsernameTooShortException exception = new UsernameTooShortException();

        // Then
        assertEquals("Username is too short.", exception.getMessage());
    }

    @Test
    void usernameTooShortException_WithCustomMessage_ReturnsCustomMessage() {
        // Given
        String customMessage = "Custom username too short error";

        // When
        UsernameTooShortException exception = new UsernameTooShortException(customMessage);

        // Then
        assertEquals(customMessage, exception.getMessage());
    }
}