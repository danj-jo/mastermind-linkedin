package com.example.mastermind.dataAccessObjects;


import com.example.mastermind.models.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GameRepository extends JpaRepository<Game, UUID> {
}
