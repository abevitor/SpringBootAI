package com.example.SpringBootAI.controller;

import com.example.SpringBootAI.model.Conversation;
import com.example.SpringBootAI.model.PromptLog;
import com.example.SpringBootAI.service.ConversationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @PostMapping
    public Conversation create(HttpServletRequest request,
            @RequestBody(required = false) Map<String, String> body) {
        String username = (String) request.getAttribute("username");
        String title = body != null ? body.get("title") : null;
        return conversationService.create(username, title);
    }

    @GetMapping
    public List<Conversation> listAll(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        return conversationService.listAll(username);
    }

    @GetMapping("/{id}")
    public List<PromptLog> getMessages(HttpServletRequest request, @PathVariable Long id) {
        String username = (String) request.getAttribute("username");
        return conversationService.getMessages(id, username);
    }

    @PostMapping("/{id}/message")
    public PromptLog sendMessage(HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody String prompt) {
        String username = (String) request.getAttribute("username");
        return conversationService.sendMessage(id, username, prompt);
    }

    @DeleteMapping("/{id}")
    public String delete(HttpServletRequest request, @PathVariable Long id) {
        String username = (String) request.getAttribute("username");
        conversationService.delete(id, username);
        return "Conversa deletada com sucesso";
    }

}
