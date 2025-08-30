package com.example.mastermind.controllers;

import com.example.mastermind.customExceptions.UsernameExistsException;
import com.example.mastermind.dataTransferObjects.ErrorDTOs.ErrorDtoFactory;
import com.example.mastermind.dataTransferObjects.PlayerDTOs.Request.UserRegistrationRequest;
import com.example.mastermind.services.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

import java.util.Map;

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


}
