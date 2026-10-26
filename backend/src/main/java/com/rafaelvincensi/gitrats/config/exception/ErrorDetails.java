package com.rafaelvincensi.gitrats.config.exception;

import java.time.LocalDateTime;

public record ErrorDetails(
        int status,
        String message,
        LocalDateTime localDateTime
) {}
