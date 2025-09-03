package com.example.mastermind.dataAccessObjects;

import com.example.mastermind.models.entities.MultiplayerGame;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository for MultiplayerGame entity persistence operations.
 * 
 * Provides basic CRUD operations inherited from JpaRepository.
 */
public interface MultiplayerGameRepository extends JpaRepository<MultiplayerGame, UUID> {
}
