package com.example.mastermind.services;

import com.example.mastermind.customExceptions.EmailExistsException;
import com.example.mastermind.customExceptions.PasswordTooShortException;
import com.example.mastermind.customExceptions.UsernameExistsException;
import com.example.mastermind.customExceptions.UsernameTooShortException;
import com.example.mastermind.dataAccessObjects.PlayerRepository;
import com.example.mastermind.dataTransferObjects.PlayerDTOs.Request.UserLoginRequest;
import com.example.mastermind.dataTransferObjects.PlayerDTOs.Request.UserRegistrationRequest;
import com.example.mastermind.models.Player;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    public UUID authenticatePlayer(UserLoginRequest existingUser){
        String username = existingUser.getUsername();
        String rawPassword = existingUser.getPassword();
        Player authenticatedUser = new Player();
            boolean isPresent = playerRepository.existsByUsername(username);
            if(isPresent){
                authenticatedUser = playerRepository.findByUsername(username);
            }
            if(!isPresent){
                throw new RuntimeException("User does not exist.");
            }
            if (!passwordEncoder.matches(rawPassword, authenticatedUser.getPassword())) {
                throw new BadCredentialsException("Please check password.");
            }

       return authenticatedUser.getPlayerId();
    }

}
