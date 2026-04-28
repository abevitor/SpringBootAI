package com.example.SpringBootAI.repository;

import java.util.List;
import java.util.Map;

import com.example.SpringBootAI.model.PromptLog;
import org.springframework.data.domain.Page;        
import org.springframework.data.domain.Pageable;    
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PromptRepository extends JpaRepository<PromptLog, Long> {

    List<PromptLog> findByUsernameOrderByCreatedAtDesc(String username);

    Page<PromptLog> findByUsernameOrderByCreatedAtDesc(String username, Pageable pageable);

   
    List<PromptLog> findByConversationIdOrderByCreatedAtAsc(Long conversationId);

    @Query("SELECT p FROM PromptLog p WHERE p.username = :username " +
           "AND (LOWER(p.prompt) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(p.response) LIKE LOWER(CONCAT('%', :q, '%'))) " +
           "ORDER BY p.createdAt DESC")
    List<PromptLog> search(@Param("username") String username, @Param("q") String q);

    @Modifying
    @Query("DELETE FROM PromptLog p WHERE p.username = :username")
    void deleteAllByUsername(@Param("username") String username);

   
    @Modifying
    @Query("DELETE FROM PromptLog p WHERE p.conversation.id = :conversationId")
    void deleteByConversationId(@Param("conversationId") Long conversationId);

    long countByUsername(String username);

    @Query("SELECT CAST(p.createdAt AS date) as date, COUNT(p) as total " +
           "FROM PromptLog p WHERE p.username = :username " +
           "GROUP BY CAST(p.createdAt AS date) ORDER BY CAST(p.createdAt AS date) DESC")
    List<Map<String, Object>> countByDay(@Param("username") String username);

    @Query("SELECT p FROM PromptLog p WHERE p.username = :username ORDER BY LENGTH(p.prompt) DESC LIMIT 1")
    PromptLog findLongestPromptLog(@Param("username") String username);
}