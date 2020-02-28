package com.bsuir.sdtt.service.impl;

import com.bsuir.sdtt.entity.Conversation;
import com.bsuir.sdtt.entity.Customer;
import com.bsuir.sdtt.repository.ConversationRepository;
import com.bsuir.sdtt.repository.CustomerRepository;
import com.bsuir.sdtt.service.ConversationService;
import com.bsuir.sdtt.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DefaultConversationService implements ConversationService {

    private ConversationRepository conversationRepository;

    private CustomerRepository customerRepository;

    @Autowired
    public DefaultConversationService(ConversationRepository conversationRepository,
                                      CustomerRepository customerRepository) {
        this.conversationRepository = conversationRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public List<Conversation> getUserConversationsByUserId(UUID userId) {
        List<Conversation> conversations = conversationRepository.getUserConversationsByUserId(userId);
        conversations.forEach(conversation -> swapAccounts(conversation, userId));

        return conversations;
    }

    @Override
    public Optional<Conversation> getConversationByUsersIds(UUID yourId, UUID otherId) {
        Optional<Conversation> conversationOptional = conversationRepository.getConversationByUsersIds(yourId, otherId);
        if (!conversationOptional.isPresent()) {
            this.addConversation(yourId, otherId);
            return conversationRepository.getConversationByUsersIds(yourId, otherId);
        }
        swapAccounts(conversationOptional.get(), yourId);

        return conversationOptional;
    }

    @Override
    public void addConversation(UUID yourId, UUID otherId) {
        Conversation conversation = Conversation.builder()
                .firstAccount(customerRepository.findById(yourId).orElseThrow(
                        () -> new EntityNotFoundException("Account with ID: " + yourId + "not found.")))
                .secondAccount(customerRepository.findById(otherId).orElseThrow(
                        () -> new EntityNotFoundException("Account with ID: " + otherId + "not found.")))
                .messages(new ArrayList<>())
                .build();
        conversationRepository.save(conversation);
    }

    @Override
    public Optional<Conversation> findConversationById(UUID conversationId) {
        return conversationRepository.findConversationById(conversationId);
    }

    private void swapAccounts(Conversation conversation, UUID yourId) {
        if (conversation.getFirstAccount().getId().compareTo(yourId) != 0) {
            Customer customer = conversation.getFirstAccount();
            conversation.setFirstAccount(conversation.getSecondAccount());
            conversation.setSecondAccount(customer);
        }
    }
}