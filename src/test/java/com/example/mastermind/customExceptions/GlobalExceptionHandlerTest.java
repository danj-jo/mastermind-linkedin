package com.example.mastermind.customExceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleGameNotFoundException() {
        // Given
        GameNotFoundException exception = new GameNotFoundException("Game not found");

        // When
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleNotFound(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Game not found", response.getBody().get("Error"));
    }

    @Test
    void testHandlePlayerNotFoundException() {
        // Given
        PlayerNotFoundException exception = new PlayerNotFoundException("Player not found");

        // When
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleNotFound(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Player not found", response.getBody().get("Error"));
    }

    @Test
    void testHandleUsernameExistsException() {
        // Given
        UsernameExistsException exception = new UsernameExistsException();

        // When
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleValidationExceptions(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("Error"));
    }

    @Test
    void testHandleEmailExistsException() {
        // Given
        EmailExistsException exception = new EmailExistsException();

        // When
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleValidationExceptions(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("Error"));
    }

    @Test
    void testHandleUnauthorizedGameAccessException() {
        // Given
        UnauthorizedGameAccessException exception = new UnauthorizedGameAccessException();

        // When
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleUnauthorizedGameAccess(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("Error"));
    }

    @Test
    void testHandleForbiddenActionException() {
        // Given
        ForbiddenActionException exception = new ForbiddenActionException();

        // When
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleForbiddenAction(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("Error"));
    }

    @Test
    void testHandleGenericException() {
        // Given
        Exception exception = new Exception("Unexpected error");

        // When
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleGeneric(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("An unexpected error occurred.", response.getBody().get("Error"));
    }
}
