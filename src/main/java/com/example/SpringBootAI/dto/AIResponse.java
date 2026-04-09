package com.example.SpringBootAI.dto;

import java.time.LocalDateTime;

public class AIResponse {

    private String prompt;
    private String response;
    private LocalDateTime date;

    public AIResponse(String prompt, String response, LocalDateTime date){
        this.prompt = prompt;
        this.response = response;
        this.date = date;
    }

    public String getPrompt(){ return prompt; }
    public String getResponse(){ return response; }
    public LocalDateTime getDate(){return date; }
    
}
