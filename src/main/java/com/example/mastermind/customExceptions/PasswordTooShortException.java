package com.example.mastermind.customExceptions;

public class PasswordTooShortException extends RuntimeException {
    public PasswordTooShortException(String message){
        super(message);
    }
    public PasswordTooShortException(){
        super("Password is too short.");
    }
}
