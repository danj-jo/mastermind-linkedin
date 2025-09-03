package com.example.mastermind.utils;

import com.example.mastermind.models.Difficulty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.Random;

/**
 * Utility class for game-related operations and random number generation.
 * 
 * This class provides static methods for:
 * - Converting string difficulty choices to Difficulty enum values
 * - Generating winning numbers for games based on difficulty level
 * - Fallback local random number generation when external API fails
 * 
 * The class attempts to use random.org for truly random numbers but falls back to local generation if the external service is unavailable or returns invalid data, and logs the error.
 * This ensures game functionality continues even during external service outages.
 */
public class GameUtils {
    private static final Logger logger = LoggerFactory.getLogger(GameUtils.class);

    public static Difficulty selectUserDifficulty(String choice){

        return choice.equalsIgnoreCase("easy") ? Difficulty.EASY : choice.equalsIgnoreCase("medium") ? Difficulty.MEDIUM : choice.equalsIgnoreCase("hard") ? Difficulty.HARD : Difficulty.EASY;


    }

    public static String generateWinningNumber(Difficulty difficulty) {
        int numberOfGuessedNumbers = switch (difficulty) {
            case EASY ->  4;
            case MEDIUM -> 6;
            case HARD ->  9;
        };

        int maxNumber = switch(difficulty) {
            case EASY -> 7;
            case MEDIUM -> 8;
            case HARD -> 9;
        };

        try {
            URI randomNumberURI = URI.create(String.format("https://www.random.org/integers/?num=%s&min=0&max=%s&col=4&base=10&format=plain&rnd=new", numberOfGuessedNumbers,maxNumber));
            RestClient client = RestClient.create("https://www.random.org");

            ResponseEntity<String> responseEntity = client.get()
                                                          .uri(randomNumberURI)
                                                          .retrieve()
                                                          .toEntity(String.class);


            if (responseEntity.getStatusCode()
                              .value() != 200) {

                logger.error("Response from API was not OK. It was {}", responseEntity.getStatusCode()
                                                                                      .value());
                return generateLocalWinningNumber(difficulty);
            }
            String responseBody = responseEntity.getBody();

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

    public static String generateLocalWinningNumber(Difficulty difficulty) {
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
