package com.example.SpringBootAI.service;

import com.example.SpringBootAI.exception.NotFoundException;
import com.example.SpringBootAI.model.Conversation;
import com.example.SpringBootAI.model.PromptLog;
import com.example.SpringBootAI.repository.ConversationRepository;
import com.example.SpringBootAI.repository.PromptRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class ConversationService {
    private final ConversationRepository conversationRepository;
    private final PromptRepository promptRepository;
    private final OllamaService ollamaService;
   
    public ConversationService(ConversationRepository conversationRepository,
                               PromptRepository promptRepository,
                               OllamaService ollamaService) {

                this.conversationRepository = conversationRepository;
                this.promptRepository = promptRepository;
                this.ollamaService = ollamaService;
                               }

    public Conversation create(String username, String title){
        Conversation conversation = new Conversation();
        conversation.setUsername(username);
        conversation.setTitle(title != null && !title.isBlank() ? title : "Nova conversa");
        return conversationRepository.save(conversation);
    }

    public List<Conversation> listAll(String username){
        return conversationRepository.findByUsernameOrderByCreatedAtDesc(username);
    }

    public List<PromptLog> getMessages(Long conversationId, String username){

        conversationRepository.findByIdAndUsername(conversationId, username)
                                .orElseThrow(() -> new NotFoundException("Conversa nao encontrada"));

        return promptRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
    }  
    
    public PromptLog sendMessage(Long conversationId, String username, String prompt) {
        Conversation conversation = conversationRepository.findByIdAndUsername(conversationId, username)
                                    .orElseThrow(() -> new NotFoundException("Conversa nao encontrada"));
            
        List<PromptLog> history = promptRepository
                             .findByConversationIdOrderByCreatedAtAsc(conversationId);
                        


        String promptWithContext = buildContext(history, prompt);

        String result = ollamaService.generateRaw(promptWithContext);


        PromptLog log = new PromptLog();
        log.setPrompt(prompt);
        log.setResponse(result);
        log.setUsername(username);
        log.setConversation(conversation);
        return promptRepository.save(log);

    }

    @Transactional
    public void delete(Long conversationId, String username){
        conversationRepository.findByIdAndUsername(conversationId, username)
                            .orElseThrow(() -> new NotFoundException("Conversa nao encontrada"));

                            promptRepository.deleteByConversationId(conversationId);
                            conversationRepository.deleteById(conversationId);
    }

    private String buildContext(List<PromptLog> history, String newPrompt){
        StringBuilder context = new StringBuilder();

        for(PromptLog log : history) {
             context.append("Usuário: ").append(log.getPrompt()).append("\n");
             context.append("Assistente: ").append(log.getResponse()).append("\n\n");

        }

        context.append("Usuário: ").append(newPrompt).append("\n");
        context.append("Assistente:");
        
        return context.toString();
    }
}
