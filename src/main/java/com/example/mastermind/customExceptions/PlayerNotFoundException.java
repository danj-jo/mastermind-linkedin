package com.example.mastermind.customExceptions;

public class PlayerNotFoundException extends RuntimeException {
    public PlayerNotFoundException() {
        super("Player not found.");
    }
    public PlayerNotFoundException(String message) {
        super(message);
    }
}