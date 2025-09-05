package com.example.mastermind.controllers;

import com.example.mastermind.customExceptions.UsernameExistsException;
import com.example.mastermind.customExceptions.UnauthenticatedUserException;
import com.example.mastermind.dataTransferObjects.PlayerDTOs.UserRegistrationRequest;
import com.example.mastermind.services.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for authentication and user registration operations.
 * <p>
 * This controller handles user registration and provides current user information.
 * Note that user login is not implemented here as Spring Security automatically
 * handles form-based authentication using the configured UserDetailsService and
 * PasswordEncoder. The login process is managed entirely by Spring Security's
 * built-in authentication system.
 * <p>
 * Endpoints:
 * - GET /auth - Returns current authenticated user's username
 * - POST /auth/register - Registers a new user in the system
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * Returns information about the currently authenticated user.
     * <p>
     * This endpoint is used by the frontend to check authentication status
     * and display the username in the navbar. It works in conjunction with
     * Spring Security's form-based authentication system.
     * 
     * @param auth the authentication object provided by Spring Security
     * @return a ResponseEntity containing the username if authenticated
     * @throws UnauthenticatedUserException if no user is authenticated
     */
    @GetMapping("/auth")
    public ResponseEntity<?> getCurrentUser(Authentication auth) {
        if (auth == null) {
            throw new UnauthenticatedUserException();
        }
        return ResponseEntity.ok(Map.of("username", auth.getName()));
    }
    
    /**
     * Registers a new player in the system.
     * <p>
     * Accepts user registration details and creates a new account.
     * Validation and user creation are handled by the AuthService.
     * 
     * @param newUser DTO containing username, password, and email
     * @return ResponseEntity with success message on successful registration
     * @throws UsernameExistsException if username already exists
     */
    @PostMapping("auth/register")
    public ResponseEntity<?> registerNewPlayer(@RequestBody UserRegistrationRequest newUser) {
        try {
            authService.registerNewUser(newUser);
            return ResponseEntity.ok("Success");
        }  catch (Exception e) {
            throw e;
        }
    }
}
