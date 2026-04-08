package com.example.SpringBootAI.security;

import javax.crypto.SecretKey;
import io.jsonwebtoken.Jwts;

public class JwtUtil {

    private static final SecretKey key = Jwts.SIG.HS256.key().build();

    public static String generateToken(String username){
        return Jwts.builder()
                .subject(username)
                .issuedAt(new java.util.Date())
                .expiration(new java.util.Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(key)
                .compact();
    }

    public static String extractUsername(String token){
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}