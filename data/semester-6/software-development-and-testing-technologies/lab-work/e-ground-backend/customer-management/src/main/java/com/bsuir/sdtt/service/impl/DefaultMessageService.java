package com.bsuir.sdtt.service.impl;

import com.bsuir.sdtt.entity.Message;
import com.bsuir.sdtt.repository.MessageRepository;
import com.bsuir.sdtt.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DefaultMessageService implements MessageService {

    private MessageRepository messageRepository;

    @Autowired
    public DefaultMessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public List<Message> getConversationMessagesById(UUID conversationId) {
        return messageRepository.getConversationMessagesById(conversationId);
    }

    @Override
    public Optional<Message> getConversationLastMessageById(UUID conversationId) {
        return messageRepository.getConversationLastMessageById(conversationId);
    }

    @Override
    public void addMessage(Message message) {
        messageRepository.save(message);
    }
}