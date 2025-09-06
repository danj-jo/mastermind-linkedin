package com.example.mastermind.customExceptions;

public class UsernameContainsInvalidCharactersException extends RuntimeException{

    public UsernameContainsInvalidCharactersException(){
        super("Username contains invalid characters.");
}

    public UsernameContainsInvalidCharactersException(String message){
    super(message);
}
}
