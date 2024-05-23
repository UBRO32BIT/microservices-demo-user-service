package org.example.userservice.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.example.userservice.dto.security.JwtProperties;
import org.example.userservice.model.User;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final JwtProperties jwtProperties;

    public Jws<Claims> parseJwt(String jwtString) {
        String secret = jwtProperties.getSecretKey();
        Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(secret), SignatureAlgorithm.HS256.getJcaName());

        Jws<Claims> jwt = Jwts.parser()
                .setSigningKey(hmacKey)
                .build()
                .parseClaimsJws(jwtString);

        return jwt;
    }

    public String extractUsername(String jwtString) {
        Jws<Claims> jwt = parseJwt(jwtString);
        return jwt.getPayload().getSubject(); // Assuming the subject contains the username
    }

    public String generateToken(User user) {
        final String username = user.getUsername();
        final String secret = jwtProperties.getSecretKey();

        Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(secret), SignatureAlgorithm.HS256.getJcaName());
        Instant now = Instant.now();

        return Jwts.builder()
                .setSubject(username)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(jwtProperties.getExpirationSecond(), ChronoUnit.SECONDS)))
                .signWith(hmacKey)
                .compact();
    }

    public Boolean validateToken(String token, String authenticatedUsername) {
        final String username = extractUsername(token);
        return username.equals(authenticatedUsername);
    }
}