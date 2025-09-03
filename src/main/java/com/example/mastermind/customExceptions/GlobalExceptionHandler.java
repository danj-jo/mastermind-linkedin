package com.example.mastermind.customExceptions;

import com.example.mastermind.dataTransferObjects.ErrorDTOs.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthenticatedUserException.class)
    public ResponseEntity<Map<String, String>> handleUnauthenticated(UnauthenticatedUserException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorResponseDTO.toMap(e));
    }

    @ExceptionHandler(UnauthorizedGameAccessException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorizedGameAccess(UnauthorizedGameAccessException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorResponseDTO.toMap(e));
    }

    // Validation exceptions - 400 Bad Request
    @ExceptionHandler({PasswordTooShortException.class, UsernameTooShortException.class, 
                      UsernameExistsException.class, EmailExistsException.class})
    public ResponseEntity<Map<String, String>> handleValidationExceptions(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponseDTO.toMap(e));
    }

    // Forbidden actions - 403 Forbidden
    @ExceptionHandler(ForbiddenActionException.class)
    public ResponseEntity<Map<String, String>> handleForbiddenAction(ForbiddenActionException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponseDTO.toMap(e));
    }

    @ExceptionHandler({NoActiveGameSessionException.class, GameCreationException.class, GuessProcessingException.class, GameUpdateException.class, PlayerDataAccessException.class})
    public ResponseEntity<Map<String, String>> handleBadRequest(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponseDTO.toMap(e));
    }
    // 404 errors
    @ExceptionHandler({GameNotFoundException.class, PlayerNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleNotFound(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponseDTO.toMap(e));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneric(Exception e) {
        // Fallback to 500 with minimal leak of details
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("Error", "An unexpected error occurred."));
    }
}
