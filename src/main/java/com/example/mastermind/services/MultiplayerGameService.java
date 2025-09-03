package com.example.mastermind.services;

import com.example.mastermind.models.Difficulty;
import com.example.mastermind.models.EmitterRegistry;
import com.example.mastermind.models.entities.MultiplayerGame;
import com.example.mastermind.models.entities.Player;
import com.example.mastermind.utils.EmitterDiagnostics;
import com.example.mastermind.utils.GameUtils;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

// add migrations

@Service
@AllArgsConstructor
public class MultiplayerGameService {
    // create a queue for players to join, so that when it has two people, I can poll them and create a game with them. They must be concurrent, in case many users join at once.
    // first thought: create three separate queues to hold players. This is good, but would require two sets of if statements per queue. (check if difficulty == ? then add to queue, and check each queue for size, and start a game with the difficulty level of said queue).
    // revised: create a map, that holds <String,Queue<Player>>, I can iterate through the map once, and each time one is the desired size, I can ccreate a game, using the selectDifficulty helper method, and use the key for the game to execute the method.
    private static final Logger logger = LoggerFactory.getLogger(MultiplayerGameService.class);

    private final PlayerService playerService;
    private final EmitterDiagnostics emitterDiagnostics;
    private final EmitterRegistry emitterRegistry;
    private final Queue<Player> playersWaitingForEasyGame = new ConcurrentLinkedQueue<>();
    private final Queue<Player> playersWaitingForMediumGame = new ConcurrentLinkedQueue<>();
    private final Queue<Player> playersWaitingForHardGame = new ConcurrentLinkedQueue<>();
    private final Map<String,Queue<Player>> queueContainer = new ConcurrentHashMap<>(Map.of(
            "EASY",playersWaitingForEasyGame,
            "MEDIUM", playersWaitingForMediumGame,
            "HARD",playersWaitingForHardGame
    ));

    // use concurrent hash map so it can be updated in different threads concurrently. Use the gameID (UUID) and the Game state (Game) because the id finds the game, and the game holds the state for it.
    public final Map<UUID, MultiplayerGame> activeGames = new ConcurrentHashMap<>();
    // first, I'll need to add the player introduced into the queue of waiting players.
    // if the queue has 2 people, I will create a new game, setting the players of the game to the 2 players I pop off of the queue, and I will set the random number to whatever I generate. The mulitplayer mode will still have to have difficulties.
    // with difficulties, I can add users to the appropriate queue based off of the string value.

    public void joinGame(Player player, String difficulty){
//         first move: add the player to the guess queue that corresponds with their difficulty.
        for(String qKey: queueContainer.keySet()){
            Queue<Player> qValue = queueContainer.get(qKey);
                synchronized (qValue){
                    if(qKey.equalsIgnoreCase(difficulty) && !qValue.contains(player)){
                     qValue.add(player);
                     }
                 }
        }
//         second: iterate through map, and whichever list has 2 or more people, create a game using the key.
        queueContainer.forEach((key,value) -> {
           synchronized (value){if(value.size() >= 2){
                Player player1 = value.poll();
                Player player2 = value.poll();
                assert player2 != null;
                List<Player> players = new ArrayList<>(List.of(player1, player2));
                MultiplayerGame game = new MultiplayerGame();
                game.setDifficulty(GameUtils.selectUserDifficulty(key));
                game.setWinningNumber(GameUtils.generateWinningNumber(Difficulty.valueOf(difficulty.toUpperCase())));
                System.out.println(game.getWinningNumber());
                game.setPlayers(players);
                activeGames.put(game.getGameId(),game);
                emitterDiagnostics.logMatchAttempt(player1, player2);
                // get emitter for player 1
                try {
                    emitterRegistry.getEmitter(player1.getPlayerId())
                                   .send(SseEmitter.event()
                                                    .name("matched")
                                                   .data(game.getGameId()));
                }
                catch(Exception e) {
                    emitterRegistry.removeEmitter(player1.getPlayerId());
                    logger.error("Player 1's emitter disconnected. Removed.");
                        try {
                            // if player 1 disconnects, emit this event and tell player 2.
                            emitterRegistry.getEmitter(player2.getPlayerId()).send(SseEmitter.event().name("disconnect").data("Player 1 has disconnected."));
                        } catch (IOException ey) {
                            logger.error("Player 1 was also disconnected. No notification sent.");
                        }

                }
                // get emitter for player 2
                try {
                    emitterRegistry.getEmitter(player2.getPlayerId()).send(SseEmitter.event().name("matched").data(game.getGameId()));
                    System.out.println(player1.getPlayerId());
                } catch (IOException e) {
                    emitterRegistry.removeEmitter(player2.getPlayerId());
                    logger.error("Player 2's emitter disconnected. Removed.");
                    try {
                        // if player 2 is disconnected, send this event and tell player 1.
                        emitterRegistry.getEmitter(player1.getPlayerId()).send(SseEmitter.event().name("disconnect").data("Player 2 has disconnected."));
                    } catch (IOException ex) {
                        logger.error("player 1 is also disconnected.");
                    }
                }
            }}
        });
    }

    public Map<String,Object> findMultiplayerGameDetails(UUID id){
        try{
            MultiplayerGame game = activeGames.get(id);
            String winningNumber = game.getWinningNumber();
            System.out.println(winningNumber);
            return new HashMap<>(Map.of("numbersToGuess", winningNumber.length()));
        } catch(Exception e){
            return new HashMap<>(Map.of("Error", e.getMessage()));
        }
    }
}
