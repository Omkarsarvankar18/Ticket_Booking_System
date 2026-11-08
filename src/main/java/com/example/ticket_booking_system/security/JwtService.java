
package com.example.ticket_booking_system.security;

import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey SECRET_KEY;

    // JWT token expiration time: 24 hours
    private final long EXP_TIME = 1000L * 60 * 60 * 24;

    // Constructor: Read Base64-encoded secret from application.properties
    public JwtService(@Value("${jwt.secret}") String key) {

        byte[] decodedKey = Base64.getDecoder().decode(key);

        SECRET_KEY = Keys.hmacShaKeyFor(decodedKey);
    }

    // Generate JWT Token
    public String generateToken(String username) {

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXP_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    // Extract Username from JWT Token
    public String getUserName(String token) {

        Claims claims = Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    // Validate JWT Token
    public boolean validateToken(String token) {

        try {

            Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token);

            return true;

        } catch (Exception e) {

            System.out.println("JWT Validation Failed: " + e.getMessage());

            return false;
        }
    }
}
