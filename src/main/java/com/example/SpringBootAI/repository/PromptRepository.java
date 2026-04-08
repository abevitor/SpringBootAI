package com.example.SpringBootAI.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.SpringBootAI.model.PromptLog;

public interface PromptRepository extends JpaRepository<PromptLog, Long>{
    
}
