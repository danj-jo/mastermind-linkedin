package com.example.mastermind.repositoryLayer;


import com.example.mastermind.models.entities.SinglePlayerGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for SinglePlayerGame entity persistence operations.
 * 
 * Provides methods for managing single-player game lifecycle including
 * game creation, completion status tracking, and player game history.
 * Extends JpaRepository for basic CRUD operations while adding custom
 * queries for game state management and player-specific game retrieval.
 */
public interface SingleplayerGameRepository extends JpaRepository<SinglePlayerGame, UUID> {
    boolean existsByGameIdAndFinishedTrue(UUID gameId);

         boolean existsByGameId(UUID gameId);
         Optional<SinglePlayerGame> findGameByGameId(UUID gameId);

    @Query("SELECT g FROM SinglePlayerGame g WHERE g.player.playerId = :playerId AND g.finished = true")
    List<SinglePlayerGame> findFinishedGames(@Param("playerId") UUID playerId);

    @Query("SELECT g FROM SinglePlayerGame g WHERE g.player.playerId = :playerId AND g.finished = false")
    List<SinglePlayerGame> findUnfinishedGames(@Param("playerId") UUID playerId);


}
