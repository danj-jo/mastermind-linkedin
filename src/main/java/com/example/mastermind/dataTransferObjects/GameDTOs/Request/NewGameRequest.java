package com.example.mastermind.dataTransferObjects.GameDTOs.Request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class NewGameRequest {
    String difficulty;
}
