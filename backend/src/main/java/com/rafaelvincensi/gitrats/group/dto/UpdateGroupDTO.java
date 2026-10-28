package com.rafaelvincensi.gitrats.group.dto;

import jakarta.validation.constraints.Size;

public record UpdateGroupDTO(

        @Size(min = 3, max = 26, message = "Username must be between 3 and 26 characters")
        String name,

        @Size(max = 80, message = "Description cannot exceed 80 characters")
        String description,
        String avatarUrl
) {
}
