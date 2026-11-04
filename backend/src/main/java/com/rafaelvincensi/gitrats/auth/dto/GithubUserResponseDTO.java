package com.rafaelvincensi.gitrats.auth.dto;

import java.util.UUID;

public record GithubUserResponseDTO(
        UUID id,
        String login
) {
}
