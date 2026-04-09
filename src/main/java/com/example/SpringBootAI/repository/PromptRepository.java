package com.example.SpringBootAI.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.SpringBootAI.model.PromptLog;

public interface PromptRepository extends JpaRepository<PromptLog, Long>{

    List<PromptLog> findByUsername(String username);
    
}
