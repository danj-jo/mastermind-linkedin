package com.example.mastermind.dataTransferObjects.GameDTOs.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDao {
    String username;
    String email;
    public Map<String,String> toMap(){
        return new HashMap<>(Map.of("username", username,
                                    "email", email));
    }
}
