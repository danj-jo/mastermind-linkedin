package com.example.mastermind.dataAccessObjects;

import com.example.mastermind.models.entities.MultiplayerGame;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MultiplayerGameRepository extends JpaRepository<MultiplayerGame, UUID> {
}
