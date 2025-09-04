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
    public String submitGuess(Player player, String guess){
        boolean isPlayer1sTurn = true;
        if(guessContainsInvalidCharacters(guess)){
            return "Guesses are numbers only";
        }
        if(inappropriateLength(guess)){
            return String.format("Guess is not the appropriate length. Please try again. Guess must %d numbers", winningNumber.length());
        }
        if(guessAlreadyExists(guess)){
            return "We don't allow Duplicate guesses here.";
        }

        if(gameIsFinished(guess)){
            return "Game is finished.";
        }

        if(userLostGame()) {
            setResult(Result.LOSS);
            isFinished = true;
            return String.format("Game Over! The correct number was: %s", winningNumber);
        }

        // create a new multiplayer guess object
        MultiplayerGuess newGuess = new MultiplayerGuess();
        // set the game to this one (to have mapping to game id)
        newGuess.setGame(this);
        // set the current player to the current guess (to know current player guessing)
        newGuess.setPlayer(player);
        // the guess itself,
        newGuess.setGuess(guess);
        // add the guesses to the guess list.
        guesses.add(newGuess);
        if(userWonGame(guess)){
            isFinished = true;
            setResult(Result.WIN);
            return "You Win!";
        }


        return guesses.size() < 10 ? generateHint(player,guess) : String.format("Game Over! The correct number was: %s", winningNumber);
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
            // Create sets directly from the strings
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
}
