package com.example.mastermind.dataTransferObjects.ErrorDTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {
   String error;
   String details;

    public static Map<String, String> toMap(RuntimeException e){
        return new HashMap<>(Map.of(
                "Error", e.getMessage()
        ));
    }
}
