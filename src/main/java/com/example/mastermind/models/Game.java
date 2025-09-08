package com.example.mastermind.models;

import com.example.mastermind.models.entities.Player;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@MappedSuperclass
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public abstract class Game {

    @Column(nullable = false)
    protected String winningNumber;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    protected Difficulty difficulty;
    @Enumerated(EnumType.STRING)
    @Column
    protected GameMode mode;
    @Enumerated(EnumType.STRING)
    @Column
    @Builder.Default
    protected Result result = Result.PENDING;
    @Column
    @Builder.Default
    protected boolean finished = false;


    /**
     * Here, I add each item in the winning number to a list of characters. I then transform the guess string into an array of characters, and track if the winning number has any characters that also exist in the guess. If no, I cast the index to null, so that if I encounter it again, it would not be counted twice.
     * @param guess - the user's guess represented as a string
     * @return - the number of correct guesses
     */
    public int numberOfCorrectLocations(String guess){
        int locationCounter = 0;
        // if the item at these indexes are the same, increase location counter.
        for(int j = 0; j < winningNumber.length(); j++){
            if(winningNumber.charAt(j) == guess.charAt(j)){
                locationCounter++;
            }
        }
        return locationCounter;
    }
    public int totalCorrectNumbers(String guess){
        int correctNumbers = 0;
        List<Character> winningNumberList = new ArrayList<>();
        for (char c : winningNumber.toCharArray()) {
            winningNumberList.add(c);
        }
        for (char guessDigit : guess.toCharArray()) {
            int index = winningNumberList.indexOf(guessDigit); // find first occurrence
            if (index != -1) {                          // if found
                correctNumbers++;                         // count it
                winningNumberList.set(index, null);            // mark as used
            }
        }
        return correctNumbers;
    }
    /**
     * The following are helper methods that help with validation. They are identical to singleplayer helper methods, but I do not place them in an external file because there is no shared interface between single and multiplayer games, and calling these methods on the current game object reduces cognitive load.
     */
    public boolean inappropriateLength(String guess){
        return guess.length() != winningNumber.length();
    }
    public boolean userWonGame(String guess){

        return guess.equals(this.winningNumber);
    }
    public boolean guessContainsInvalidCharacters(String guess){
        System.out.println(guess);
        return !guess.matches("\\d+");
    }
    public boolean guessIsOverLimit(String guess){
        return !guess.matches("[0-7]+");
    }
    public abstract boolean userLostGame();
    public abstract boolean guessAlreadyExists(String guess);
}

