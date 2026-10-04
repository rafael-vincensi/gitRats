package com.rafaelvincensi.gitrats.token;

import com.rafaelvincensi.gitrats.user.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class TokenProvider {

    @Value("${jwt.secret}")
    private String key;

    @Value("${jwt.expiration}")
    private Long expirationTime;

    public String generateToken(User user){
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .subject(user.getId().toString()) // sub do JWT, eh um UUID do user, subjetct retorna String, por isso ToString
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(getSigningKey())
                .compact(); // transforma em string
    }

    public boolean validateToken(String token){
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return true;
        } catch (Exception e){
            return false;
        }
    }

    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));
    }

}
