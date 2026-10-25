package com.rafaelvincensi.gitrats.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequestDTO(

        @NotBlank(message = "Username cannot be empty!")
        @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
        String username,

        @NotBlank
        String name,

        @NotBlank(message = "Email cannot be empty!")
        @Email(message = "Invalid email format!")
        String email,

        @NotBlank(message = "Password cannot be empty!")
        @Size(min = 6, message = "The password must be at least 6 characters long!")
        String password
){}
