package com.rafaelvincensi.gitrats.checkIn.dto;

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

        @NotNull
        UUID groupId
){}
