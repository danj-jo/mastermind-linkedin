package com.example.mastermind.controllers;

import com.example.mastermind.dataTransferObjects.GameDTOs.Request.GameSearchRequest;
import com.example.mastermind.dataTransferObjects.GameDTOs.Request.UpdatedGameRequest;
import com.example.mastermind.models.Game;
import com.example.mastermind.models.Player;
import com.example.mastermind.services.GameService;
import com.example.mastermind.services.PlayerService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@AllArgsConstructor
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;
    private final PlayerService playerService;

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    @PreAuthorize("hasRole('USER')")
    @PostMapping(value = "/new", produces = "application/json")
    public ResponseEntity<?> createNewGame(@RequestBody Map<String, String> newGame, HttpSession session) {
        try {

            Authentication auth = SecurityContextHolder.getContext()
                                                       .getAuthentication();
            String username = auth.getName();
            Player player = playerService.findByUsername(username);
            UUID playerId = player.getPlayerId();
            String difficulty = newGame.get("difficulty")
                                       .toUpperCase();
            int numbersToGuess = 0;
            switch (difficulty) {
                case "EASY" -> numbersToGuess = 4;
                case "MEDIUM" -> numbersToGuess = 6;
                case "HARD" -> numbersToGuess = 9;
            }
            Game game = gameService.createNewGame(difficulty, playerId);
            session.setAttribute("gameId", game.getGameId());
            return new ResponseEntity<>(new HashMap<>(Map.of("numbersToGuess", numbersToGuess)), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new HashMap<>(Map.of("Error in guess method:", e.getMessage())), HttpStatus.OK);
        }
    }

    // specifically for game creation, bundled with createNewGame
    @PreAuthorize("hasRole('USER')")
    @PostMapping(value = "/guess", produces = "application/json")
    public ResponseEntity<?> guess(@RequestBody Map<String, String> guess, HttpSession session) {
        try {
            Authentication auth = SecurityContextHolder.getContext()
                                                       .getAuthentication();
            String username = auth.getName();
            Player player = playerService.findByUsername(username);
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
            return ResponseEntity.ok(Map.of("feedback", feedback,
                                            "finished", finished));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                                 .body(new HashMap<>(Map.of("Error:", e.getMessage())));
        }
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/update")
    public ResponseEntity<?> updateGame(@RequestBody UpdatedGameRequest updatedGame) {
        try {
            UUID gameId = updatedGame.getGameId();
            String guess = updatedGame.getGuess();
            Authentication auth = SecurityContextHolder.getContext()
                                                       .getAuthentication();
            String username = auth.getName();
            Player player = playerService.findByUsername(username);
            UUID playerId = player.getPlayerId();

            Game game = gameService.findById(updatedGame.getGameId());
            if (game.getPlayer()
                    .getPlayerId() != playerId) {
                return new ResponseEntity<>(new HashMap<>(Map.of("Error", "You are not authorized to do this.")), HttpStatus.UNAUTHORIZED);
            }
            String feedback = gameService.makeGuess(gameId,guess);
            String finished = gameService.isGameFinished(gameId);
            return ResponseEntity.ok(Map.of("feedback", feedback,
                                            "finished", finished));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                                 .body(new HashMap<>(Map.of("Error:", e.getMessage())));
        }

    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public ResponseEntity<?> findGameDetails(@PathVariable UUID id){
try{

    Game game = gameService.findById(id);
    Authentication auth = SecurityContextHolder.getContext()
                                               .getAuthentication();
    String username = auth.getName();
    Player player = playerService.findByUsername(username);
    UUID playerId = player.getPlayerId();
    if(game.getPlayer().getPlayerId() != playerId){
        throw new RuntimeException("Hmmm...");
    }
        return new ResponseEntity<>(new HashMap<>(Map.of("numbersToGuess", game.getWinningNumber().length())),HttpStatus.OK);
} catch(Exception e){
    return new ResponseEntity<>(new HashMap<>(Map.of("error", e.getMessage())),HttpStatus.BAD_REQUEST);
}
}
}