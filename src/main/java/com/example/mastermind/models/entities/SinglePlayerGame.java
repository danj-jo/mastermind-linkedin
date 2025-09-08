package com.example.mastermind.models.entities;

import com.example.mastermind.models.Difficulty;
import com.example.mastermind.models.Game;
import com.example.mastermind.models.GameMode;
import com.example.mastermind.models.Result;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.*;


    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Entity
    public class SinglePlayerGame extends Game {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column
    private UUID gameId;
    @ManyToOne
    @JoinColumn(name = "player_id", referencedColumnName = "playerId", nullable = false)
    private Player player;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> guesses;



    public String generateHint(String guess){
        return String.format("You have %d numbers correct, in %d locations. %d guesses remaining.", totalCorrectNumbers(guess),numberOfCorrectLocations(guess), 10 - this.guesses.size());
    }
    @Override
    public boolean guessAlreadyExists(String guess){
        return this.guesses.contains(guess);
    }
    @Override
    public boolean userLostGame(){
        return guesses.size() >= 10;
    }

}






