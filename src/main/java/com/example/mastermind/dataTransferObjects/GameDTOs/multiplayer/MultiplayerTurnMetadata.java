package com.example.mastermind.dataTransferObjects.GameDTOs.multiplayer;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MultiplayerTurnMetadata {
    private String player;
    private String feedback;
    private boolean finished;
    private List<String> guesses;
}