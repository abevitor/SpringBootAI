package com.example.SpringBootAI.repository;

import com.example.SpringBootAI.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    
    List<Conversation> findByUsernameOrderByCreatedAtDesc(String username);

   
    Optional<Conversation> findByIdAndUsername(Long id, String username);
}
