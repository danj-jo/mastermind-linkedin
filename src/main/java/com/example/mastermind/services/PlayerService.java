package com.example.mastermind.services;

import com.example.mastermind.models.PastGame;
import com.example.mastermind.repositoryLayer.SingleplayerGameRepository;
import com.example.mastermind.repositoryLayer.PlayerRepository;
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
    private final PlayerRepository playerRepository;

    /**
     * This method is used to return a player that we search for via username parameter.
     * @param username username that we input to find player
     * @return corresponding player
     */
    public Player findPlayerByUsername(String username){
            return playerRepository.findByUsername(username).orElseThrow();

    }



}
