package com.example.mastermind.dataTransferObjects.GameDTOs.Request;

import com.example.mastermind.models.entities.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MultiplayerJoinGameRequest {
    Player player;
    String difficulty;

}
