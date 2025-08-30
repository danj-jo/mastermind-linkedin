package com.example.mastermind.dataTransferObjects.GameDTOs.Response;

import com.example.mastermind.models.Difficulty;
import com.example.mastermind.models.Result;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class CurrentUserPastGames {
    private static UUID gameId;
    private Difficulty difficulty;
    private Result result;
    private String winningNumber;
    private List<String> previousGuesses;
    boolean isFinished;

    private Map<String,String> toMap(){
        return new HashMap<>(Map.of("GameId", gameId.toString(),
                                    "Difficulty",difficulty.toString(),
                                    "Result", result.toString(),
                                    "Previous Guesses", previousGuesses.toString(),
                                    "Winning Number", winningNumber,
                                    "Status",isFinished ? "Complete": "Incomplete"
        ));
    }

}

