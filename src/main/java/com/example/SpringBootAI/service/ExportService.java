package com.example.SpringBootAI.service;

import com.example.SpringBootAI.dto.StatsResponse;
import com.example.SpringBootAI.model.PromptLog;
import com.example.SpringBootAI.repository.PromptRepository;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.util.List;

@Service
public class ExportService {

    private final PromptRepository repository;

    public ExportService(PromptRepository repository) {
        this.repository = repository;
    }

    
    public void exportCsv(String username, PrintWriter writer) {
        List<PromptLog> history = repository.findByUsernameOrderByCreatedAtDesc(username);

       
        writer.println("id,prompt,response,date");

        for (PromptLog log : history) {
            writer.printf("%d,\"%s\",\"%s\",\"%s\"%n",
                log.getId(),
                escapeCsv(log.getPrompt()),
                escapeCsv(log.getResponse()),
                log.getCreatedAt()
            );
        }

        writer.flush();
    }

    
    public StatsResponse getStats(String username) {
        long total = repository.countByUsername(username);

        var usageByDay = repository.countByDay(username);

        PromptLog longest = repository.findLongestPromptLog(username);
        String longestPrompt = longest != null ? longest.getPrompt() : "Nenhum prompt ainda";

        return new StatsResponse(username, total, longestPrompt, usageByDay);
    }

   
    private String escapeCsv(String value) {
        if (value == null) return "";
        return value.replace("\"", "\"\"");
    }
}
