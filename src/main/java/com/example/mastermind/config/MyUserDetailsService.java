package com.example.mastermind.config;

import com.example.mastermind.repositoryLayer.PlayerRepository;
import com.example.mastermind.models.entities.Player;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
/**
 * Custom UserDetailsService that converts Player entities to UserDetails for Spring Security.
 * 
 * This service bridges the Player entity with Spring Security's authentication system
 * by converting Player data into the format Spring Security expects. Implemented by the Player class and used in the SecurityConfig class.
  */
public class MyUserDetailsService implements UserDetailsService {

    private final PlayerRepository playerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Player player = playerRepository.findByUsername(username).orElseThrow();
        return org.springframework.security.core.userdetails.User.builder()
                                                                 .username(player.getUsername())
                                                                 .password(player.getPassword())
                                                                 .roles("USER")
                                                                 .build();
    }


    public Player getPlayer(String username) {
        return playerRepository.findByUsername(username).orElseThrow();
    }

    }

