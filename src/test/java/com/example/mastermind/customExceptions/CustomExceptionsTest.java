package com.example.mastermind.customExceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomExceptionsTest {

    @Test
    void testUsernameExistsException() {
        // When
        UsernameExistsException exception = new UsernameExistsException();

        // Then
        assertNotNull(exception);
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testEmailExistsException() {
        // When
        EmailExistsException exception = new EmailExistsException();

        // Then
        assertNotNull(exception);
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testPasswordTooShortException() {
        // When
        PasswordTooShortException exception = new PasswordTooShortException();

        // Then
        assertNotNull(exception);
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testUsernameTooShortException() {
        // When
        UsernameTooShortException exception = new UsernameTooShortException();

        // Then
        assertNotNull(exception);
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testGameNotFoundException() {
        // When
        GameNotFoundException exception = new GameNotFoundException();

        // Then
        assertNotNull(exception);
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testPlayerNotFoundException() {
        // When
        PlayerNotFoundException exception = new PlayerNotFoundException();

        // Then
        assertNotNull(exception);
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testUnauthorizedGameAccessException() {
        // When
        UnauthorizedGameAccessException exception = new UnauthorizedGameAccessException();

        // Then
        assertNotNull(exception);
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testForbiddenActionException() {
        // When
        ForbiddenActionException exception = new ForbiddenActionException();

        // Then
        assertNotNull(exception);
        assertTrue(exception instanceof RuntimeException);
    }
}
