package com.example.mastermind.services;

import com.example.mastermind.dataAccessObjects.GameRepository;
import com.example.mastermind.dataAccessObjects.PlayerRepository;
import com.example.mastermind.dataTransferObjects.GameDTOs.Response.CurrentUserPastGames;
import com.example.mastermind.models.Difficulty;
import com.example.mastermind.models.entities.SinglePlayerGame;
import com.example.mastermind.models.entities.Player;
import com.example.mastermind.utils.GameUtils;
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


    public SinglePlayerGame createNewGame(String playerDifficulty, UUID playerId){


        Player player = playerRepository.findById(playerId).orElseThrow(() -> new UsernameNotFoundException("Player Not found."));
        SinglePlayerGame singlePlayerGame = new SinglePlayerGame();

        singlePlayerGame.setDifficulty(GameUtils.selectUserDifficulty(playerDifficulty));
        singlePlayerGame.setPlayer(player);
        singlePlayerGame.setWinningNumber(GameUtils.generateWinningNumber(Difficulty.valueOf(playerDifficulty)));
        singlePlayerGame.setGuesses(new ArrayList<>());

        gameRepository.saveAndFlush(singlePlayerGame);
        return singlePlayerGame;

    }

    public String makeGuess(UUID gameId, String guess){
        SinglePlayerGame currentGame = gameRepository.findById(gameId)
                                                                 .orElseThrow(() -> new RuntimeException("Game not found"));
        String feedback = currentGame.submitGuess(guess);
        gameRepository.saveAndFlush(currentGame);
        return feedback;
    }

    // if I send this as a map, it can be parsed on the front end and displayed accordingly.
    public Map<String, List<CurrentUserPastGames>> returnCurrentUsersPastGames(UUID playerId){
try {
    List<CurrentUserPastGames> finishedGames = gameRepository.findFinishedGames(playerId).
                                                             stream().
                                                             map((singlePlayerGame -> {
                                                                 return new CurrentUserPastGames(singlePlayerGame.getGameId().toString(),
                                                                                                 String.valueOf(singlePlayerGame.getDifficulty()),
                                                                                                 String.valueOf(singlePlayerGame.getResult()),
                                                                                                 singlePlayerGame.getWinningNumber(),
                                                                                                 singlePlayerGame.getGuesses().toString(),
                                                                                                 String.valueOf(singlePlayerGame.isFinished()));
                                                             }))
                                                             .toList();

    List<CurrentUserPastGames> unfinishedGames = gameRepository.findUnfinishedGames(playerId).
                                                               stream().
                                                               map((singlePlayerGame -> {
                                                                   return new CurrentUserPastGames(singlePlayerGame.getGameId().toString(),
                                                                                                   String.valueOf(singlePlayerGame.getDifficulty()),
                                                                                                   String.valueOf(singlePlayerGame.getResult()),
                                                                                                   "Finish to see results!",
                                                                                                   singlePlayerGame.getGuesses().toString(),
                                                                                                   String.valueOf(singlePlayerGame.isFinished()));
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

    public SinglePlayerGame findById(UUID gameId){
      return gameRepository.findGameByGameId(gameId).orElseThrow();
    }

    public String isGameFinished(UUID gameId){
        return gameRepository.existsByGameIdAndIsFinishedTrue(gameId)
 ? "true" : "false";
    }

}
