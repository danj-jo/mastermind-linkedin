package com.example.mastermind.models.entities;

import com.example.mastermind.models.Difficulty;
import com.example.mastermind.models.GameMode;
import com.example.mastermind.models.Result;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @Entity(name = "games")
    public class SinglePlayerGame {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column
    private UUID gameId;
    @ManyToOne
    @JoinColumn(name = "player_id", referencedColumnName = "playerId", nullable = false)
    private Player player;
    @Column(nullable = false)
    private String winningNumber;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;
    @Enumerated(EnumType.STRING)
    private GameMode mode = GameMode.SINGLE_PLAYER;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> guesses;
    @Column
    @Enumerated(EnumType.STRING)
    private Result result = Result.PENDING;
    @Column
    private boolean finished = false;

    public int numberOfCorrectLocations(String guess){
        int locationCounter = 0;
        // if the item at these indexes are the same, increase location counter.
        for(int j = 0; j < winningNumber.length(); j++){
            if(guess.charAt(j) == winningNumber.charAt(j)){
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
    public boolean inappropriateLength(String guess){
        return guess.length() != winningNumber.length();
    }
    public boolean guessAlreadyExists(String guess){
        return guesses.contains(guess);
    }
    public boolean gameIsFinished(SinglePlayerGame game){
        return game.isFinished();
    }
    public boolean userWonGame(String guess){

        return guess.equals(this.winningNumber);
    }
    public boolean userLostGame(){
        return guesses.size() >= 10;
    }
    public boolean guessContainsInvalidCharacters(String guess){
        System.out.println(guess);
        return !guess.matches("\\d+");
    }
    public boolean guessIsOverLimit(String guess){
        return !guess.matches("[0-7]+");
    }public String generateHint(String guess) {
        return String.format("You have %d numbers correct, in %d locations. %d guesses remaining.", totalCorrectNumbers(guess),numberOfCorrectLocations(guess), 10 - guesses.size());
    }

}






