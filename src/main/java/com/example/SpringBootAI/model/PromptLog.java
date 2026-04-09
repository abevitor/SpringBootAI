package com.example.SpringBootAI.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class PromptLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   
    private String prompt;


    @Column(columnDefinition = "TEXT")
    private String response;
    
    private String username;

    

    

  
     public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

     public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

     public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

      public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
  
}
