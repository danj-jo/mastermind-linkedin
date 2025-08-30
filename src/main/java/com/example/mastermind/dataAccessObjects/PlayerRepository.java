package com.example.mastermind.dataAccessObjects;

import com.example.mastermind.models.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlayerRepository extends JpaRepository<Player, UUID> {
    Optional <Player> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
