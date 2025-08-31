package com.example.mastermind.controllers;

import com.example.mastermind.dataAccessObjects.PlayerRepository;
import com.example.mastermind.dataTransferObjects.GameDTOs.GameResponseDtoFactory;
import com.example.mastermind.models.Game;
import com.example.mastermind.models.Player;
import com.example.mastermind.models.Result;
import com.example.mastermind.services.GameService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@AllArgsConstructor
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;
    private final PlayerRepository playerRepository;

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    @PreAuthorize("hasRole('USER')")
    @PostMapping(value = "/new", produces = "application/json")
    public ResponseEntity<?> createNewGame(@RequestBody Map<String,String> newGame, HttpSession session){
        try {

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            Player player = playerRepository.findByUsername(username)
                                            .orElseThrow(() -> new RuntimeException("User not found"));
            UUID playerId = player.getPlayerId();
            String difficulty = newGame.get("difficulty").toUpperCase();
            int numbersToGuess = 0;
            switch(difficulty){
                case "EASY" -> numbersToGuess = 4;
                case "MEDIUM" -> numbersToGuess = 6;
                case "HARD" -> numbersToGuess = 9;
            }
            Game game = gameService.createNewGame(difficulty, playerId);
            session.setAttribute("gameId", game.getGameId());
            return new ResponseEntity<>(new HashMap<>(Map.of("numbersToGuess",numbersToGuess)), HttpStatus.CREATED);
        } catch(Exception e){
            return new ResponseEntity<>(new HashMap<>(Map.of("Error in guess method:", e.getMessage())), HttpStatus.OK);
        }
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping(value = "/guess", produces = "application/json")
    public ResponseEntity<?> guess(@RequestBody Map<String, String> guess, HttpSession session){
        try {
            Authentication auth = SecurityContextHolder.getContext()
                                                       .getAuthentication();
            String username = auth.getName();
            Player player = playerRepository.findByUsername(username)
                                            .orElseThrow(() -> new RuntimeException("User not found"));
            UUID playerId = player.getPlayerId();
            if (playerId == null) {
                return new ResponseEntity<>("User not logged in", HttpStatus.UNAUTHORIZED);
            }
            UUID gameId = (UUID) session.getAttribute("gameId");
            if (gameId == null) {
                return new ResponseEntity<>("No active game session. Start a new game first.", HttpStatus.BAD_REQUEST);
            }
            String feedback = gameService.makeGuess(gameId, guess.get("guess"));
            String finished = gameService.isGameFinished(gameId);
            return new ResponseEntity<>(Map.of("feedback", feedback,
                                               "finished", finished
            ), HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>(new HashMap<>(Map.of("Error:", e.getMessage())), HttpStatus.BAD_REQUEST);
        }
    }
}
