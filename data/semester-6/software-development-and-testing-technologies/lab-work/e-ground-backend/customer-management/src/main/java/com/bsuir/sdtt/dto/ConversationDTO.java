package com.bsuir.sdtt.dto;

import com.bsuir.sdtt.entity.Customer;
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
    private Customer firstUser;
    private Customer secondUser;
    private String name;
    private List<MessageDTO> conversationMessages;
}
