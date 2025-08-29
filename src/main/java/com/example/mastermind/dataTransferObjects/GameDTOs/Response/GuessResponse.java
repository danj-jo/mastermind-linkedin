package com.example.mastermind.dataTransferObjects.GameDTOs.Response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
public class GuessResponse {
    List<String> guesses;
    String feedback;

    public static Map<String,String> toMap(List<String> guesses, String feedback){
        return new HashMap<>(Map.of(
                "Prior Guesses",guesses.toString(),
                "Feedback", feedback
        ));
    }
}
