package com.rafaelvincensi.gitrats.user;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String name,
        String bio,
        String avatarUrl,
        String githubUsername
){}
