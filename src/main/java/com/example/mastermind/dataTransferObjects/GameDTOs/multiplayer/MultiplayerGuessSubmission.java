package com.example.mastermind.dataTransferObjects.GameDTOs.multiplayer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MultiplayerGuessSubmission {
    String guess;
    UUID playerId;
}
