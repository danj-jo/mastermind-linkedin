package com.example.mastermind.customExceptions;

import com.example.mastermind.dataTransferObjects.ErrorDTOs.ErrorResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleUnauthenticated_ReturnsUnauthorizedStatus() {
        // Given
        UnauthenticatedUserException exception = new UnauthenticatedUserException("User not authenticated");
        Map<String, String> expectedResponse = Map.of("error", "User not authenticated");

        try (MockedStatic<ErrorResponseDTO> mockedStatic = Mockito.mockStatic(ErrorResponseDTO.class)) {
            mockedStatic.when(() -> ErrorResponseDTO.toMap(exception)).thenReturn(expectedResponse);

            // When
            ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleUnauthenticated(exception);

            // Then
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals(expectedResponse, response.getBody());
        }
    }

    @Test
    void handleUnauthorizedGameAccess_ReturnsUnauthorizedStatus() {
        // Given
        UnauthorizedGameAccessException exception = new UnauthorizedGameAccessException("Unauthorized access");
        Map<String, String> expectedResponse = Map.of("error", "Unauthorized access");

        try (MockedStatic<ErrorResponseDTO> mockedStatic = Mockito.mockStatic(ErrorResponseDTO.class)) {
            mockedStatic.when(() -> ErrorResponseDTO.toMap(exception)).thenReturn(expectedResponse);

            // When
            ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleUnauthorizedGameAccess(exception);

            // Then
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals(expectedResponse, response.getBody());
        }
    }

    @Test
    void handleValidationExceptions_WithPasswordTooShort_ReturnsBadRequestStatus() {
        // Given
        PasswordTooShortException exception = new PasswordTooShortException("Password too short");
        Map<String, String> expectedResponse = Map.of("error", "Password too short");

        try (MockedStatic<ErrorResponseDTO> mockedStatic = Mockito.mockStatic(ErrorResponseDTO.class)) {
            mockedStatic.when(() -> ErrorResponseDTO.toMap(exception)).thenReturn(expectedResponse);

            // When
            ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleValidationExceptions(exception);

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals(expectedResponse, response.getBody());
        }
    }

    @Test
    void handleValidationExceptions_WithUsernameExists_ReturnsBadRequestStatus() {
        // Given
        UsernameExistsException exception = new UsernameExistsException("Username exists");
        Map<String, String> expectedResponse = Map.of("error", "Username exists");

        try (MockedStatic<ErrorResponseDTO> mockedStatic = Mockito.mockStatic(ErrorResponseDTO.class)) {
            mockedStatic.when(() -> ErrorResponseDTO.toMap(exception)).thenReturn(expectedResponse);

            // When
            ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleValidationExceptions(exception);

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals(expectedResponse, response.getBody());
        }
    }

    @Test
    void handleForbiddenAction_ReturnsForbiddenStatus() {
        // Given
        ForbiddenActionException exception = new ForbiddenActionException("Forbidden action");
        Map<String, String> expectedResponse = Map.of("error", "Forbidden action");

        try (MockedStatic<ErrorResponseDTO> mockedStatic = Mockito.mockStatic(ErrorResponseDTO.class)) {
            mockedStatic.when(() -> ErrorResponseDTO.toMap(exception)).thenReturn(expectedResponse);

            // When
            ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleForbiddenAction(exception);

            // Then
            assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
            assertEquals(expectedResponse, response.getBody());
        }
    }

    @Test
    void handleBadRequest_WithGameCreationException_ReturnsBadRequestStatus() {
        // Given
        GameCreationException exception = new GameCreationException("Game creation failed");
        Map<String, String> expectedResponse = Map.of("error", "Game creation failed");

        try (MockedStatic<ErrorResponseDTO> mockedStatic = Mockito.mockStatic(ErrorResponseDTO.class)) {
            mockedStatic.when(() -> ErrorResponseDTO.toMap(exception)).thenReturn(expectedResponse);

            // When
            ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleBadRequest(exception);

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals(expectedResponse, response.getBody());
        }
    }

    @Test
    void handleNotFound_WithGameNotFoundException_ReturnsNotFoundStatus() {
        // Given
        GameNotFoundException exception = new GameNotFoundException("Game not found");
        Map<String, String> expectedResponse = Map.of("error", "Game not found");

        try (MockedStatic<ErrorResponseDTO> mockedStatic = Mockito.mockStatic(ErrorResponseDTO.class)) {
            mockedStatic.when(() -> ErrorResponseDTO.toMap(exception)).thenReturn(expectedResponse);

            // When
            ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleNotFound(exception);

            // Then
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertEquals(expectedResponse, response.getBody());
        }
    }

    @Test
    void handleNotFound_WithPlayerNotFoundException_ReturnsNotFoundStatus() {
        // Given
        PlayerNotFoundException exception = new PlayerNotFoundException("Player not found");
        Map<String, String> expectedResponse = Map.of("error", "Player not found");

        try (MockedStatic<ErrorResponseDTO> mockedStatic = Mockito.mockStatic(ErrorResponseDTO.class)) {
            mockedStatic.when(() -> ErrorResponseDTO.toMap(exception)).thenReturn(expectedResponse);

            // When
            ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleNotFound(exception);

            // Then
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertEquals(expectedResponse, response.getBody());
        }
    }

    @Test
    void handleGeneric_WithGenericException_ReturnsInternalServerErrorStatus() {
        // Given
        Exception exception = new Exception("Generic error");

        // When
        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleGeneric(exception);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(Map.of("Error", "An unexpected error occurred."), response.getBody());
    }

    @Test
    void handleGeneric_WithNullException_ReturnsInternalServerErrorStatus() {
        // When
        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleGeneric(null);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(Map.of("Error", "An unexpected error occurred."), response.getBody());
    }
}