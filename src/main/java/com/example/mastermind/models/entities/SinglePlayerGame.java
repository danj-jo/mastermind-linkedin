package com.example.mastermind.models.entities;

import com.example.mastermind.models.Difficulty;
import com.example.mastermind.models.GameMode;
import com.example.mastermind.models.Result;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
        @JoinColumn(name="player_id", referencedColumnName = "playerId", nullable = false)
        private Player player;
        @Column(nullable = false)
        private String winningNumber;
        @Column(nullable = false)
        @Enumerated(EnumType.STRING)
        private Difficulty difficulty;
        @Enumerated(EnumType.STRING)
        private GameMode mode = GameMode.SINGLE_PLAYER;
        @ElementCollection(fetch = FetchType.EAGER)
        private List<String> guesses;
        @Column
        @Enumerated(EnumType.STRING)
        private Result result = Result.PENDING;
        @Column
        private boolean isFinished = false;

        public String submitGuess(String guess){
            if(this.difficulty == Difficulty.EASY && guessIsOverLimit(guess)){
                return "Only numbers 0-7 are allowed. Please try again.";
            }
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

            guesses.add(guess);
            if(userWonGame(guess)){
                isFinished = true;
                setResult(Result.WIN);
                return "You Win!";
            }
            if(userLostGame()) {
                setResult(Result.LOSS);
                isFinished = true;
                return String.format("Game Over! The correct number was: %s", winningNumber);
            }
            return generateHint(guess);
        }
        private String generateHint(String guess) {
            return String.format("You have %d amount of numbers correct, in %d locations. %d guesses remaining.", totalCorrectNumbers(guess),numberOfCorrectLocations(guess), 10 - guesses.size());
        }
        private int numberOfCorrectLocations(String guess){
            int locationCounter = 0;
            // if the item at these indexes are the same, increase location counter.
            for(int j = 0; j < winningNumber.length(); j++){
                if(guess.charAt(j) == winningNumber.charAt(j)){
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
            return guesses.contains(guess);
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






