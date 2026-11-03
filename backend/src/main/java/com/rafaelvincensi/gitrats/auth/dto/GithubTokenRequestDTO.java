package com.rafaelvincensi.gitrats.auth.dto;

public record GithubTokenRequestDTO(
        String client_id,
        String client_secret,
        String code,
        String redirect_uri
){}
