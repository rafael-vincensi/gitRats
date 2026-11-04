package com.rafaelvincensi.gitrats.auth.dto;

import java.util.UUID;

public record GithubUserResponseDTO(
        String id,
        String login
) {
}
