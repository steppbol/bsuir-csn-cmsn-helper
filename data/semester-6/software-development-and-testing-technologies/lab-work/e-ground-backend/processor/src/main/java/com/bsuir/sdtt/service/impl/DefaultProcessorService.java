package com.bsuir.sdtt.service.impl;

import com.bsuir.sdtt.client.CatalogClient;
import com.bsuir.sdtt.client.CustomerManagementClient;
import com.bsuir.sdtt.client.FavouriteItemManagementClient;
import com.bsuir.sdtt.dto.catalog.CategoryDTO;
import com.bsuir.sdtt.dto.catalog.CommentDTO;
import com.bsuir.sdtt.dto.catalog.OfferDTO;
import com.bsuir.sdtt.dto.customer.ConversationDTO;
import com.bsuir.sdtt.dto.customer.CustomerDTO;
import com.bsuir.sdtt.dto.customer.MessageDTO;
import com.bsuir.sdtt.dto.favourite.OrderDTO;
import com.bsuir.sdtt.dto.processor.*;
import com.bsuir.sdtt.service.ProcessorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Class of processor service.
 *
 * @author Stsiapan Balashenka
 * @version 1.0
 */
@Service
@Slf4j
public class DefaultProcessorService implements ProcessorService {
    private final CatalogClient catalogClient;

    private final FavouriteItemManagementClient favouriteItemManagementClient;

    private final CustomerManagementClient customerManagementClient;

    @Autowired
    public DefaultProcessorService(CatalogClient catalogClient,
                                   FavouriteItemManagementClient favouriteItemManagement,
                                   CustomerManagementClient customerManagementClient) {
        this.catalogClient = catalogClient;
        this.favouriteItemManagementClient = favouriteItemManagement;
        this.customerManagementClient = customerManagementClient;
    }

    @Override
    public OrderDTO addToFavorite(CreateOrderParameterDTO createOrderParameter) {
        OrderDTO orderDto = new OrderDTO();

        UUID customerId = createOrderParameter.getCustomerId();
        UUID itemId = createOrderParameter.getItemId();

        CustomerDTO customerDto = customerManagementClient.getCustomerDTO(customerId);
        OfferDTO offerDto = catalogClient.getOfferDTO(itemId);

        log.info("Start method DefaultProcessorService.createOrder customerId = {}",
                customerId);

        OrderDTO savedOrderDto = null;
        if (customerId.equals(customerDto.getId())) {
            orderDto.setCustomerId(customerId);
            orderDto.setName(customerDto.getFirstName());
            orderDto.setEmail(customerDto.getEmail());
            orderDto.setTotalPrice(offerDto.getPrice() * createOrderParameter
                    .getItemCount());
            orderDto.setOrderItemCount(createOrderParameter.getItemCount());
            orderDto.setImage(offerDto.getImage());
            orderDto.setImageId(offerDto.getImageId());
            orderDto.setCompressedImageId(offerDto.getCompressedImageId());
            savedOrderDto = favouriteItemManagementClient.save(orderDto);
        }

        return savedOrderDto;
    }

    @Override
    public CustomerDTO createCustomer(CustomerDTO customerDto) {
        log.debug("Start method DefaultProcessorService.createCustomer Customer DTO = {}", customerDto);
        return customerManagementClient.save(customerDto);
    }

    @Override
    public CustomerDTO authorizationCustomer(AuthorizationParameterDTO authorizationDto) {
        return customerManagementClient.authorizationCustomer(authorizationDto);
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDto) {
        log.debug("Start method DefaultProcessorService.updateCustomer Customer DTO = {}", customerDto);
        return customerManagementClient.update(customerDto);
    }

    @Override
    public OfferDTO createOffer(OfferDTO offerDto) {
        log.debug("Start method DefaultProcessorService.createOffer Offer DTO = {}", offerDto);
        OfferDTO offerDtoCreated = null;
        if (customerManagementClient.getCustomerDTO(offerDto.getSellerId()) != null) {
            offerDtoCreated = catalogClient.save(offerDto);
        }
        return offerDtoCreated;
    }

    @Override
    public CustomerCommentParameterDTO addCommentToOffer(AddCommentToOfferParameterDTO addCommentToOfferDto) {
        log.debug("Start method DefaultProcessorService.addCommentToOffer" +
                " Customer DTO = {}", addCommentToOfferDto);

        CustomerDTO customerDto = customerManagementClient
                .getCustomerDTO(addCommentToOfferDto.getCustomerId());
        CustomerCommentParameterDTO customerCommentParameterDto = null;
        if (addCommentToOfferDto.getCustomerId().equals(customerDto.getId())) {
            CommentDTO commentDto = CommentDTO.builder()
                    .customerId(addCommentToOfferDto.getCustomerId())
                    .message(addCommentToOfferDto.getMessage())
                    .build();

            OfferDTO offerDto = catalogClient.addCommentToOffer(addCommentToOfferDto.getOfferId(), commentDto);

            AccountDTO accountDto = AccountDTO.builder()
                    .id(customerDto.getId())
                    .name(customerDto.getFirstName())
                    .surname(customerDto.getLastName())
                    .age(customerDto.getAge())
                    .email(customerDto.getEmail())
                    .phoneNumber(customerDto.getPhoneNumber())
                    .build();

            customerCommentParameterDto = CustomerCommentParameterDTO.builder()
                    .id(offerDto.getComments().get(offerDto.getComments().size() - 1).getId())
                    .message(addCommentToOfferDto.getMessage())
                    .accountDto(accountDto)
                    .build();
        }

        return customerCommentParameterDto;
    }

    @Override
    public OfferDTO updateOffer(OfferDTO offerDto) {
        log.debug("Start method DefaultProcessorService.updateOffer Offer DTO = {}", offerDto);
        return catalogClient.update(offerDto);
    }

    @Override
    public List<CommentDTO> getAllCommentsByOfferId(UUID id) {
        log.debug("Start method DefaultProcessorService.getAllCommentsByOfferId ID = {}", id);

        return catalogClient.getAllCommentsByOfferId(id);
    }

    @Override
    public OfferDTO getOfferById(UUID id) {
        log.info("Start method DefaultProcessorService.getOfferById ID = {}", id);
        return catalogClient.getOfferDTO(id);
    }

    @Override
    public List<OfferDTO> getOffersByFilter(String name, String category, String priceFrom, String priceTo) {
        log.info("Start method DefaultProcessorService.getOffersByFilter");

        return catalogClient.getOffersDtoByFilter(name, category, priceFrom, priceTo);
    }

    @Override
    public CustomerDTO getCustomerById(UUID id) {
        log.info("Start method DefaultProcessorService.getCustomersByEmail ID = {}", id);
        return customerManagementClient.getCustomerDTO(id);
    }

    @Override
    public List<OrderDTO> getOrderByCustomerId(UUID id) {
        log.info("Start method DefaultProcessorService.getOrderById ID = {}", id);
        return favouriteItemManagementClient.getOrdersDTO(id);
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        log.info("Start method DefaultProcessorService.getAllCategories");
        return catalogClient.getAllCategories();
    }

    @Override
    public List<MessageDTO> getConversationMessages(UUID id) {
        return customerManagementClient.getConversationMessages(id);
    }

    @Override
    public List<ConversationDTO> getConversationsByUserId(UUID id) {
        return customerManagementClient.getConversationsByUserId(id);
    }

    @Override
    public ConversationDTO getConversationInfo(UUID id, UUID otherId) {
        return customerManagementClient.getConversationInfo(id, otherId);
    }
}
