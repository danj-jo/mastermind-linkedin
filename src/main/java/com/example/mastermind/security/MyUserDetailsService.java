package com.example.mastermind.security;

import com.example.mastermind.dataAccessObjects.PlayerRepository;
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
            return playerRepository.findByUsername(username);
        }
    }

