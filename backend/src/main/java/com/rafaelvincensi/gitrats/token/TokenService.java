package com.rafaelvincensi.gitrats.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.rafaelvincensi.gitrats.exception.ExpiredJwtException;
import com.rafaelvincensi.gitrats.exception.NotFoundException;
import com.rafaelvincensi.gitrats.user.User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class TokenService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expirationTime;

    @Value("${jwt.refresh.expiration}")
    private Long refreshExpirationTime;
    private final TokenRepository tokenRepository;

    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.create()
                    .withIssuer("gitrats-api")
                    .withSubject(user.getEmail())
                    .withExpiresAt(Instant.now().plusMillis(expirationTime))
                    .sign(algorithm);
        } catch (JWTCreationException ex) {
            throw new RuntimeException("Error generating token", ex);
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.require(algorithm)
                    .withIssuer("gitrats-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException ex) {
            return "";
        }
    }

    @Transactional
    public Token createRefreshToken(User user) {
        Token refreshToken = tokenRepository.findByUser(user)
                .orElse(new Token());

        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshExpirationTime));

        return tokenRepository.save(refreshToken);
    }

    @Transactional(noRollbackFor = ExpiredJwtException.class)
    public Token verifyRefreshToken(String token) {
        Token refreshToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new NotFoundException("Token not found!"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())){
            tokenRepository.delete(refreshToken);
            throw new ExpiredJwtException("Refresh token expired. Please log in again.");
        }
        return refreshToken;
    }

}
