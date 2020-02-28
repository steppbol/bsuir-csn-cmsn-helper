package com.bsuir.sdtt.mapper;

import com.bsuir.sdtt.dto.MessageDTO;
import com.bsuir.sdtt.entity.Message;
import com.bsuir.sdtt.repository.CustomerRepository;
import com.bsuir.sdtt.service.ConversationService;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component
@Mapper(componentModel = "spring")
public class MessageMapper {

    private CustomerRepository customerRepository;

    private ConversationService conversationService;

    @Autowired
    public MessageMapper(CustomerRepository customerRepository,
                         ConversationService conversationService) {
        this.customerRepository = customerRepository;
        this.conversationService = conversationService;
    }

    public Message messageDTOtoMessage(MessageDTO messageDTO) {
        Message m = new Message();
        return Message.builder()
                .id(UUID.randomUUID())
                .body(messageDTO.getMessage())
                .receiver(customerRepository.findById(
                        messageDTO.getReceiverId()).orElseThrow(
                        () -> new EntityNotFoundException("Account with UUID: " + messageDTO.getReceiverId() + " not found")))
                .sender(customerRepository.findById(
                        messageDTO.getSenderId()).orElseThrow(
                        () -> new EntityNotFoundException("Account with UUID: " + messageDTO.getReceiverId() + " not found")))
                .conversation(conversationService.findConversationById(
                        messageDTO.getConversationId()).orElseThrow(
                        () -> new EntityNotFoundException("Conversation with UUID: " + messageDTO.getReceiverId() + " not found")))
                .creationDate(messageDTO.getCreationDate())
                .build();
    }

    public MessageDTO messageToMessageDTO(Message message) {
        return MessageDTO.builder()
                .conversationId(message.getConversation().getId())
                .creationDate(message.getCreationDate())
                .message(message.getBody())
                .receiverId(message.getReceiver().getId())
                .senderId(message.getSender().getId())
                .build();
    }

    public List<MessageDTO> messageToMessageDTO(Collection<Message> messages) {
        List<MessageDTO> messagesDTO = new ArrayList<>();
        messages.forEach(m -> messagesDTO.add(messageToMessageDTO(m)));
        return messagesDTO;
    }
}