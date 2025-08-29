package com.example.mastermind.controllers;

import com.example.mastermind.customExceptions.UsernameExistsException;
import com.example.mastermind.dataTransferObjects.ErrorDTOs.ErrorDtoFactory;
import com.example.mastermind.dataTransferObjects.PlayerDTOs.Request.UserLoginRequest;
import com.example.mastermind.dataTransferObjects.PlayerDTOs.Request.UserRegistrationRequest;
import com.example.mastermind.services.AuthService;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<?> registerNewPlayer(@RequestBody UserRegistrationRequest newUser) {
        try {
            authService.registerNewUser(newUser);
            return new ResponseEntity<>(
                    new HashMap<>(Map.of("Success", "User Created")),
                    HttpStatus.CREATED
            );
        }  catch (UsernameExistsException e) {
            Map<String,String> error = ErrorDtoFactory.toMap(e);
            return new ResponseEntity<>(
                    error,
                    HttpStatus.CONFLICT
            );
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginRequest user, HttpSession session) {
        try {
            UUID playerId = authService.authenticatePlayer(user);
            session.setAttribute("playerId", playerId);
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            null,
                            List.of(new SimpleGrantedAuthority("USER"))
                    );
            SecurityContextHolder.getContext()
                                 .setAuthentication(auth);

            // Store context in session
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
            return new ResponseEntity<>(
                    new HashMap<>(Map.of("Success", "User Logged in.")),
                    HttpStatus.OK
            );
        } catch (Exception e) {

            return new ResponseEntity<>(
                    new HashMap<>(Map.of("Error", e.getMessage())),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}
