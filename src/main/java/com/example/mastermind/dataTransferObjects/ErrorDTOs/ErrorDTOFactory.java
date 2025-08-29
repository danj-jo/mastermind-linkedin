package com.example.mastermind.dataTransferObjects.ErrorDTOs;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
public class ErrorDTOFactory {
    RuntimeException e;
    String message;

    public Map<String, String> toMap(){
        return new HashMap<>(Map.of(
                "Error", e.getMessage()
        ));
    }
}
