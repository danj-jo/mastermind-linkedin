package com.example.mastermind.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MultiplayerGuess {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID guessId;
    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private MultiplayerGame game;
    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;
    @Column
    private String guess;

    @Override
    public String toString() {
        return "MultiplayerGuess{" +
                "player=" + (player != null ? player.getUsername() : "null") +
                ", guess='" + guess + '\'' +
                '}';
    }


}
