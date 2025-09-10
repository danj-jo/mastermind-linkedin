package com.example.mastermind.repositoryLayer;

import com.example.mastermind.models.entities.Player;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class PlayerRepositoryTest {

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    void findByUsername_WithExistingUsername_ReturnsPlayer() {
        // Given
        Player player = new Player(
                UUID.randomUUID(),
                "testuser",
                "password123",
                "test@example.com",
                "USER"
        );
        playerRepository.save(player);

        // When
        Optional<Player> result = playerRepository.findByUsername("testuser");

        // Then
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    void findByUsername_WithNonExistentUsername_ReturnsEmpty() {
        // When
        Optional<Player> result = playerRepository.findByUsername("nonexistent");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void existsByUsername_WithExistingUsername_ReturnsTrue() {
        // Given
        Player player = new Player(
                UUID.randomUUID(),
                "existinguser",
                "password123",
                "existing@example.com",
                "USER"
        );
        playerRepository.save(player);

        // When
        boolean exists = playerRepository.existsByUsername("existinguser");

        // Then
        assertTrue(exists);
    }

    @Test
    void existsByUsername_WithNonExistentUsername_ReturnsFalse() {
        // When
        boolean exists = playerRepository.existsByUsername("nonexistent");

        // Then
        assertFalse(exists);
    }

    @Test
    void existsByEmail_WithExistingEmail_ReturnsTrue() {
        // Given
        Player player = new Player(
                UUID.randomUUID(),
                "emailuser",
                "password123",
                "email@example.com",
                "USER"
        );
        playerRepository.save(player);

        // When
        boolean exists = playerRepository.existsByEmail("email@example.com");

        // Then
        assertTrue(exists);
    }

    @Test
    void existsByEmail_WithNonExistentEmail_ReturnsFalse() {
        // When
        boolean exists = playerRepository.existsByEmail("nonexistent@example.com");

        // Then
        assertFalse(exists);
    }
}