package com.example.mastermind.services;

import com.example.mastermind.customExceptions.EmailExistsException;
import com.example.mastermind.customExceptions.PasswordTooShortException;
import com.example.mastermind.customExceptions.UsernameExistsException;
import com.example.mastermind.customExceptions.UsernameTooShortException;
import com.example.mastermind.dataAccessObjects.PlayerRepository;
import com.example.mastermind.dataTransferObjects.PlayerDTOs.Request.UserRegistrationRequest;
import com.example.mastermind.models.Player;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Service
@AllArgsConstructor

public class AuthService {
    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;

    public void registerNewUser(UserRegistrationRequest newUser){
        String username = newUser.getUsername();
        String password = newUser.getPassword();
        String email = newUser.getEmail();
            if (username == null || username.length() < 5) {
                throw new UsernameTooShortException();
            }
            // if username does n
            if (!username.matches("(?=(?:.*[A-Za-z]){2,}).*")) {
                throw new IllegalArgumentException();
            }

            // if username exists already
            if (playerRepository.existsByUsername(username)) {
                throw new UsernameExistsException();
            }
            if(playerRepository.existsByEmail(email)){
                throw new EmailExistsException();
            }
            // if password is too short or empty
            if (password == null || password.length() < 6) {
                throw new PasswordTooShortException();
            }


            Player newPlayer = new Player();
            newPlayer.setUsername(username);
            newPlayer.setPassword(passwordEncoder.encode(password));
            newPlayer.setEmail(email);
            newPlayer.setRole("ROLE_USER");
            playerRepository.saveAndFlush(newPlayer);
    }



}
