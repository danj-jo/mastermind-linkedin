package com.example.mastermind.controllers;

import com.example.mastermind.dataTransferObjects.GameDTOs.Response.CurrentUserPastGames;
import com.example.mastermind.dataTransferObjects.GameDTOs.Response.UserProfileDao;
import com.example.mastermind.models.entities.Player;
import com.example.mastermind.services.GameService;
import com.example.mastermind.services.PlayerService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@RestController
public class PlayerController {
    private final PlayerService playerService;
    private final GameService gameService;
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/me")
    public ResponseEntity<?> returnCurrentUserPastGames(){
        try {
            Authentication auth = SecurityContextHolder.getContext()
                                                       .getAuthentication();
            String username = auth.getName();
            Player player = playerService.findByUsername(username);
            UUID playerId = player.getPlayerId();
            if (playerId == null) {
                return new ResponseEntity<>(new HashMap<>(Map.of("Error","User id is null.")), HttpStatus.UNAUTHORIZED);
            }
            Map<String, List<CurrentUserPastGames>> pastGames = playerService.returnCurrentPlayersPastGames(playerId);
            System.out.println(playerId);
            return  new ResponseEntity<>(pastGames,HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>(new HashMap<>(Map.of("Error", e.getMessage())),HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/about")
    public ResponseEntity<?> returnUsername(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Player player = playerService.findByUsername(auth.getName());
        String email = player.getEmail();
        UserProfileDao currentUser = new UserProfileDao(auth.getName(),email);
        return ResponseEntity.ok(currentUser.toMap());
    }

    @PostMapping("/home")
    // return player with most wins
    // team with most wins
    //
    public ResponseEntity<?> returnLeaderboard(){

        return null;

    }

}
