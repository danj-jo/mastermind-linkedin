package com.example.mastermind.dataAccessObjects;


import com.example.mastermind.models.entities.SinglePlayerGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SingleplayerGameRepository extends JpaRepository<SinglePlayerGame, UUID> {
    boolean existsByGameIdAndIsFinishedTrue(UUID gameId);

         boolean existsByGameId(UUID gameId);
         Optional<SinglePlayerGame> findGameByGameId(UUID gameId);

        @Query("SELECT g FROM games g WHERE g.player.playerId = :playerId AND g.isFinished = true")
        List<SinglePlayerGame> findFinishedGames(@Param("playerId") UUID playerId);

        @Query("SELECT g FROM games g WHERE g.player.playerId = :playerId AND g.isFinished = false")
        List<SinglePlayerGame> findUnfinishedGames(@Param("playerId") UUID playerId);


}
