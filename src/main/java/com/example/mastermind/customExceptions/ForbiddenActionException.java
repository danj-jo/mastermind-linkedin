package com.example.mastermind.customExceptions;

public class ForbiddenActionException extends RuntimeException{
    public ForbiddenActionException(String message){
        super();
    }
    public ForbiddenActionException(){
        super("PlayerID is not stored. User must log in.");
    }
}
