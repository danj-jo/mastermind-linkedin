package com.example.mastermind.services;

import com.example.mastermind.dataAccessObjects.SingleplayerGameRepository;
import com.example.mastermind.dataAccessObjects.PlayerRepository;
import com.example.mastermind.dataTransferObjects.GameDTOs.Response.CurrentUserPastGames;
import com.example.mastermind.models.entities.Player;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Component
@AllArgsConstructor
public class PlayerService {
    private final SingleplayerGameService singleplayerGameService;
    private final PlayerRepository playerRepository;
    private final SingleplayerGameRepository singleplayerGameRepository;

    /**
     * This method is used to return a player that we search for via username parameter.
     * @param username username that we input to find player
     * @return corresponding player
     */
    public Player findPlayerByUsername(String username){
        if(!playerRepository.existsByUsername(username)){
            throw new UsernameNotFoundException("User does not exist.");
        }

            return playerRepository.findByUsername(username).orElseThrow();

    }

    public Map<String, List<CurrentUserPastGames>> returnCurrentPlayersPastGames(UUID playerId){
        return singleplayerGameService.returnCurrentUsersPastGames(playerId);
    }



}
