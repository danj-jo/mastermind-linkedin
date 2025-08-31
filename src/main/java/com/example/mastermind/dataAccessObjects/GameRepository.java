package com.example.mastermind.dataAccessObjects;


import com.example.mastermind.models.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface GameRepository extends JpaRepository<Game, UUID> {

    boolean existsByGameIdAndIsFinishedTrue(UUID gameId);

}
