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
    private List <MultiplayerGuess> guesses = new ArrayList<>();
    @Enumerated(EnumType.STRING)
    @Column
    private Result result = Result.PENDING;
    @Column
    private boolean isFinished = false;

    // takes in the current player (who's turn it is, and their guess)
    public String submitGuess(Player player, String guess) {

        MultiplayerGuess newGuess = new MultiplayerGuess();
        // link guess to this game
        newGuess.setGame(this);
        // link guess to player
        newGuess.setPlayer(player);
        // the guess itself
        newGuess.setGuess(guess);
        if (this.difficulty == Difficulty.EASY && guessIsOverLimit(guess)) {
            return "Only numbers 0-7 are allowed. Please try again.";
        }

        if (guessContainsInvalidCharacters(guess)) {
            return "Guesses are numbers only";
        }

        if (inappropriateLength(guess)) {
            return String.format(
                    "Guess is not the appropriate length. Please try again. Guess must be %d numbers",
                    winningNumber.length()
            );
        }

        if (guessAlreadyExists(guess)) {
            return "We don't allow duplicate guesses here.";
        }

        if (isFinished || gameIsFinished(guess)) {  // guard for already finished
            return "Game is finished.";
        }
        guesses.add(newGuess);
        if (userWonGame(guess)) {
            isFinished = true;
            setResult(Result.WIN);
            return "You Win!";
        }
        if (userLostGame()) {
            isFinished = true;
            setResult(Result.LOSS);
            return String.format("Game Over! The correct number was: %s", winningNumber);
        }
        return generateHint(player, guess);
    }

    private String generateHint(Player player, String guess) {
        return String.format("%s has %d  numbers correct, in %d locations. %d guesses remaining.", player.getUsername(), totalCorrectNumbers(guess),numberOfCorrectLocations(guess), 10 - guesses.size());
    }
    private int numberOfCorrectLocations(String guess){
        int locationCounter = 0;
        // if the item at these indexes are the same, increase location counter.
        for(int j = 0; j < winningNumber.length(); j++){
            if(winningNumber.charAt(j) == guess.charAt(j)){
                locationCounter++;
            }
        }
        return locationCounter;

    }
    private int totalCorrectNumbers(String guess){
            // Create sets from strings, since they can not hold duplicates. I then iterate through each Set and increment a number with each match.
            Set<Character> winningNumberSet = new HashSet<>();
            for (char c : winningNumber.toCharArray()) {
                winningNumberSet.add(c);
            }

            Set<Character> guessSet = new HashSet<>();
            for (char c : guess.toCharArray()) {
                guessSet.add(c);
            }

            int correctGuesses = 0;
            for (char guessCharacter : guessSet) {
                if (winningNumberSet.contains(guessCharacter)) {
                    correctGuesses++;
                }
            }
            return correctGuesses;
        }

    private boolean inappropriateLength(String guess){
        return guess.length() != winningNumber.length();
    }
    private boolean guessAlreadyExists(String guess){
        return guesses.stream().anyMatch(g -> g.getGuess().equals(guess));
    }
    private boolean gameIsFinished(String guess){
        return isFinished;
    }
    private boolean userWonGame(String guess){

        return guess.equals(this.winningNumber);
    }
    private boolean userLostGame(){

        return guesses.size() == 10;
    }
    private boolean guessContainsInvalidCharacters(String guess){
        System.out.println(guess);
        return !guess.matches("\\d+");
    }
    private boolean guessIsOverLimit(String guess){
        return !guess.matches("[0-7]+");
    }
}
