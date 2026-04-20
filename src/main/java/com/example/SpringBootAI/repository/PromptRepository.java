package com.example.SpringBootAI.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.SpringBootAI.model.PromptLog;

public interface PromptRepository extends JpaRepository<PromptLog, Long>{

    List<PromptLog> findByUsernameOrderByCreatedAtDesc(String username);
    
    long countByUsername(String username);


    @Query("SELECT CAST(p.createdAt AS date) as date, COUNT(p) as total " +
           "FROM PromptLog p WHERE p.username = :username " +
           "GROUP BY CAST(p.createdAt AS date) ORDER BY CAST(p.createdAt AS date) DESC")
    List<Map<String, Object>> countByDay(@Param("username") String username); 
    
    @Query("SELECT p FROM PromptLog p WHERE p.username = :username ORDER BY LENGTH(p.prompt) DESC LIMIT 1")
    PromptLog findLongestPromptLog(@Param("username") String username);
}
