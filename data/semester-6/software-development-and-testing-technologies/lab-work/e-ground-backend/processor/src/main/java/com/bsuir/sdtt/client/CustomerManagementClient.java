package com.bsuir.sdtt.client;

import com.bsuir.sdtt.dto.customer.ConversationDTO;
import com.bsuir.sdtt.dto.customer.CustomerDTO;
import com.bsuir.sdtt.dto.customer.MessageDTO;
import com.bsuir.sdtt.dto.processor.AuthorizationParameterDTO;
import com.bsuir.sdtt.util.CustomerManagementClientProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Class of Customer Management Client.
 *
 * @author Stsiapan Balashenka
 * @version 1.0
 */
@Component
@Slf4j
public class CustomerManagementClient {
    private final RestTemplate restTemplate;

    @Value("${customer-management.url}")
    private String baseUrl;

    @Autowired
    public CustomerManagementClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public CustomerDTO save(CustomerDTO customerDTO) {
        log.info("Start method CustomerManagementClient.save: {}", customerDTO);

        StringBuilder finalUrl = new StringBuilder(baseUrl);
        finalUrl.append(CustomerManagementClientProperty.API_V1_CUSTOMER_MANAGEMENT);

        log.info("Final URL: {}", finalUrl.toString());

        return getResponseEntityCustomer(finalUrl, HttpMethod.POST, customerDTO).getBody();
    }

    public CustomerDTO update(CustomerDTO customerDTO) {
        log.info("Start method CustomerManagementClient.update: {}", customerDTO);

        StringBuilder finalUrl = new StringBuilder(baseUrl);
        finalUrl.append(CustomerManagementClientProperty.API_V1_CUSTOMER_MANAGEMENT);

        log.info("Final URL: {}", finalUrl.toString());

        return getResponseEntityCustomer(finalUrl, HttpMethod.PUT, customerDTO).getBody();
    }

    public CustomerDTO authorizationCustomer(AuthorizationParameterDTO authorizationParameterDTO) {
        log.info("Start method CustomerManagementClient.authorizationCustomer: {}", authorizationParameterDTO);

        StringBuilder finalUrl = new StringBuilder(baseUrl);
        finalUrl.append(CustomerManagementClientProperty.API_V1_CUSTOMER_MANAGEMENT);
        finalUrl.append("authorization");

        log.info("Final URL: {}", finalUrl.toString());

        return getResponseEntityCustomer(finalUrl, HttpMethod.POST, authorizationParameterDTO).getBody();
    }

    public CustomerDTO getCustomerDTO(UUID id) {
        log.info("Start method InventoryClient.getCustomersDto ID: {}", id);

        StringBuilder finalUrl = new StringBuilder(baseUrl);
        finalUrl.append(CustomerManagementClientProperty.API_V1_CUSTOMER_MANAGEMENT);
        finalUrl.append(id);

        log.info("Final URL: {}", finalUrl.toString());

        ResponseEntity<CustomerDTO> responseEntity = getResponseEntityCustomer(finalUrl, HttpMethod.GET, "");

        log.info("Customer DTO: {}", responseEntity.getBody());

        return responseEntity.getBody();
    }

    public List<ConversationDTO> getConversationsByUserId(UUID id) {
        log.info("Start method InventoryClient.getConversationsByUserId: {}", id);

        StringBuilder finalUrl = new StringBuilder(baseUrl);
        finalUrl.append(CustomerManagementClientProperty.API_V1_CONVERSATIONS);
        finalUrl.append("users/");
        finalUrl.append(id);

        log.info("Final URL: {}", finalUrl.toString());

        ResponseEntity<ConversationDTO[]> responseEntity = restTemplate
                .exchange(finalUrl.toString(), HttpMethod.GET,
                        getHttpEntityHeader(""), ConversationDTO[].class);

        return Arrays.asList(responseEntity.getBody());
    }

    public ConversationDTO getConversationInfo(UUID id, UUID otherId) {
        log.info("Start method InventoryClient.getConversationInfo ID: {}", id);

        StringBuilder finalUrl = new StringBuilder(baseUrl);
        finalUrl.append(CustomerManagementClientProperty.API_V1_CONVERSATIONS);
        finalUrl.append("?");
        finalUrl.append("id=");
        finalUrl.append(id);
        finalUrl.append("&otherId=");
        finalUrl.append(otherId);

        log.info("Final URL: {}", finalUrl.toString());

        ResponseEntity<ConversationDTO> responseEntity = restTemplate
                .exchange(finalUrl.toString(), HttpMethod.GET,
                        getHttpEntityHeader(""), ConversationDTO.class);

        return responseEntity.getBody();
    }

    public List<MessageDTO> getConversationMessages(UUID conversationId) {
        log.info("Start method InventoryClient.getConversationMessages: {}", conversationId);

        StringBuilder finalUrl = new StringBuilder(baseUrl);
        finalUrl.append(CustomerManagementClientProperty.API_V1_MESSAGES);
        finalUrl.append("conversations/");
        finalUrl.append(conversationId);

        log.info("Final URL: {}", finalUrl.toString());

        ResponseEntity<MessageDTO[]> responseEntity = restTemplate
                .exchange(finalUrl.toString(), HttpMethod.GET,
                        getHttpEntityHeader(""), MessageDTO[].class);

        return Arrays.asList(responseEntity.getBody());
    }

    private <T> HttpEntity<T> getHttpEntityHeader(T template) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return new HttpEntity<>(template, headers);
    }

    private <U> ResponseEntity<CustomerDTO> getResponseEntityCustomer(StringBuilder finalUrl,
                                                                      HttpMethod httpMethod, U template) {
        return restTemplate.exchange(finalUrl.toString(),
                httpMethod, getHttpEntityHeader(template), CustomerDTO.class);
    }
}
