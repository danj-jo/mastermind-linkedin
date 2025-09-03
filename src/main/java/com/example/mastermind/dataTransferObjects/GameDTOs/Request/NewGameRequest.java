package com.example.mastermind.dataTransferObjects.GameDTOs.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor


public class NewGameRequest {
    String difficulty;
}
