package com.example.mastermind.controllers;

import com.example.mastermind.dataTransferObjects.GameDTOs.Response.UserProfileDao;
import com.example.mastermind.models.entities.Player;
import com.example.mastermind.services.PlayerService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.mastermind.customExceptions.PlayerDataAccessException;
import com.example.mastermind.customExceptions.UnauthenticatedUserException;
import com.example.mastermind.customExceptions.PlayerNotFoundException;
import com.example.mastermind.utils.PlayerUtils;
import java.util.UUID;
import java.util.Map;
import java.util.List;
import com.example.mastermind.dataTransferObjects.GameDTOs.Response.CurrentUserPastGames;
import org.springframework.web.bind.annotation.RequestMapping;
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
   

    /**
     * This method is used to return all of a user's past games, complete and incomplete.
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/games")
    public ResponseEntity<Map <String, List<CurrentUserPastGames>> > getCurrentUserPastGames(){
        try {
            String username = PlayerUtils.getCurrentUsername();
            Player currentPlayer = playerService.findPlayerByUsername(username);
            UUID playerId = currentPlayer.getPlayerId();
            if (playerId == null) {
                throw new UnauthenticatedUserException("User id is null.");
            }
            
            return  new ResponseEntity<>(playerService.returnCurrentPlayersPastGames(playerId),HttpStatus.OK);
        } catch(Exception e){
           
            if (e instanceof PlayerDataAccessException) {
                throw e; 
            }
            throw new PlayerDataAccessException(e.getMessage());
        }
    }


       /**
     * This method is used to return the profile details of the current user.
     * 
     * @return a ResponseEntity containing the UserProfileDao with username and email
     * @throws PlayerNotFoundException if the current user is not found in the database
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDao> getCurrentUserProfile(){
        String username = PlayerUtils.getCurrentUsername();
        Player player = playerService.findPlayerByUsername(username);
        if (player == null) {
            throw new PlayerNotFoundException("Player not found for username: " + username);
        }
        String email = player.getEmail();
        UserProfileDao currentUser = new UserProfileDao(username,email);
        return ResponseEntity.ok(currentUser);
    }

}
