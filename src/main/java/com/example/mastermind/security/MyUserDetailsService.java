package com.example.mastermind.security;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;

import com.example.mastermind.dataAccessObjects.PlayerRepository;
import com.example.mastermind.models.Player;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
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

