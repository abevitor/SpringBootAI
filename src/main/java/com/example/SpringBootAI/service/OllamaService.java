package com.example.SpringBootAI.service;

import com.example.SpringBootAI.dto.AIResponse;
import com.example.SpringBootAI.model.PromptLog;
import com.example.SpringBootAI.repository.PromptRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class OllamaService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final PromptRepository repository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OllamaService(PromptRepository repository) {
        this.repository = repository;
    }

    private String buildPromptWithHistory(String username, String newPrompt) {
        List<PromptLog> history = repository.findByUsernameOrderByCreatedAtDesc(username);

        StringBuilder context = new StringBuilder();

        int limit = Math.min(history.size(), 10);
        List<PromptLog> recent = history.subList(0, limit);

        for (int i = recent.size() - 1; i >= 0; i--) {
            PromptLog log = recent.get(i);
            context.append("Usuário: ").append(log.getPrompt()).append("\n");
            context.append("Assistente: ").append(log.getResponse()).append("\n");
        }

        context.append("Usuário: ").append(newPrompt).append("\n");
        context.append("Assistente: ");

        return context.toString();
    }

    public AIResponse generate(String prompt, String username) {
        String url = "http://localhost:11434/api/generate";

        String promptWithHistory = buildPromptWithHistory(username, prompt);

        Map<String, Object> request = new HashMap<>();
        request.put("model", "llama3.2");
        request.put("prompt", promptWithHistory);
        request.put("stream", false);

        Map<String, Object> ollamaResponse = restTemplate.postForObject(url, request, Map.class);

        if (ollamaResponse == null || ollamaResponse.get("response") == null) {
            throw new RuntimeException("Erro ao se comunicar com o Ollama");
        }

        String result = ollamaResponse.get("response").toString();
        LocalDateTime now = LocalDateTime.now();

        PromptLog log = new PromptLog();
        log.setPrompt(prompt);
        log.setResponse(result);
        log.setUsername(username);
        repository.save(log);

        return new AIResponse(prompt, result, now);
    }

    public String generateRaw(String promptWithContext) {
        String url = "http://localhost:11434/api/generate";

        Map<String, Object> request = new HashMap<>();
        request.put("model", "llama3.2");
        request.put("prompt", promptWithContext);
        request.put("stream", false);

        Map<String, Object> ollamaResponse = restTemplate.postForObject(url, request, Map.class);

        if (ollamaResponse == null || ollamaResponse.get("response") == null) {
            throw new RuntimeException("Erro ao se comunicar com o Ollama");
        }

        return ollamaResponse.get("response").toString();
    }

    public SseEmitter streamGenerate(String prompt, String username) {
        SseEmitter emitter = new SseEmitter(300_000L);

        String promptWithHistory = buildPromptWithHistory(username, prompt);

        CompletableFuture.runAsync(() -> {
            StringBuilder fullResponse = new StringBuilder();

            try {
                String url = "http://localhost:11434/api/generate";

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("model", "llama3.2");
                requestBody.put("prompt", promptWithHistory);
                requestBody.put("stream", true);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                restTemplate.execute(url, HttpMethod.POST,
                        req -> {
                            req.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                            objectMapper.writeValue(req.getBody(), requestBody);
                        },
                        res -> {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(res.getBody()));
                            String line;

                            while ((line = reader.readLine()) != null) {
                                if (line.isEmpty())
                                    continue;

                                Map<String, Object> chunk = objectMapper.readValue(line, Map.class);
                                String token = (String) chunk.get("response");
                                boolean done = Boolean.TRUE.equals(chunk.get("done"));

                                if (token != null && !token.isEmpty()) {
                                    fullResponse.append(token);
                                    emitter.send(token);
                                }

                                if (done)
                                    break;
                            }
                            return null;
                        });

                PromptLog log = new PromptLog();
                log.setPrompt(prompt);
                log.setResponse(fullResponse.toString());
                log.setUsername(username);
                repository.save(log);

                emitter.complete();

            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }
}