package com.rafaelvincensi.gitrats.user;

public record UpdateProfileRequest(
        String username,
        String name,
        String avatarUrl,
        String bio,
        String timezone
){}