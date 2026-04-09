package com.example.SpringBootAI.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.SpringBootAI.model.PromptLog;
import com.example.SpringBootAI.repository.PromptRepository;

@Service
public class OllamaService {

    private final RestTemplate RestTemplate = new RestTemplate();

    
@Autowired
    private PromptRepository repository;

    public String generate(String prompt, String username) {

        String url = "http://localhost:11434/api/generate";

        Map<String, Object> request = new HashMap<>();
        request.put("model", "llama3.2");
        request.put("prompt", prompt);
        request.put("stream", false); 
        Map<String, Object> response = RestTemplate.postForObject(url, request, Map.class);

        if (response == null || response.get("response") == null) {
            throw new RuntimeException("Erro ao chamar o Ollama");
        }

        String result = response.get("response").toString();

        // salvar no banco
        PromptLog log = new PromptLog();
        log.setPrompt(prompt);
        log.setResponse(result);
        log.setUsername(username);

        repository.save(log);

        return result;
    }
}