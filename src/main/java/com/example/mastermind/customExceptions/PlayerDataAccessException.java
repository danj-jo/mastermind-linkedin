package com.example.mastermind.customExceptions;

public class PlayerDataAccessException extends RuntimeException {
    public PlayerDataAccessException() {
        super("Error accessing player data.");
    }
    public PlayerDataAccessException(String message) {
        super(message);
    }
}