package com.example.mastermind.repositoryLayer;

import com.example.mastermind.models.entities.MultiplayerGame;
import com.example.mastermind.models.entities.SinglePlayerGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * Repository for MultiplayerGame entity persistence operations.
 * <p>
 * Provides basic CRUD operations inherited from JpaRepository.
 */
public interface MultiplayerGameRepository extends JpaRepository<MultiplayerGame, UUID> {
    @Query("SELECT g FROM MultiplayerGame g WHERE (g.player1.playerId = :playerId OR g.player2.playerId = :playerId) AND g.finished = true")
    List<MultiplayerGame> findFinishedGames(@Param("playerId") UUID playerId);



    @Query("SELECT g FROM MultiplayerGame g WHERE (g.player1.playerId = :playerId OR g.player2.playerId = :playerId) AND g.finished = false")
    List<SinglePlayerGame> findUnfinishedGames(@Param("playerId") UUID playerId);
}
