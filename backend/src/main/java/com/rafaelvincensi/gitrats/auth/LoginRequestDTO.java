package com.rafaelvincensi.gitrats.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "Email cannot be empty!")
        @Email(message = "Invalid format!")
        String email,

        @NotBlank(message = "Password cannot be empty!")
        String password
) {}
