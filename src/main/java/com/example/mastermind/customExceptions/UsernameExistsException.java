package com.example.mastermind.customExceptions;

public class UsernameExistsException extends RuntimeException {
    public UsernameExistsException(String message){
        super(message);
    }
    public UsernameExistsException(){
        super("Username already exists.");
    }
}
