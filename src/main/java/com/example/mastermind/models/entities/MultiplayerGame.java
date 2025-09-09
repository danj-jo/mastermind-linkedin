package com.example.mastermind.models.entities;

import com.example.mastermind.models.Difficulty;
import com.example.mastermind.models.Game;
import com.example.mastermind.models.Result;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.*;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MultiplayerGame extends Game {

    @Id
    @Builder.Default
    private UUID gameId = UUID.randomUUID();
    @ManyToOne
    @JoinColumn(name = "player1_id")
    private Player player1;
    @ManyToOne
    @JoinColumn(name = "player2_id")
    private Player player2;
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set <MultiplayerGuess> guesses = new HashSet<>();

    @Transient
    UUID currentPlayerId;

    public String generateHint(Player player, String guess){
        return String.format("%s has %d  numbers correct, in %d locations. %d guesses remaining.", player.getUsername(), totalCorrectNumbers(guess),numberOfCorrectLocations(guess), 10 - guesses.size());
    }
    @Override
    public boolean guessAlreadyExists(String guess){
        return guesses.stream().anyMatch(g -> g.getGuess().equals(guess));
    }
    @Override
    public boolean userLostGame(){
        return guesses.size() == 10;
    }


}
