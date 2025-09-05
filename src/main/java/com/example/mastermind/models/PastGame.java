package com.example.mastermind.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PastGame {
    private String gameId;
    private String difficulty;
    private String result;
    private String winningNumber;
    private String previousGuesses;
}

