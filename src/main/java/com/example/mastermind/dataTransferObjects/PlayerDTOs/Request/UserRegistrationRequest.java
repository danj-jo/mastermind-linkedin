package com.example.mastermind.dataTransferObjects.PlayerDTOs.Request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserRegistrationRequest {
    String username;
    String email;
    String password;
}
