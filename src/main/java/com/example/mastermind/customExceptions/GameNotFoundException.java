package com.example.mastermind.customExceptions;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException() {
        super("Game not found.");
    }
    public GameNotFoundException(String message) {
        super(message);
    }
}