package com.rafaelvincensi.gitrats.user;

public record CreateUserRequest (
        String username,
        String name,
        String email,
        String password
){}
