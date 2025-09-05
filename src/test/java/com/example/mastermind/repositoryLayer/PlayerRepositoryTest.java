package com.example.mastermind.repositoryLayer;

import com.example.mastermind.models.entities.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class PlayerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PlayerRepository playerRepository;

    private Player testPlayer;

    @BeforeEach
    void setUp() {
        testPlayer = new Player();
        testPlayer.setUsername("testuser");
        testPlayer.setPassword("encodedpassword");
        testPlayer.setEmail("test@example.com");
        testPlayer.setRole("ROLE_USER");
    }

    @Test
    void testSavePlayer() {
        // When
        Player savedPlayer = playerRepository.save(testPlayer);

        // Then
        assertNotNull(savedPlayer.getPlayerId());
        assertEquals("testuser", savedPlayer.getUsername());
        assertEquals("test@example.com", savedPlayer.getEmail());
    }

    @Test
    void testFindByUsername_Exists() {
        // Given
        Player savedPlayer = entityManager.persistAndFlush(testPlayer);

        // When
        Optional<Player> found = playerRepository.findByUsername("testuser");

        // Then
        assertTrue(found.isPresent());
        assertEquals(savedPlayer.getPlayerId(), found.get().getPlayerId());
    }

    @Test
    void testFindByUsername_NotExists() {
        // When
        Optional<Player> found = playerRepository.findByUsername("nonexistent");

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    void testExistsByUsername_True() {
        // Given
        entityManager.persistAndFlush(testPlayer);

        // When
        boolean exists = playerRepository.existsByUsername("testuser");

        // Then
        assertTrue(exists);
    }

    @Test
    void testExistsByUsername_False() {
        // When
        boolean exists = playerRepository.existsByUsername("nonexistent");

        // Then
        assertFalse(exists);
    }

    @Test
    void testExistsByEmail_True() {
        // Given
        entityManager.persistAndFlush(testPlayer);

        // When
        boolean exists = playerRepository.existsByEmail("test@example.com");

        // Then
        assertTrue(exists);
    }

    @Test
    void testExistsByEmail_False() {
        // When
        boolean exists = playerRepository.existsByEmail("nonexistent@example.com");

        // Then
        assertFalse(exists);
    }
}
