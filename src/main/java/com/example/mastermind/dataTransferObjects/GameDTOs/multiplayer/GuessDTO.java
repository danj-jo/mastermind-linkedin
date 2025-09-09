package com.example.mastermind.dataTransferObjects.GameDTOs.multiplayer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GuessDTO {
    private String player;
    private String guess;
}
