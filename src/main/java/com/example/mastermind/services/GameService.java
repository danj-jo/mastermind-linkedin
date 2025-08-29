package com.example.mastermind.services;

import com.example.mastermind.dataAccessObjects.GameRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
@AllArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
}
