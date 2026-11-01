package com.rafaelvincensi.gitrats.CheckIn.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateCheckInDTO(

        @NotBlank
        @Size(max = 16)
        String title,

        @Size(max = 100)
        String description,

        @NotBlank
        String imageUrl,

        @NotNull
        UUID groupId
){}
