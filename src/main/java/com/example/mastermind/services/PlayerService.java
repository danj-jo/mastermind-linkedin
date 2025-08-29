package com.example.mastermind.services;

import com.example.mastermind.dataAccessObjects.PlayerRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
@AllArgsConstructor
public class PlayerService {
    private final PlayerRepository playerRepository;

}
