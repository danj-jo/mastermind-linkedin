package com.example.mastermind.controllers;

import com.example.mastermind.dataAccessObjects.PlayerRepository;
import com.example.mastermind.dataTransferObjects.GameDTOs.GameResponseDtoFactory;
import com.example.mastermind.models.Game;
import com.example.mastermind.models.Player;
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

            String difficulty = String.valueOf(newGame.get("difficulty"));
            Game game = gameService.createNewGame(difficulty, playerId);
            Map<String, String> response = GameResponseDtoFactory.NewGameResponseDTO(difficulty);
            session.setAttribute("playerId", playerId);
            session.setAttribute("gameId", game.getGameId());
            session.setAttribute("guesses_copy", game.getGuesses());
            System.out.println(session.getAttribute("playerId"));
            System.out.println(session.getAttribute("gameId"));
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch(Exception e){
            return new ResponseEntity<>(Map.of("PlayerId:",session.getAttribute("playerId").toString(),
                                               "GameId", session.getAttribute("gameId").toString()
                                               ), HttpStatus.OK);
        }
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping(value = "/guess", produces = "application/json")
    public ResponseEntity<?> guess(@RequestBody Map<String, String> guess, HttpSession session){
        UUID playerId = (UUID) session.getAttribute("playerId");
        if (playerId == null) {
            return new ResponseEntity<>("User not logged in",HttpStatus.UNAUTHORIZED);
        }
        UUID gameId = (UUID) session.getAttribute("gameId");
        if (gameId == null) {
            return new ResponseEntity<>("No active game session. Start a new game first.",HttpStatus.BAD_REQUEST);
        }

        String response = guess != null ? guess.get("guess") : null;
        if (response == null || response.isBlank()) {
            return new ResponseEntity<>("Guess is required",HttpStatus.BAD_REQUEST);
        }
        Object listOfGuesses = session.getAttribute("guesses");
        List<String> localGuesses;
        if (listOfGuesses instanceof java.util.List) {
            localGuesses = (ArrayList<String>) listOfGuesses;
        } else {
            localGuesses = new java.util.ArrayList<>();
        }
        localGuesses.add(guess.get("guess"));
        session.setAttribute("guesses", localGuesses);

        String feedback = gameService.makeGuess(gameId, guess.get("guess"));
        Map<String,String> responseMap = GameResponseDtoFactory.GuessResponseDTO(localGuesses, feedback);
        return new ResponseEntity<>(responseMap, HttpStatus.OK);
    }
}
