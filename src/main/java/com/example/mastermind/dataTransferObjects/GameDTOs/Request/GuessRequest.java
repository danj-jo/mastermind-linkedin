package com.example.mastermind.dataTransferObjects.GameDTOs.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

/**
 * DTO for submitting a guess.
 * Contains the guess sent by the user
 */
public class GuessRequest {
    /** The string value of the guess  */
    private String guess;
}
