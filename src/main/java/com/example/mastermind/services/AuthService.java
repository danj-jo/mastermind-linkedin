package com.example.mastermind.services;

import com.example.mastermind.dataAccessObjects.PlayerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Service
@AllArgsConstructor

public class AuthService {
    private final PlayerRepository playerRepository;

}
