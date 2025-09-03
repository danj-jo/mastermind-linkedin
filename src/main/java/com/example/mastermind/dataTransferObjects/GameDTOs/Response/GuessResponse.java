package com.example.mastermind.dataTransferObjects.GameDTOs.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GuessResponse {
    private String feedback;
    private List<String> guesses;
    private boolean finished;
}
