package com.example.mastermind.customExceptions;

public class UnauthenticatedUserException extends RuntimeException {
    public UnauthenticatedUserException() {
        super("User is not authenticated.");
    }
    public UnauthenticatedUserException(String message) {
        super(message);
    }
}