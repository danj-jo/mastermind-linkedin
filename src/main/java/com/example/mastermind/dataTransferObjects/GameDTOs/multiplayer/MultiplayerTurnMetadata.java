package com.example.mastermind.dataTransferObjects.GameDTOs.multiplayer;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MultiplayerTurnMetadata {
    private String feedback;
    private boolean finished;
    private List<GuessDTO> guesses;


    private UUID currentPlayerId;
    private boolean notYourTurn;
}
