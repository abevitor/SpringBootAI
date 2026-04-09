package com.example.SpringBootAI.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.SpringBootAI.model.PromptLog;
import com.example.SpringBootAI.repository.PromptRepository;
import com.example.SpringBootAI.service.OllamaService;

import jakarta.servlet.http.HttpServletRequest;





@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final PromptRepository repository; 
    @Autowired
    private final OllamaService service;

     public AIController(OllamaService service, PromptRepository repository) {
        this.service = service;
        this.repository = repository;
    }

   

    @PostMapping("/generate")
    public String generate(@RequestBody String prompt, HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        return service.generate(prompt, username);

    }

    @GetMapping("/history")
    public List<PromptLog> history(HttpServletRequest request){

        String username = (String) request.getAttribute("username");

        return repository.findByUsername(username);
    }
}