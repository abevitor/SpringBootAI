package com.example.SpringBootAI.service;

import com.example.SpringBootAI.dto.AIResponse;
import com.example.SpringBootAI.model.PromptLog;
import com.example.SpringBootAI.repository.PromptRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class OllamaService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final PromptRepository repository;

    public OllamaService(PromptRepository repository) {
        this.repository = repository;
    }

    public AIResponse generate(String prompt, String username) {
        String url = "http://localhost:11434/api/generate";

        Map<String, Object> request = new HashMap<>();
        request.put("model", "llama3.2");
        request.put("prompt", prompt);
        request.put("stream", false);

        Map<String, Object> Ollamaresponse = restTemplate.postForObject(url, request, Map.class);

        if (Ollamaresponse == null || Ollamaresponse.get("response") == null) {
            throw new RuntimeException("Erro ao chamar o Ollama");
        }

        String result = Ollamaresponse.get("response").toString();
        LocalDateTime now = LocalDateTime.now();

        
        PromptLog log = new PromptLog();
        log.setPrompt(prompt);
        log.setResponse(result);
        log.setUsername(username);
        repository.save(log);

        return new AIResponse(prompt, result, now);
    }
}