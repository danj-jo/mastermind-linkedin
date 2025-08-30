package com.example.mastermind.services;

import com.example.mastermind.dataAccessObjects.GameRepository;
import com.example.mastermind.dataAccessObjects.PlayerRepository;
import com.example.mastermind.models.Difficulty;
import com.example.mastermind.models.Game;
import com.example.mastermind.models.Player;
import lombok.AllArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.net.URI;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import org.slf4j.Logger;

@Service
@Component
@AllArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private static final Logger logger = LoggerFactory.getLogger(GameService.class);


    public Game createNewGame(String playerDifficulty, UUID playerId){


        Player player = playerRepository.findById(playerId).orElseThrow(() -> new UsernameNotFoundException("Player Not found."));
        Game game = new Game();

        game.setDifficulty(selectUserDifficulty(playerDifficulty));
        game.setPlayer(player);
        game.setWinningNumber(generateWinningNumber(Difficulty.valueOf(playerDifficulty)));
        game.setGuesses(new ArrayList<>());

        gameRepository.saveAndFlush(game);
        return game;

    }

    public String makeGuess(UUID gameId, String guess){
        Game currentGame = gameRepository.findById(gameId)
                                         .orElseThrow(() -> new RuntimeException("Game not found"));
        if (currentGame.getGuesses() == null) {
            currentGame.setGuesses(new ArrayList<>());
        }

        String feedback = currentGame.submitGuess(guess);
        gameRepository.saveAndFlush(currentGame);
        return feedback;
    }

    private Difficulty selectUserDifficulty(String choice){

        return choice.equalsIgnoreCase("easy") ? Difficulty.EASY : choice.equalsIgnoreCase("medium") ? Difficulty.MEDIUM : choice.equalsIgnoreCase("hard") ? Difficulty.HARD : Difficulty.EASY;


    }

    private String generateWinningNumber(Difficulty difficulty) {
        int numberOfGuessedNumbers;
        switch (difficulty) {
            case EASY -> numberOfGuessedNumbers = 4;
            case MEDIUM -> numberOfGuessedNumbers = 6;
            case HARD -> numberOfGuessedNumbers = 9;
            default -> numberOfGuessedNumbers = 4;
        }

        try {
            URI randomNumberURI = URI.create(String.format("https://www.random.org/integers/?num=%s&min=0&max=7&col=4&base=10&format=plain&rnd=new", numberOfGuessedNumbers));
            RestClient client = RestClient.create("https://www.random.org");

            ResponseEntity<String> responseEntity = client.get()
                                                          .uri(randomNumberURI)
                                                          .retrieve()
                                                          .toEntity(String.class);


            if (responseEntity.getStatusCode()
                              .value() != 200) {
                //if there is an issue, write fallback numbers and log this event.
                logger.error("Response from API was not OK. It was {}", responseEntity.getStatusCode()
                                                                                       .value());
                return generateLocalWinningNumber(difficulty);
            }
            String responseBody = responseEntity.getBody();
            // If body is null or blank, log this error and return a local list of numbers.
             if (responseBody == null || responseBody.isBlank()) {
                logger.error("Empty Response body from {}. Local list of random numbers were supplied.", randomNumberURI);
                return generateLocalWinningNumber(difficulty);
            }
             if(responseBody.matches(".*[A-Za-z].*")){
                 logger.error("Response body contained letters, which indicates some sort of failer. Here it is: {} ", responseBody);
                 return generateLocalWinningNumber(difficulty);
             }
            return responseBody.replaceAll("[^A-Za-z0-9]", "");
        } catch (Exception e) {
            logger.error("Catch clause activated. Error: {}", e.getMessage());
            return generateLocalWinningNumber(difficulty);
        }
    }


    private String generateLocalWinningNumber(Difficulty difficulty) {
        int numberOfGuessedNumbers;
        switch (difficulty) {
            case EASY -> numberOfGuessedNumbers = 4;
            case MEDIUM -> numberOfGuessedNumbers = 6;
            case HARD -> numberOfGuessedNumbers = 9;
            default -> numberOfGuessedNumbers = 4;
        }
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numberOfGuessedNumbers; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }


}
