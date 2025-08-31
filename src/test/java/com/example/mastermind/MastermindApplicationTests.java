package com.example.mastermind;

import com.example.mastermind.dataAccessObjects.GameRepository;
import com.example.mastermind.dataAccessObjects.PlayerRepository;
import com.example.mastermind.dataTransferObjects.GameDTOs.Response.CurrentUserPastGames;
import com.example.mastermind.models.Game;
import com.example.mastermind.models.Player;
import com.example.mastermind.services.GameService;
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
    GameService gameService;

    @MockitoBean
    GameRepository gameRepository;
    @Autowired
    private PlayerRepository playerRepository;

    @Test
	void contextLoads() {
	}

    @Test
    void returnListOfGames(){

    UUID id = UUID.fromString("8bdc7872-65e4-46b3-8c72-5b53dcef72e9");

        gameRepository.findFinishedGames(id);
        System.out.println(gameService.returnCurrentUsersPastGames(id).getClass());
    }
}
