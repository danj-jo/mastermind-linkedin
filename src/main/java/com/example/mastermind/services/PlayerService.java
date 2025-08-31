package com.example.mastermind.services;

import com.example.mastermind.dataAccessObjects.PlayerRepository;
import com.example.mastermind.dataTransferObjects.GameDTOs.Response.CurrentUserPastGames;
import com.example.mastermind.dataTransferObjects.GameDTOs.Response.OtherPlayersPastGames;
import com.example.mastermind.dataTransferObjects.GameDTOs.Response.UserProfileDao;
import com.example.mastermind.models.Player;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.*;

@Service
@Component
@AllArgsConstructor
public class PlayerService {
    private final GameService gameService;
    private final PlayerRepository playerRepository;

    public Player findByUsername(String username){
        if(!playerRepository.existsByUsername(username)){
            throw new UsernameNotFoundException("User does not exist.");
        }

            return playerRepository.findByUsername(username).orElseThrow();

    }

    public Map<String, List<CurrentUserPastGames>> returnCurrentPlayersPastGames(UUID playerId){
        return gameService.returnCurrentUsersPastGames(playerId);
    }

    public Map<String, List<OtherPlayersPastGames>> returnOtherPlayersPastGames(UUID playerId){
        return null;
    }

}
