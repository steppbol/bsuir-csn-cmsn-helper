package com.bsuir.sdtt.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDTO {

    private UUID id;
    private CustomerDTO firstCustomer;
    private CustomerDTO secondCustomer;
    private String name;
    private List<MessageDTO> conversationMessages;
}
