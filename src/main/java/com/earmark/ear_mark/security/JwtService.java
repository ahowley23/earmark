package com.earmark.ear_mark.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    // In production this moves to application.properties and gets injected
    // with @Value — never hardcode a real secret in source code
    private static final String SECRET = "earmark-secret-key-that-is-long-enough-for-hs256-algorithm";

    // How long an access token lives — 1 hour in milliseconds
    private static final long EXPIRATION_MS = 1000 * 60 * 60;

    // Derives a signing key from the secret string
    // HMAC-SHA256 is the algorithm — it signs and verifies the token
    // so we know nobody tampered with the payload
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    // Builds and signs a token for a given username
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(getSigningKey())
                .compact();
    }

    // Pulls the username out of a token's payload
    // Throws if token is expired or tampered — filter catches this
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Checks expiration by comparing token's expiry date against now
    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    // Valid means not expired AND username matches
    public boolean isTokenValid(String token, String username) {
        return extractUsername(token).equals(username) && !isTokenExpired(token);
    }

    // Does the actual cryptographic verification
    // jjwt checks the signature against our key and throws if anything doesn't match
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
