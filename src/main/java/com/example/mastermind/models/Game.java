package com.example.mastermind.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
import java.util.UUID;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @Entity(name = "games")
    public class Game {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        @Column
        private UUID gameId;
        @ManyToOne
        @JoinColumn(name="player_id", referencedColumnName = "playerId", nullable = false)
        private Player player;
        @Column(nullable = false)
        private String winningNumber;
        @Column(nullable = false)
        @Enumerated(EnumType.STRING)
        private Difficulty difficulty;
        @ElementCollection(fetch = FetchType.EAGER)
        private List<String> guesses;
        @Column
        @Enumerated(EnumType.STRING)
        private Result result = Result.PENDING;
        @Column
        private boolean isFinished = false;
    }






