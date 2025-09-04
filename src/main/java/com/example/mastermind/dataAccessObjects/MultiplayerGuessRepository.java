package com.example.mastermind.dataAccessObjects;

import com.example.mastermind.models.entities.MultiplayerGuess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MultiplayerGuessRepository extends JpaRepository<MultiplayerGuess, UUID> {
}
