package com.example.mastermind.dataAccessObjects;


import com.example.mastermind.dataTransferObjects.GameDTOs.Response.CurrentUserPastGames;
import com.example.mastermind.models.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameRepository extends JpaRepository<Game, UUID> {
    boolean existsByGameIdAndIsFinishedTrue(UUID gameId);

         boolean existsByGameId(UUID gameId);
         Optional<Game> findGameByGameId(UUID gameId);

        @Query("SELECT g FROM games g WHERE g.player.playerId = :playerId AND g.isFinished = true")
        List<Game> findFinishedGames(@Param("playerId") UUID playerId);

        @Query("SELECT g FROM games g WHERE g.player.playerId = :playerId AND g.isFinished = false")
        List<Game> findUnfinishedGames(@Param("playerId") UUID playerId);


}
