package com.rafaelvincensi.gitrats.group.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record GroupResponseDTO(
        UUID id,
        String name,
        String description,
        String code,
        String avatarUrl,
        LocalDateTime createdAt
) {}
