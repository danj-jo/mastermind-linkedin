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
    @ManyToMany
    @JoinTable(
            name = "multiplayer_game_players",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    private List<Player> players = new ArrayList<>();
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
        // set player to the current player (to know current player guessing)
        newGuess.setPlayer(player);
        // set the guess to the current player
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
        // todo just a reminder, don't forget that we took the guess array out of this string to create a guess bank type structure for the frontend. When we send the hint to clients, make sure the response includes either a list of guesses from the object, or a local list that we update upon each guess. A map may suffice.
        return String.format("%s has %d  numbers correct, in %d locations. %d guesses remaining.", player.getUsername(), totalCorrectNumbers(guess),numberOfCorrectLocations(guess), 10 - guesses.size());
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

        for(int i = 0; i < winningNumber.length(); i++){
            for(int j = 0; j< guess.length(); j++){
                if(winningNumber.charAt(i) == guess.charAt(j)){
                    // if the winning number contains the character in guess, add the guess at that character to the Set,
                    correctValues.add(String.valueOf(guess.charAt(i)));
                }

            }
        }
    
        return correctValues.size();
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
