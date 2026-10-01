package com.rafaelvincensi.gitrats.user;

public record CreateUserRequest (
        String username,
        String email,
        String password
){}
