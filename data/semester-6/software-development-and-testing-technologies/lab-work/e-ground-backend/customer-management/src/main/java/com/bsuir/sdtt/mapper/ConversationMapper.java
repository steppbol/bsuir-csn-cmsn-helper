package com.bsuir.sdtt.mapper;

import com.bsuir.sdtt.dto.ConversationDTO;
import com.bsuir.sdtt.entity.Conversation;
import com.bsuir.sdtt.repository.CustomerRepository;
import com.bsuir.sdtt.service.MessageService;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
@Mapper(componentModel = "spring")
public class ConversationMapper {

    private CustomerRepository customerRepository;
    private MessageService messageService;
    private MessageMapper messageMapper;

    @Autowired
    public ConversationMapper(CustomerRepository customerRepository,
                              MessageService messageService,
                              MessageMapper messageMapper) {
        this.customerRepository = customerRepository;
        this.messageService = messageService;
        this.messageMapper = messageMapper;
    }

    public ConversationDTO conversationToConversationDTO(Conversation conversation) {
        conversation.getMessages().sort(Collections.reverseOrder());
        return ConversationDTO.builder()
                .id(conversation.getId())
                .firstUser(conversation.getFirstAccount())
                .secondUser(conversation.getSecondAccount())
                .conversationMessages(messageMapper.messageToMessageDTO(conversation.getMessages()))
                .build();
    }

    public Conversation conversationDTOtoConversation(ConversationDTO conversationDTO) {
        return Conversation.builder()
                .id(conversationDTO.getId())
                .firstAccount(customerRepository.findById(conversationDTO.getFirstUser().getId()).orElseThrow(
                        () -> new EntityNotFoundException("Account with UUID" + conversationDTO.getFirstUser() + "not found")))
                .secondAccount(customerRepository.findById(conversationDTO.getSecondUser().getId()).orElseThrow(
                        () -> new EntityNotFoundException("Account with UUID" + conversationDTO.getSecondUser() + "not found")))
                .messages(messageService.getConversationMessagesById(conversationDTO.getId()))
                .build();
    }

    public List<ConversationDTO> conversationDTOtoConversation(Collection<Conversation> conversations) {
        List<ConversationDTO> conversationsDTO = new ArrayList<>();
        conversations.forEach((c) ->
                conversationsDTO.add(conversationToConversationDTO(c)));
        return conversationsDTO;
    }
}
