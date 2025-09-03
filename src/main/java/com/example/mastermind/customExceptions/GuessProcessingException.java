package com.example.mastermind.customExceptions;

public class GuessProcessingException extends RuntimeException {
    public GuessProcessingException() {
        super("Error processing guess.");
    }
    public GuessProcessingException(String message) {
        super(message);
    }
}