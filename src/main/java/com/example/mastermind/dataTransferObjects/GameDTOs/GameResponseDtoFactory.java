package com.example.mastermind.dataTransferObjects.GameDTOs;

import com.example.mastermind.models.Result;
import java.util.HashMap;
import java.util.Map;


public class GameResponseDtoFactory {
        public static Map<String,String> GuessResponseDTO(String feedback, Result result){
            return new HashMap<>(Map.of(
                    "feedback",feedback,
                    "result", String.valueOf(result)
            ));
        }
        //"Guesses: %s, %s", localGuesses, feedback
        public static Map<String,String> NewGameResponseDTO(String difficulty){
            return new HashMap<>(Map.of("Difficulty",difficulty));
        }
}
