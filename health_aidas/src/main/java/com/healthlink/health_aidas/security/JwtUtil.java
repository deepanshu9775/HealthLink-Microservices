package com.healthlink.health_aidas.security;

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
    private final long EXPIRATION_TIME = 1000 * 60 * 60; // Aapki value use ki hai

    // 2. Secret String ko Key object mein initialize karna
    @PostConstruct
    public void init() {
        // Same logic jaisa Login Project mein use hua tha
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }
    
    // 🔹 Extract username
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // 🔹 Validate token
    public boolean validateToken(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
             // System.out.println("JWT Validation Failed: " + e.getMessage()); // Debugging ke liye
            return false;
        }
    }

    // 🔹 Extract all claims
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // Static key use hogi validation ke liye
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}