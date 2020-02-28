package com.bsuir.sdtt.controller;

import com.bsuir.sdtt.dto.MessageDTO;
import com.bsuir.sdtt.mapper.MessageMapper;
import com.bsuir.sdtt.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/customer-management/messages")
public class MessageController {

    private MessageService messageService;

    private MessageMapper messageMapper;

    @Autowired
    public MessageController(MessageService messageService,
                             MessageMapper messageMapper) {
        this.messageService = messageService;
        this.messageMapper = messageMapper;
    }

    @GetMapping("/conversations/{id}")
    public List<MessageDTO> getConversationMessages(@PathVariable(name = "id") UUID id) {
        return messageMapper.messageToMessageDTO(messageService.getConversationMessagesById(id));
    }
}