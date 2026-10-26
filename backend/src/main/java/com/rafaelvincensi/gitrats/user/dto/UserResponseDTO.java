package com.rafaelvincensi.gitrats.user.dto;

import com.rafaelvincensi.gitrats.user.User;

import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String email,
        String username,
        String name,
        String bio,
        String githubId,
        String githubUsername
){
    public static UserResponseDTO fromEntity(User user){
        return new UserResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getName(),
                user.getBio(),
                user.getGithubId(),
                user.getGithubUsername());
}
}
