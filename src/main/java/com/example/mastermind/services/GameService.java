package com.example.mastermind.services;

import com.example.mastermind.dataAccessObjects.GameRepository;
import com.example.mastermind.dataAccessObjects.PlayerRepository;
import com.example.mastermind.dataTransferObjects.GameDTOs.Response.CurrentUserPastGames;
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
import java.util.*;

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

    // if I send this as a map, it can be parsed on the front end and displayed accordingly.
    public Map<String, List<CurrentUserPastGames>> returnCurrentUsersPastGames(UUID playerId){
try {
    List<CurrentUserPastGames> finishedGames = gameRepository.findFinishedGames(playerId).
                                                             stream().
                                                             map((game -> {
                                                                 return new CurrentUserPastGames(game.getGameId().toString(),
                                                                                                 String.valueOf(game.getDifficulty()),
                                                                                                 String.valueOf(game.getResult()),
                                                                                                 game.getWinningNumber(),
                                                                                                 game.getGuesses().toString(),
                                                                                                 String.valueOf(game.isFinished()));
                                                             }))
                                                             .toList();

    List<CurrentUserPastGames> unfinishedGames = gameRepository.findUnfinishedGames(playerId).
                                                               stream().
                                                               map((game -> {
                                                                   return new CurrentUserPastGames(game.getGameId().toString(),
                                                                                                   String.valueOf(game.getDifficulty()),
                                                                                                   String.valueOf(game.getResult()),
                                                                                                   "Finish to see results!",
                                                                                                   game.getGuesses().toString(),
                                                                                                   String.valueOf(game.isFinished()));
                                                               }))
                                                               .toList();

    Map<String, List<CurrentUserPastGames>> result = new HashMap<>();
    Player player = playerRepository.findById(playerId).orElseThrow();
    String username = player.getUsername();
    result.put("finished", finishedGames);
    result.put("unfinished", unfinishedGames);
    return result;
} catch(Exception e){
    throw new RuntimeException("Found the issue.");
}
    }

    public Game findById(UUID gameId){
      return gameRepository.findGameByGameId(gameId).orElseThrow();
    }

    public String isGameFinished(UUID gameId){
        return gameRepository.existsByGameIdAndIsFinishedTrue(gameId)
 ? "true" : "false";
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
