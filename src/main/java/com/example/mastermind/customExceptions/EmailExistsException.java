package com.example.mastermind.customExceptions;

public class EmailExistsException extends RuntimeException{
    public EmailExistsException(String message){
        super();
    }
    public EmailExistsException(){
        super("Email already exists.");
    }
}
