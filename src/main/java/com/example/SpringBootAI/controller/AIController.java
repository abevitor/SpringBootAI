package com.example.SpringBootAI.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.SpringBootAI.dto.AIResponse;
import com.example.SpringBootAI.dto.StatsResponse;
import com.example.SpringBootAI.model.PromptLog;
import com.example.SpringBootAI.repository.PromptRepository;
import com.example.SpringBootAI.service.ExportService;
import com.example.SpringBootAI.service.OllamaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final PromptRepository repository;
    private final OllamaService service; 
    private final ExportService exportService;

    public AIController(OllamaService service, PromptRepository repository, ExportService exportService) {
        this.service = service;
        this.repository = repository;
        this.exportService = exportService;
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

    @GetMapping("/export")
    public void exportCsv(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = (String) request.getAttribute("username");
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"historico_" + username + ".csv\"");

        exportService.exportCsv(username, response.getWriter());

    }

    @GetMapping("/stats")
    public StatsResponse stats(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        return exportService.getStats(username);
    }
}