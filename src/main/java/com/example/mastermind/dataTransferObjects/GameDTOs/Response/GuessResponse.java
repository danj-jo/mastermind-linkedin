package com.example.mastermind.dataTransferObjects.GameDTOs.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GuessResponse {
    private List<String> guesses;
    private String feedback;

    public static Map<String,String> toMap(List<String> guesses, String feedback){
        return new HashMap<>(Map.of(
                "Prior Guesses",guesses.toString(),
                "Feedback", feedback
        ));
    }
}
