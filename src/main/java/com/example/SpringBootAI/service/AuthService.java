package com.example.SpringBootAI.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.SpringBootAI.model.User;
import com.example.SpringBootAI.repository.UserRepository;
import com.example.SpringBootAI.security.JwUtil;


@Service
public class AuthService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder encoder;

    public String register(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        repository.save(user);
        return "Usuário criado!";
    }

    public String login(String username, String password) {
        User user = repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!encoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Senha inválida");
        }

        return JwUtil.generateToken(username);
    }
}