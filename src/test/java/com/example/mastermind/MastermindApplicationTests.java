package com.example.mastermind;

import com.example.mastermind.repositoryLayer.SingleplayerGameRepository;
import com.example.mastermind.repositoryLayer.PlayerRepository;
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



}

