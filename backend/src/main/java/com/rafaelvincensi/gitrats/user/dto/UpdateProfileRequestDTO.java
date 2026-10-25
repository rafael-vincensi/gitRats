package com.rafaelvincensi.gitrats.user.dto;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequestDTO(

        @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
        String username,
        String name,
        String avatarUrl,

        @Size(max = 255, message = "Bio cannot exceed 255 characters")
        String bio,
        String timezone
){}