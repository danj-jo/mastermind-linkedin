package com.example.mastermind.models.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player();
        player.setPlayerId(UUID.randomUUID());
        player.setUsername("testuser");
        player.setPassword("encodedpassword");
        player.setEmail("test@example.com");
        player.setRole("ROLE_USER");
    }

    @Test
    void testPlayerCreation() {
        // Then
        assertNotNull(player.getPlayerId());
        assertEquals("testuser", player.getUsername());
        assertEquals("encodedpassword", player.getPassword());
        assertEquals("test@example.com", player.getEmail());
        assertEquals("ROLE_USER", player.getRole());
    }

    @Test
    void testGetAuthorities() {
        // When
        Collection<?> authorities = player.getAuthorities();

        // Then
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void testAccountStatus() {
        // Then
        assertTrue(player.isAccountNonExpired());
        assertTrue(player.isAccountNonLocked());
        assertTrue(player.isCredentialsNonExpired());
        assertTrue(player.isEnabled());
    }

    @Test
    void testPlayerEquality() {
        // Given
        Player samePlayer = new Player();
        samePlayer.setPlayerId(player.getPlayerId());
        samePlayer.setUsername("testuser");
        samePlayer.setPassword("encodedpassword");
        samePlayer.setEmail("test@example.com");
        samePlayer.setRole("ROLE_USER");

        // Then
        assertEquals(player.getPlayerId(), samePlayer.getPlayerId());
        assertEquals(player.getUsername(), samePlayer.getUsername());
    }

    @Test
    void testPlayerWithDifferentRole() {
        // Given
        player.setRole("ROLE_ADMIN");

        // When
        Collection<?> authorities = player.getAuthorities();

        // Then
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }
}
