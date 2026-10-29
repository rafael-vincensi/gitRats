package com.rafaelvincensi.gitrats.group.dto;

import com.rafaelvincensi.gitrats.group.Group;

import java.time.LocalDateTime;
import java.util.UUID;

public record GroupResponseDTO(
        UUID id,
        String name,
        String description,
        String code,
        String avatarUrl,
        LocalDateTime createdAt
) {
    public static GroupResponseDTO fromEntity(Group group){
        return new GroupResponseDTO(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getCode(),
                group.getAvatarUrl(),
                group.getCreatedAt()
        );
    }
}
