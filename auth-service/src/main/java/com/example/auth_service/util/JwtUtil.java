package com.example.auth_service.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "123456";
    private static final long TOKEN_VALIDITY = 60 * 60 * 5; // 5 hours

    private final Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

    public String generateToken(String email) {

        return JWT.create()
                .withSubject(email)
                .withIssuedAt(Date.from(Instant.now()))
                .withExpiresAt(Date.from(Instant.now().plusSeconds(TOKEN_VALIDITY)))
                .sign(algorithm);
    }

    public String extractEmail(String token) {

        return JWT.require(algorithm)
                .build()
                .verify(token)
                .getSubject();
    }

    public boolean validateToken(String token, String email) {
        try {
            return extractEmail(token).equals(email);
        } catch (JWTVerificationException e) {
            return false;
        }
    }
}
