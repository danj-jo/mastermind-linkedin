//package com.example.mastermind.repositoryLayer;
//
//import com.example.mastermind.models.Difficulty;
//import com.example.mastermind.models.entities.Player;
//import com.example.mastermind.models.entities.SinglePlayerGame;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@DataJpaTest
//@ActiveProfiles("test")
//class SingleplayerGameRepositoryTest {
//
//    @Autowired
//    private TestEntityManager entityManager;
//
//    @Autowired
//    private SingleplayerGameRepository singleplayerGameRepository;
//
//    private Player player;
//
//    @BeforeEach
//    void setUp() {
//        player = new Player();
//        player.setUsername("single_repo_user");
//        player.setPassword("password");
//        player.setEmail("single_repo_user@example.com");
//        player.setRole("ROLE_USER");
//        player = entityManager.persistAndFlush(player);
//    }
//
//    private SinglePlayerGame buildGame(boolean finished) {
//        SinglePlayerGame game = new SinglePlayerGame();
//        game.setPlayer(player);
//        game.setWinningNumber("1234");
//        game.setDifficulty(Difficulty.EASY);
//        game.setGuesses(new ArrayList<>());
//        game.setFinished(finished);
//        return game;
//    }
//
//    @Test
//    void testExistsByGameIdAndFinishedTrue() {
//        SinglePlayerGame finishedGame = buildGame(true);
//        finishedGame = entityManager.persistAndFlush(finishedGame);
//
//        boolean existsFinished = singleplayerGameRepository.existsByGameIdAndFinishedTrue(finishedGame.getGameId());
//        assertTrue(existsFinished);
//
//        SinglePlayerGame unfinishedGame = buildGame(false);
//        unfinishedGame = entityManager.persistAndFlush(unfinishedGame);
//
//        boolean existsUnfinishedAsFinished = singleplayerGameRepository.existsByGameIdAndFinishedTrue(unfinishedGame.getGameId());
//        assertFalse(existsUnfinishedAsFinished);
//    }
//
//    @Test
//    void testExistsByGameId() {
//        SinglePlayerGame game = buildGame(false);
//        game = entityManager.persistAndFlush(game);
//
//        assertTrue(singleplayerGameRepository.existsByGameId(game.getGameId()));
//        assertFalse(singleplayerGameRepository.existsByGameId(UUID.randomUUID()));
//    }
//
//    @Test
//    void testFindGameByGameId() {
//        SinglePlayerGame game = buildGame(false);
//        game = entityManager.persistAndFlush(game);
//
//        Optional<SinglePlayerGame> found = singleplayerGameRepository.findGameByGameId(game.getGameId());
//        assertTrue(found.isPresent());
//        assertEquals(game.getGameId(), found.get().getGameId());
//
//        Optional<SinglePlayerGame> notFound = singleplayerGameRepository.findGameByGameId(UUID.randomUUID());
//        assertTrue(notFound.isEmpty());
//    }
//
//    @Test
//    void testFindFinishedAndUnfinishedGamesByPlayer() {
//        // Given one finished and one unfinished game for same player
//        SinglePlayerGame finishedGame = buildGame(true);
//        SinglePlayerGame unfinishedGame = buildGame(false);
//        entityManager.persist(finishedGame);
//        entityManager.persist(unfinishedGame);
//        entityManager.flush();
//
//        List<SinglePlayerGame> finished = singleplayerGameRepository.findFinishedGames(player.getPlayerId());
//        List<SinglePlayerGame> unfinished = singleplayerGameRepository.findUnfinishedGames(player.getPlayerId());
//
//        assertEquals(1, finished.size());
//        assertTrue(finished.get(0).finished());
//
//        assertEquals(1, unfinished.size());
//        assertFalse(unfinished.get(0).finished());
//    }
//}
