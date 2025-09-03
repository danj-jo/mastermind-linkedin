package com.example.mastermind.services;

import com.example.mastermind.customExceptions.EmailExistsException;
import com.example.mastermind.customExceptions.PasswordTooShortException;
import com.example.mastermind.customExceptions.UsernameExistsException;
import com.example.mastermind.customExceptions.UsernameTooShortException;
import com.example.mastermind.dataAccessObjects.PlayerRepository;
import com.example.mastermind.dataTransferObjects.PlayerDTOs.Request.UserRegistrationRequest;
import com.example.mastermind.models.entities.Player;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Service
@AllArgsConstructor
/**
 * Service for user registration and authentication management.
 * 
 * This service handles user registration operations including validation of
 * username, password, and email requirements. Note that user login is not
 * implemented here as Spring Security automatically handles form-based
 * authentication using the configured UserDetailsService and PasswordEncoder.
 * 
 * The service works in conjunction with:
 * - Spring Security for authentication and session management
 * - UserDetailsService for user lookup during login
 * - PasswordEncoder for secure password hashing and verification
 */
public class AuthService {
    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * This method is used to register new users.
     * @param newUser - contains the username, password, and email of a registering user.
     */
    public void registerNewUser(UserRegistrationRequest newUser){
        String username = newUser.getUsername();
        String password = newUser.getPassword();
        String email = newUser.getEmail();
            if (username == null || username.length() < 5) {
                throw new UsernameTooShortException();
            }
            // checks if the username contains punction marks.
            if (!username.matches("(?=(?:.*[A-Za-z]){2,}).*")) {
                throw new IllegalArgumentException();
            }

            // checks if the username exists already
            if (playerRepository.existsByUsername(username)) {
                throw new UsernameExistsException();
            }
            if(playerRepository.existsByEmail(email)){
                throw new EmailExistsException();
            }
            // if the password is too short or empty
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
