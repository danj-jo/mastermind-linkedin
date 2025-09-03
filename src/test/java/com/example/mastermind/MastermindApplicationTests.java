package com.example.mastermind;

import com.example.mastermind.dataAccessObjects.SingleplayerGameRepository;
import com.example.mastermind.dataAccessObjects.PlayerRepository;
import com.example.mastermind.models.Difficulty;
import com.example.mastermind.models.entities.MultiplayerGame;
import com.example.mastermind.models.entities.Player;
import com.example.mastermind.services.SingleplayerGameService;
import com.example.mastermind.services.PlayerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.*;

@ContextConfiguration
@SpringBootTest
class MastermindApplicationTests {
    @MockitoBean
    PlayerService playerService;

    @MockitoBean
    SingleplayerGameService singleplayerGameService;

    @MockitoBean
    SingleplayerGameRepository gameRepository;
    @Autowired
    private PlayerRepository playerRepository;

    /**
     * Creates two test players for use in tests.
     * @return List containing the test players
     */
    List <Player> createPlayers(){
       Player player1 = new Player(
             UUID.randomUUID(),
             "jim",
             "psswrd",
             "jim@googly.web",
             "USER"
       );
       Player player2 = new Player(
               UUID.randomUUID(),
               "sky",
               "psswrd",
               "sky@googly.web",
               "USER"
       );
       return new ArrayList<>(List.of(player1,player2));
    }
    @Test
	void contextLoads() {
	}

    @Test
    void returnListOfGames(){

    UUID id = UUID.fromString("8bdc7872-65e4-46b3-8c72-5b53dcef72e9");

        gameRepository.findFinishedGames(id);
        System.out.println(singleplayerGameService.returnCurrentUsersPastGames(id).getClass());
    }

    @Test
    void testMultiplayerGame(){
        List<Player> players = createPlayers();
        MultiplayerGame newgame = new MultiplayerGame();
        newgame.setGameId(UUID.randomUUID());
        newgame.setPlayers(players);
        newgame.setWinningNumber("1234");
        newgame.setDifficulty(Difficulty.EASY);

        System.out.println(newgame.submitGuess(players.get(0),"3456"));
        System.out.println(newgame.getGuesses().toString());

    }
}

