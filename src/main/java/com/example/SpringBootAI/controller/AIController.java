package com.example.SpringBootAI.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.SpringBootAI.service.OllamaService;





@RestController
@RequestMapping("/api/ai")
public class AIController {
    
    private final OllamaService service;

    public AIController(OllamaService service) {
        this.service = service;
    }

    @PostMapping("/generate")
    public String generate(@RequestBody String prompt) {
        return service.generate(prompt);
    }
}