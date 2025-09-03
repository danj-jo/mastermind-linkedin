package com.example.mastermind.customExceptions;

public class NoActiveGameSessionException extends RuntimeException {
    public NoActiveGameSessionException() {
        super("No active game session. Start a new game first.");
    }
    public NoActiveGameSessionException(String message) {
        super(message);
    }
}