package com.example.mastermind.customExceptions;

public class GameUpdateException extends RuntimeException {
    public GameUpdateException() {
        super("Error updating game.");
    }
    public GameUpdateException(String message) {
        super(message);
    }
}