package com.rafaelvincensi.gitrats.checkIn;

import java.util.UUID;

public record GroupLeaderboardDTO(
        UUID userId,
        String name,
        String username,
        String avatarUrl,
        long score
) {
}
