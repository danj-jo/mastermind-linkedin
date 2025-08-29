package com.example.mastermind.customExceptions;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


public class UsernameTooShortException extends RuntimeException{
   String message;

    public UsernameTooShortException(String message){
        super(message);
    }
    public UsernameTooShortException(){
        super("Username is too short.");
    }
}
