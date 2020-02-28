package com.bsuir.sdtt.controller;

import com.bsuir.sdtt.dto.ConversationDTO;
import com.bsuir.sdtt.mapper.ConversationMapper;
import com.bsuir.sdtt.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value = "api/v1/customer-management")
public class ConversationController {

    private ConversationService conversationService;

    private ConversationMapper conversationMapper;

    @Autowired
    public ConversationController(ConversationService conversationService,
                                  ConversationMapper conversationMapper) {
        this.conversationService = conversationService;
        this.conversationMapper = conversationMapper;
    }


    @GetMapping("/conversations")
    public ConversationDTO getConversationInfo(@RequestParam(name = "id") UUID id,
                                               @RequestParam(name = "otherId") UUID otherId) {
        return conversationMapper.conversationToConversationDTO(
                conversationService.getConversationByUsersIds(id, otherId).orElseThrow(
                        () -> new EntityNotFoundException("Conversation not found for users: " + id + "and " + otherId)
                ));
    }

    @GetMapping("/conversations/users/{userId}")
    public List<ConversationDTO> getConversationsByUserId(@PathVariable(name = "userId") UUID userId) {
        return conversationMapper
                .conversationDTOtoConversation(
                        conversationService.getUserConversationsByUserId(userId)
                );
    }
}