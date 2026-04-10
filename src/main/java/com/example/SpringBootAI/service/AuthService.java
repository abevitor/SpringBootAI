package com.example.SpringBootAI.service;

import com.example.SpringBootAI.exception.BadRequestException;
import com.example.SpringBootAI.model.User;
import com.example.SpringBootAI.repository.UserRepository;
import com.example.SpringBootAI.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil; 

    public AuthService(UserRepository repository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String register(User user) {
        // 
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new BadRequestException("Username é obrigatório");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new BadRequestException("Password é obrigatório");
        }
        // 
        if (repository.findByUsername(user.getUsername()).isPresent()) {
            throw new BadRequestException("Username já está em uso");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        repository.save(user);
        return jwtUtil.generateToken(user.getUsername());
    }

    public String login(String username, String password) {
        Optional<User> userOpt = repository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado");
        }
        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Senha inválida");
        }
        return jwtUtil.generateToken(username); 
    }
}