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
            guesses.add(guess);
            if(userWonGame(guess)){
                isFinished = true;
                setResult(Result.WIN);
                return "You Win!";
            }
            return guesses.size() < 10 ? generateHint(guess) : String.format("Game Over! The correct number was: %s", winningNumber);
        }

        private String generateHint(String guess) {
            return String.format("You have %d amount of numbers correct, in %d locations. %d guesses remaining. %s", totalCorrectNumbers(guess),numberOfCorrectLocations(guess), 10 - guesses.size(), guesses);
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
            Set<String> correctValues = new HashSet<>();

        /*
         iterate through guess string and winningNumber string (in tandem) and identify the number of matches.
           Full thought process:

           1. Create a correctNumber integer variable
           2. Increase correctNumber variable by one with each number that matched.

           Issue with this logic:
           if I have duplicate numbers in a guess, this will increase the correctNumber variable by every duplicate.

           The solution:
           Create a set to hold each number that is in both the winning number and the guess. The set will automatically filter out any duplicates, returning only unique values between both. I then set the return value to the size of the set of unique values.

         */
            // loop through winning number and guess
            for(int i = 0; i < winningNumber.length(); i++){
                for(int j = 0; j< guess.length(); j++){
                    if(winningNumber.charAt(i) == guess.charAt(j)){
                        // if the winning number contains the character in guess, add the guess at that character to the Set,
                        correctValues.add(String.valueOf(guess.charAt(i)));
                    }

                }
            }
            // return the size of the set that contains correct guesses
            return correctValues.size();
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
    }






