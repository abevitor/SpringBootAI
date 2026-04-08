package com.example.SpringBootAI.security;

import java.security.Key;
import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwUtil {

    private static final String SECRET = "chave-super-chave-min-32-bytes";

    private static final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public static String generateToken(String username){
        return Jwts.builder()
                 .subject(username)
                 .issuedAt(new Date())
                 .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                 .signWith(key)
                 .compact();
                 
    }

    public static String extractUsername(String token){
        return Jwts.parser()
                  .verifyWith((SecretKey) key)
                  .build()
                  .parseSignedClaims(token)
                  .getPayload()
                  .getSubject();
    }
    
}
