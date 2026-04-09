package com.example.SpringBootAI.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey key;

   public JwtUtil(@Value("${jwt.secret}") String secret) { 
        if (secret == null || secret.isEmpty()) {
            throw new IllegalStateException("jwt.secret não está definido!");
        }
        byte[] decodedKey = Base64.getDecoder().decode(secret);
        this.key = Keys.hmacShaKeyFor(decodedKey);
    }


    // Gera token JWT
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)              
                .issuedAt(new Date())           
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) 
                .signWith(key)                 
                .compact();
    }

    // Extrai username do token
    public String extractUsername(String token) {
        try {
            Claims claims = Jwts.parser()             
                    .verifyWith(key)                 
                    .build()
                    .parseSignedClaims(token)           
                    .getPayload();                      
            return claims.getSubject();
        } catch (JwtException e) {
            return null;
        }
    }

    // Verifica se token é válido
    public boolean isTokenValid(String token, String username) {
        String tokenUsername = extractUsername(token);
        return tokenUsername != null && tokenUsername.equals(username);
    }
}