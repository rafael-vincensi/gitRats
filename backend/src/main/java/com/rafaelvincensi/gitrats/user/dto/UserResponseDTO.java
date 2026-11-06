package com.rafaelvincensi.gitrats.user.dto;

import com.rafaelvincensi.gitrats.user.User;

import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String email,
        String username,
        String name,
        String bio,
        String avatarUrl,
        String githubId,
        String githubUsername,
        Integer currentStreak,
        Integer highestStreak,
        Integer totalCheckIns,
        Integer weeklyCommits
){
    public static UserResponseDTO fromEntity(User user, Integer weeklyCommits){
        return new UserResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getName(),
                user.getBio(),
                user.getAvatarUrl(),
                user.getGithubId(),
                user.getGithubUsername(),
                user.getCurrentStreak(),
                user.getHighestStreak(),
                user.getTotalCheckIns(),
                weeklyCommits
        );
    }

    public static UserResponseDTO fromEntity(User user){
        return fromEntity(user, 0);
    }
}
