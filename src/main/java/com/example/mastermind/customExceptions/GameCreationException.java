package com.example.mastermind.customExceptions;

public class GameCreationException extends RuntimeException {
    public GameCreationException() {
        super("Error in creation process.");
    }
    public GameCreationException(String message) {
        super(message);
    }
}