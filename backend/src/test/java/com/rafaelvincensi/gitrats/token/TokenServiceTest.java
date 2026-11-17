package com.rafaelvincensi.gitrats.token;

import com.rafaelvincensi.gitrats.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", "my-super-secret-test-key-123456");
        ReflectionTestUtils.setField(tokenService, "expirationTime", 3600000L);
    }

    @Test
    @DisplayName("Should generate token successfully")
    void shouldGenerateTokenSuccessfully() {
        User user = new User();
        user.setEmail("rafinha@gmail.com");

        String token = tokenService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    @DisplayName("Should validate token successfully")
    void shouldValidateTokenSuccessfully(){
        User user = new User();
        user.setEmail("mrlonely@gmail.com");

        String token = tokenService.generateToken(user);
        String validateToken = tokenService.validateToken(token);

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertNotNull(validateToken);
        assertEquals("mrlonely@gmail.com", validateToken);
    }

    @Test
    @DisplayName("Should return empty string when token is invalid")
    void shouldReturnEmptyStringWhenTokenIsInvalid(){
        String result = tokenService.validateToken("token-invalido");

        assertEquals("", result);
    }

    @Test
    @DisplayName("Should return empty string when token is expired")
    void shouldReturnEmptyStringWhenTokenIsExpired(){
        ReflectionTestUtils.setField(tokenService, "expirationTime", -10000L);

        User user = new User();
        user.setEmail("feliciathegoat@gmail.com");

        String token = tokenService.generateToken(user);
        String result = tokenService.validateToken(token);

        assertEquals("", result);
    }
}