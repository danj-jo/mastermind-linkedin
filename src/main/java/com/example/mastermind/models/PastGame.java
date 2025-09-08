package com.example.mastermind.models;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PastGame {
    private String gameId;
    private String difficulty;
    private String result;
    private String winningNumber;
    private String previousGuesses;
}

