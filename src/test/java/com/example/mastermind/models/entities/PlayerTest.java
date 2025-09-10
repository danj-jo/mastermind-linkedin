package com.example.mastermind.models.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player(
                UUID.randomUUID(),
                "testuser",
                "password123",
                "test@example.com",
                "USER"
        );
    }

    @Test
    void playerConstructor_CreatesCorrectInstance() {
        // Then
        assertNotNull(player);
        assertNotNull(player.getPlayerId());
        assertEquals("testuser", player.getUsername());
        assertEquals("password123", player.getPassword());
        assertEquals("test@example.com", player.getEmail());
        assertEquals("USER", player.getRole());
    }

    @Test
    void getAuthorities_ReturnsCorrectAuthority() {
        // When
        Collection<? extends GrantedAuthority> authorities = player.getAuthorities();

        // Then
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("USER")));
    }

    @Test
    void isAccountNonExpired_ReturnsTrue() {
        // When & Then
        assertTrue(player.isAccountNonExpired());
    }

    @Test
    void isAccountNonLocked_ReturnsTrue() {
        // When & Then
        assertTrue(player.isAccountNonLocked());
    }

    @Test
    void isCredentialsNonExpired_ReturnsTrue() {
        // When & Then
        assertTrue(player.isCredentialsNonExpired());
    }

    @Test
    void isEnabled_ReturnsTrue() {
        // When & Then
        assertTrue(player.isEnabled());
    }

    @Test
    void playerSetters_UpdateValuesCorrectly() {
        // Given
        UUID newId = UUID.randomUUID();

        // When
        player.setPlayerId(newId);
        player.setUsername("newuser");
        player.setPassword("newpassword");
        player.setEmail("new@example.com");
        player.setRole("ADMIN");

        // Then
        assertEquals(newId, player.getPlayerId());
        assertEquals("newuser", player.getUsername());
        assertEquals("newpassword", player.getPassword());
        assertEquals("new@example.com", player.getEmail());
        assertEquals("ADMIN", player.getRole());
    }

    @Test
    void playerNoArgsConstructor_CreatesEmptyInstance() {
        // When
        Player emptyPlayer = new Player();

        // Then
        assertNotNull(emptyPlayer);
        assertNull(emptyPlayer.getPlayerId());
        assertNull(emptyPlayer.getUsername());
        assertNull(emptyPlayer.getPassword());
        assertNull(emptyPlayer.getEmail());
        assertNull(emptyPlayer.getRole());
    }
}