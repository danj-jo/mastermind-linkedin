package com.example.mastermind.controllers;

import com.example.mastermind.models.PastGame;
import com.example.mastermind.dataTransferObjects.GameDTOs.Response.UserProfileDao;
import com.example.mastermind.models.entities.Player;
import com.example.mastermind.services.AuthService;
import com.example.mastermind.services.PlayerService;
import com.example.mastermind.services.SingleplayerGameService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.mastermind.customExceptions.PlayerNotFoundException;

import java.util.HashMap;
import java.util.UUID;
import java.util.Map;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;

import static com.example.mastermind.services.AuthService.getCurrentAuthenticatedPlayerUsername;

/**
 * Controller for player-related operations.
 * 
 * Note: All exceptions thrown by methods in this controller are automatically handled 
 * by the GlobalExceptionHandler, which converts them to appropriate HTTP responses 
 * with status codes and error messages.
 */
@AllArgsConstructor
@RestController
@RequestMapping("/me")
public class PlayerController {
    private final PlayerService playerService;
    private final SingleplayerGameService singleplayerGameService;
    private final AuthService authService;

    /**
     * This method is used to return the profile details of the current user.
     *
     * @return a ResponseEntity containing the UserProfileDao with username and email
     * @throws PlayerNotFoundException if the current user is not found in the database
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/")
    public ResponseEntity<UserProfileDao> getCurrentUserProfile(){
        String username = getCurrentAuthenticatedPlayerUsername();
        Player player = playerService.findPlayerByUsername(username);
        if (player == null) {
            throw new PlayerNotFoundException("Player not found for username: " + username);
        }
        String email = player.getEmail();
        UserProfileDao currentUser = new UserProfileDao(username,email);
        return ResponseEntity.ok(currentUser);
    }


    /**
     * This method is used to return all of a user's past games, complete and incomplete. It does not contain try catch blocks or thrown errors because if the list is empty, it will just return an empty list.
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/games")
    public ResponseEntity<Map <String, List<PastGame>>> getCurrentUserPastGames(){
        UUID id = authService.getCurrentAuthenticatedPlayerId();
        List<PastGame> finishedGames = singleplayerGameService.getFinishedGamesByPlayerId(id);
        List<PastGame> unfinishedGames = singleplayerGameService.getUnfinishedGamesByPlayerId(id);
        return ResponseEntity.ok(new HashMap<>(Map.of("finished",finishedGames,"unfinished",unfinishedGames)));
    }





}
