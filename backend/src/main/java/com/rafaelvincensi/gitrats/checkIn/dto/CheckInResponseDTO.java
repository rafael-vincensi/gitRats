package com.rafaelvincensi.gitrats.checkIn.dto;

import com.rafaelvincensi.gitrats.checkIn.CheckIn;

import java.time.LocalDateTime;
import java.util.UUID;

public record CheckInResponseDTO(
        UUID id,
        String title,
        String description,
        String imageUrl,
        String username,
        String userAvatarUrl,
        String groupName,
        String groupAvatarUrl,
        LocalDateTime createdAt
) {
    static public CheckInResponseDTO fromEntity(CheckIn checkIn){
        return new CheckInResponseDTO(
                checkIn.getId(),
                checkIn.getTitle(),
                checkIn.getDescription(),
                checkIn.getImageUrl(),
                checkIn.getUser().getUsername(),
                checkIn.getUser().getAvatarUrl(),
                checkIn.getGroup().getName(),
                checkIn.getGroup().getAvatarUrl(),
                checkIn.getCreatedAt()
        );
    }
}
