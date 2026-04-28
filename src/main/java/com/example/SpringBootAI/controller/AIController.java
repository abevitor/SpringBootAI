package com.example.SpringBootAI.controller;

import com.example.SpringBootAI.dto.AIResponse;
import com.example.SpringBootAI.dto.StatsResponse;
import com.example.SpringBootAI.exception.NotFoundException;
import com.example.SpringBootAI.model.PromptLog;
import com.example.SpringBootAI.repository.PromptRepository;
import com.example.SpringBootAI.service.ExportService;
import com.example.SpringBootAI.service.OllamaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;


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

    @PostMapping(value = "/generate/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamGenerate(@RequestBody String prompt, HttpServletRequest request){
        String username = (String) request.getAttribute("username");
        return service.streamGenerate(prompt, username);
        
    }

    @GetMapping("/history")
    public Page<PromptLog> history(HttpServletRequest request,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "20") int size) {
                            String username = (String) request.getAttribute("username");
                            Pageable pageable = PageRequest.of(page,size);
                            return repository.findByUsernameOrderByCreatedAtDesc(username, pageable);
                                   }

    @GetMapping("/history/search")
    public List<PromptLog> search(HttpServletRequest request,
                                  @RequestParam String q) {
        String username = (String) request.getAttribute("username");

        if (q == null || q.isBlank()) {
            throw new NotFoundException("Informe o termo de busca");
        }

        return repository.search(username, q);
    }

    @DeleteMapping("/history/{id}")
    public String deleteOne(HttpServletRequest request, @PathVariable Long id) {
        String username = (String) request.getAttribute("username");

        PromptLog log = repository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Prompt nao encontrado"));

                        if(!log.getUsername().equals(username)) {
                            throw new NotFoundException("Prompt nao encontrado");
                        }

                        repository.delete(log);
                        return "Prompt deletado com sucesso";

    }

    @Transactional
    @DeleteMapping("/history")
    public String deleteAll(HttpServletRequest request){
        String username = (String) request.getAttribute("username");
        repository.deleteAllByUsername(username);
        return "Histórico deletado com sucesso";
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