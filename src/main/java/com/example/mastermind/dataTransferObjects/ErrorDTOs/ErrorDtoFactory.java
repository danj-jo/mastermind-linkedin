package com.example.mastermind.dataTransferObjects.ErrorDTOs;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
public class ErrorDtoFactory {
    RuntimeException e;

    public static Map<String, String> toMap(RuntimeException e){
        return new HashMap<>(Map.of(
                "Error", e.getMessage()
        ));
    }
}
