package com.example.mastermind.dataAccessObjects;

import com.example.mastermind.models.entities.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Player entity persistence operations.
 * 
 * Provides methods for finding players by username/email and checking existence.
 * Extends JpaRepository to inherit basic CRUD operations while adding custom
 * query methods specific to player authentication and lookup needs.
 */
@Repository
public interface PlayerRepository extends JpaRepository<Player, UUID> {
    Optional<Player> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
