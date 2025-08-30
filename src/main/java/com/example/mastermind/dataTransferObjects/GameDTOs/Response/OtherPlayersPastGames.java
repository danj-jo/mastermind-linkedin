package com.example.mastermind.dataTransferObjects.GameDTOs.Response;

import com.example.mastermind.models.Difficulty;
import com.example.mastermind.models.Result;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
public class OtherPlayersPastGames {
    private Difficulty difficulty;
    private String winningNumber;
    private Result result;

    public static Map<String,String> toMap(Difficulty difficulty,String winningNumber, Result result){
        return new HashMap<>(Map.of(
                "Difficulty",difficulty.toString(),
                "Winning Number", winningNumber,
                "Result", result.toString()
        ));
    }
}
