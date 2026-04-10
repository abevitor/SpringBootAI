package com.example.SpringBootAI.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.SpringBootAI.dto.AIResponse;
import com.example.SpringBootAI.model.PromptLog;
import com.example.SpringBootAI.repository.PromptRepository;
import com.example.SpringBootAI.service.OllamaService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final PromptRepository repository;
    private final OllamaService service; 

    public AIController(OllamaService service, PromptRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @PostMapping("/generate")
    public AIResponse generate(@RequestBody String prompt, HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        return service.generate(prompt, username);

    }

    @PostMapping(value = " /generate/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamGenerate(@RequestBody String prompt, HttpServletRequest request){
        String username = (String) request.getAttribute("username");
        return service.streamGenerate(prompt, username);
        
    }

    @GetMapping("/history")
    public List<PromptLog> history(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        return repository.findByUsernameOrderByCreatedAtDesc(username);
    }
}