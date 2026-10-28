package com.rafaelvincensi.gitrats.group.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateGroupDTO(

        @NotBlank(message = "Group Name cannot be empty!")
        @Size(min = 3, max = 26)
        String name,

        @Size(max = 80, message = "Description cannot exceed 80 characters")
        String description
) {}
