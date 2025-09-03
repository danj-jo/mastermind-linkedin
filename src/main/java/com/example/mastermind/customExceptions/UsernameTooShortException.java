package com.example.mastermind.customExceptions;


public class UsernameTooShortException extends RuntimeException{
   String message;

    public UsernameTooShortException(String message){
        super(message);
    }
    public UsernameTooShortException(){
        super("Username is too short.");
    }
}
