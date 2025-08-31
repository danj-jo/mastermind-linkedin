package com.example.mastermind.dataTransferObjects.GameDTOs.Response;

import com.example.mastermind.models.Difficulty;
import com.example.mastermind.models.Result;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CurrentUserPastGames {
    private String gameId;
    private String difficulty;
    private String result;
    private String winningNumber;
    private String previousGuesses;
    private String isFinished;



//    private Map<String,String> toMap(){
//        return new HashMap<>(Map.of("GameId", gameId,
//                                    "Difficulty",difficulty,
//                                    "Result", result,
//                                    "Previous Guesses", previousGuesses,
//                                    "Winning Number", winningNumber,
//                                    "Status",isFinished ? "Complete": "Incomplete"
//        ));
//    }

}

