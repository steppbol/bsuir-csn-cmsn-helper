package com.bsuir.sdtt.client;

import com.bsuir.sdtt.dto.favourite.OrderDTO;
import com.bsuir.sdtt.util.FavouriteItemClientManagementProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Class of Favourite Item Management Client.
 *
 * @author Stsiapan Balashenka
 * @version 1.0
 */
@Component
@Slf4j
public class FavouriteItemManagementClient {
    private final RestTemplate restTemplate;

    @Value("${favourite-item-management.url}")
    private String baseUrl;

    @Autowired
    public FavouriteItemManagementClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public OrderDTO save(OrderDTO orderDTO) {
        log.info("Start method save InventoryClient.Order DTO: {}", orderDTO);

        StringBuilder finalUrl = new StringBuilder(baseUrl);
        finalUrl.append(FavouriteItemClientManagementProperty.API_V1_INVENTORY_ORDERS);

        log.info("Final URL: {}", finalUrl.toString());

        return getResponseEntityOrder(finalUrl, HttpMethod.POST, orderDTO).getBody();
    }

    public List<OrderDTO> getOrdersDTO(UUID id) {
        log.info("Start method InventoryClient.getOrdersDTO ID: {}", id);

        StringBuilder finalUrl = new StringBuilder(baseUrl);
        finalUrl.append(FavouriteItemClientManagementProperty
                .API_V1_INVENTORY_ORDERS_CUSTOMER_ID);
        finalUrl.append(id);

        log.info("Final URL: {}", finalUrl.toString());

        ResponseEntity<OrderDTO[]> responseEntity = restTemplate
                .exchange(finalUrl.toString(), HttpMethod.GET,
                        getHttpEntityHeader(""), OrderDTO[].class);

        log.info("Size Orders DTO: {}", Objects
                .requireNonNull(responseEntity.getBody()).length);

        return Arrays.asList(responseEntity.getBody());
    }

    private <T> HttpEntity<T> getHttpEntityHeader(T template) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return new HttpEntity<>(template, headers);
    }

    private <U> ResponseEntity<OrderDTO> getResponseEntityOrder(StringBuilder finalUrl,
                                                                HttpMethod httpMethod, U template) {
        return restTemplate.exchange(finalUrl.toString(),
                httpMethod, getHttpEntityHeader(template), OrderDTO.class);
    }
}
