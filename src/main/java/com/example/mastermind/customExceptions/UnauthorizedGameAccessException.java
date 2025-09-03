package com.example.mastermind.customExceptions;

public class UnauthorizedGameAccessException extends RuntimeException {
    public UnauthorizedGameAccessException() {
        super("You are not allowed to access or modify this game.");
    }
    public UnauthorizedGameAccessException(String message) {
        super(message);
    }
}