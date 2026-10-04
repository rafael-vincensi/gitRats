package com.rafaelvincensi.gitrats.auth;

public record LoginRequest(
        String email,
        String password
) {}
