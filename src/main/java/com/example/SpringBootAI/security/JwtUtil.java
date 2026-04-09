package com.example.SpringBootAI.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey key;

    public JwtUtil() {
        String secret = System.getenv("JWT_SECRET");
        if (secret == null || secret.isEmpty()) {
            throw new IllegalStateException("JWT_SECRET não está definido!");
        }
        byte[] decodedKey = Base64.getDecoder().decode(secret);
        // SecretKey no lugar de Key (necessário para o novo verifyWith())
        this.key = Keys.hmacShaKeyFor(decodedKey);
    }

    // Gera token JWT
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)              // era .setSubject()
                .issuedAt(new Date())           // era .setIssuedAt()
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // era .setExpiration()
                .signWith(key)                  // algoritmo inferido automaticamente da chave
                .compact();
    }

    // Extrai username do token
    public String extractUsername(String token) {
        try {
            Claims claims = Jwts.parser()               // era Jwts.parserBuilder()
                    .verifyWith(key)                    // era .setSigningKey()
                    .build()
                    .parseSignedClaims(token)           // era .parseClaimsJws()
                    .getPayload();                      // era .getBody()
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