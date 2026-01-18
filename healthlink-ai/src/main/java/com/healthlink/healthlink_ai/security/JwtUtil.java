package com.healthlink.healthlink_ai.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value; // Import for @Value
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct; // Import for @PostConstruct

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // 1. Secret Key ko properties file se load karenge
    // Dhyan rakho ki yeh KEY dono projects mein same ho!
    @Value("${jwt.secret}")
    private String secret;

    private Key key; // Key object jo signing aur parsing ke liye use hoga

    // token validity (1 hour)
    private final long EXPIRATION_TIME = 1000 * 60 * 60;

    // 2. Secret String ko Key object mein initialize karna
    @PostConstruct
    public void init() {
        // Ensure that the secret key is long enough for HS256 (256 bits or 32 characters base64 encoded)
        // Agar aap base64 encoded secret use kar rahe hain, toh Jwts.SIG.HS256.key().build() use kar sakte hain
        // Simple string ke liye, bytes mein convert karke use karein.
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // 🔹 Generate Token
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                // SignWith mein ab woh static key use hogi
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 🔹 Extract username
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // 🔹 Validate token
    public boolean isTokenValid(String token) {
        try {
            // Agar token invalid hoga (expired, signature mismatch), toh exception throw hoga
            extractClaims(token);
            return true;
        } catch (Exception e) {
            System.out.println("JWT Validation Failed: " + e.getMessage()); // Logging
            return false;
        }
    }

    // 🔹 Extract all claims
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // Same static key use hogi validation ke liye
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}