package com.example.mastermind.models.entities;

import com.example.mastermind.models.Difficulty;
import com.example.mastermind.models.Result;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MultiplayerGame  {
    @Id
    private UUID gameId = UUID.randomUUID();
    @ManyToOne
    @JoinColumn(name = "player1_id")
    private Player player1;
    @ManyToOne
    @JoinColumn(name = "player2_id")
    private Player player2;
    @Column(nullable = false)
    private String winningNumber;
    @Enumerated(EnumType.STRING)
    @Column
    private Difficulty difficulty;
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set <MultiplayerGuess> guesses = new HashSet<>();
    @Enumerated(EnumType.STRING)
    @Column
    private Result result = Result.PENDING;
    @Column
    private boolean isFinished = false;

    // takes in the current player (whose turn it is, and their guess)

    public String generateHint(Player player, String guess) {
        return String.format("%s has %d  numbers correct, in %d locations. %d guesses remaining.", player.getUsername(), totalCorrectNumbers(guess),numberOfCorrectLocations(guess), 10 - guesses.size());
    }
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

    public boolean inappropriateLength(String guess){
        return guess.length() != winningNumber.length();
    }
    public boolean guessAlreadyExists(String guess){
        return guesses.stream().anyMatch(g -> g.getGuess().equals(guess));
    }
    public boolean gameIsFinished(String guess){
        return isFinished;
    }
    public boolean userWonGame(String guess){

        return guess.equals(this.winningNumber);
    }
    public boolean userLostGame(){

        return guesses.size() == 10;
    }
    public boolean guessContainsInvalidCharacters(String guess){
        System.out.println(guess);
        return !guess.matches("\\d+");
    }
    public boolean guessIsOverLimit(String guess){
        return !guess.matches("[0-7]+");
    }
}
