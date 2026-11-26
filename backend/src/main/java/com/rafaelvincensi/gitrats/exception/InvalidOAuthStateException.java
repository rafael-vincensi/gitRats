package com.rafaelvincensi.gitrats.exception;

public class InvalidOAuthStateException extends RuntimeException {
    public InvalidOAuthStateException(String message) {
        super(message);
    }
}
