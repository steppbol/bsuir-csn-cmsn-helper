package com.bsuir.sdtt.service;

import com.bsuir.sdtt.dto.catalog.CategoryDTO;
import com.bsuir.sdtt.dto.catalog.CommentDTO;
import com.bsuir.sdtt.dto.catalog.OfferDTO;
import com.bsuir.sdtt.dto.customer.ConversationDTO;
import com.bsuir.sdtt.dto.customer.CustomerDTO;
import com.bsuir.sdtt.dto.customer.MessageDTO;
import com.bsuir.sdtt.dto.favourite.OrderDTO;
import com.bsuir.sdtt.dto.processor.AddCommentToOfferParameterDTO;
import com.bsuir.sdtt.dto.processor.AuthorizationParameterDTO;
import com.bsuir.sdtt.dto.processor.CreateOrderParameterDTO;
import com.bsuir.sdtt.dto.processor.CustomerCommentParameterDTO;

import java.util.List;
import java.util.UUID;

/**
 * Class of processor service.
 *
 * @author Stsiapan Balashenka
 * @version 1.0
 */
public interface ProcessorService {
    OrderDTO addToFavorite(CreateOrderParameterDTO createOrderParameter);

    CustomerDTO createCustomer(CustomerDTO customerDto);

    CustomerDTO updateCustomer(CustomerDTO customerDto);

    CustomerDTO authorizationCustomer(AuthorizationParameterDTO authorizationDto);

    OfferDTO createOffer(OfferDTO offerDto);

    OfferDTO updateOffer(OfferDTO offerDto);

    CustomerCommentParameterDTO addCommentToOffer(AddCommentToOfferParameterDTO addCommentToOfferDto);

    List<CommentDTO> getAllCommentsByOfferId(UUID id);

    CustomerDTO getCustomerById(UUID id);

    OfferDTO getOfferById(UUID id);

    List<OfferDTO> getOffersByFilter(String name, String category,
                                     String priceFrom, String priceTo);

    List<OrderDTO> getOrderByCustomerId(UUID id);

    List<CategoryDTO> getAllCategories();

    List<MessageDTO> getConversationMessages(UUID id);

    List<ConversationDTO> getConversationsByUserId(UUID id);

    ConversationDTO getConversationInfo(UUID id, UUID otherId);
}
