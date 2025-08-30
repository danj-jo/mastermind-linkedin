package com.example.mastermind.dataTransferObjects.GameDTOs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameResponseDtoFactory {
        public static Map<String,String> GuessResponseDTO(List<String> LocalGuessList, String feedback){
            return new HashMap<>(Map.of(
                    "guesses",LocalGuessList.toString(),
                    "feedback",feedback
            ));
        }
        //"Guesses: %s, %s", localGuesses, feedback
        public static Map<String,String> NewGameResponseDTO(String difficulty){
            return new HashMap<>(Map.of("Difficulty",difficulty));
        }
}
