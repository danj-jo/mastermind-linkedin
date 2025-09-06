package com.example.mastermind.dataTransferObjects.PlayerDTOs;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class UserLoginRequest {
    @Email(message="Please provide a valid email address.")
    String email;
    String password;
}
