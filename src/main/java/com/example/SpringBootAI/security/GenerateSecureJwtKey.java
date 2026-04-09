package com.example.SpringBootAI.security;

import io.jsonwebtoken.Jwts;

import java.util.Base64;
import javax.crypto.SecretKey;

public class GenerateSecureJwtKey {
    public static void main(String[] args) {
     
        SecretKey key = Jwts.SIG.HS256.key().build();

        String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());
        System.out.println("Chave JWT segura (Base64): " + base64Key);
    }
}