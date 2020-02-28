package com.bsuir.sdtt.controller;

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
import com.bsuir.sdtt.service.ProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Class of Processor REST Controller.
 *
 * @author Stsiapan Balashenka
 * @version 1.0
 */
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(value = "/api/v1/processor")
public class ProcessorController {

    private final ProcessorService processorService;

    @Autowired
    public ProcessorController(ProcessorService processorService) {
        this.processorService = processorService;
    }

    @PostMapping(path = "/orders")
    public OrderDTO addToFavorites(@Validated @RequestBody
                                           CreateOrderParameterDTO createOrderParameterDTO) {
        return processorService.addToFavorite(createOrderParameterDTO);
    }

    @PostMapping(path = "/customers")
    public CustomerDTO createCustomer(@Validated @RequestBody
                                              CustomerDTO customerDTO) {
        return processorService.createCustomer(customerDTO);
    }

    @PostMapping(path = "/offers")
    public OfferDTO createOffer(@Validated @RequestBody OfferDTO offerDTO) {
        return processorService.createOffer(offerDTO);
    }

    @PostMapping(path = "/authorization")
    public CustomerDTO authorizationCustomer(@Validated @RequestBody AuthorizationParameterDTO authorizationParameterDTO) {
        return processorService.authorizationCustomer(authorizationParameterDTO);
    }

    @PutMapping(path = "/customers")
    public CustomerDTO updateCustomer(@Validated @RequestBody
                                              CustomerDTO customerDTO) {
        return processorService.updateCustomer(customerDTO);
    }

    @PutMapping(path = "/offers")
    public OfferDTO updateOffer(@Validated @RequestBody OfferDTO offerDTO) {
        return processorService.updateOffer(offerDTO);
    }

    @PutMapping(path = "/offers/comments")
    public CustomerCommentParameterDTO addCommentToOffer(
            @Validated @RequestBody AddCommentToOfferParameterDTO addCommentToOfferParameterDTO) {
        return processorService.addCommentToOffer(addCommentToOfferParameterDTO);
    }

    @GetMapping(path = "/offers/comments/{id}")
    public List<CommentDTO> getAllCommentsByOfferId(@PathVariable("id") UUID id) {
        return processorService.getAllCommentsByOfferId(id);
    }

    @GetMapping(path = "/offers/filter")
    public List<OfferDTO> getOffersByFilter(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "priceFrom", required = false) String priceFrom,
            @RequestParam(value = "priceTo", required = false) String priceTo) {
        return processorService.getOffersByFilter(name, category, priceFrom, priceTo);
    }

    @GetMapping(path = "/offers/{id}")
    public OfferDTO getOfferById(@PathVariable("id") UUID id) {
        return processorService.getOfferById(id);
    }

    @GetMapping(path = "/customers/{id}")
    public CustomerDTO getCustomersById(@PathVariable("id") UUID id) {
        return processorService.getCustomerById(id);
    }

    @GetMapping(path = "/orders/{id}")
    public List<OrderDTO> getOrderByCustomerId(@PathVariable("id") UUID id) {
        return processorService.getOrderByCustomerId(id);
    }

    @GetMapping(path = "/categories}")
    public List<CategoryDTO> getAllCategories() {
        return processorService.getAllCategories();
    }

    @GetMapping(path = "/messages/conversations/{id}")
    public List<MessageDTO> getConversationMessages(@PathVariable("id") UUID id) {
        return processorService.getConversationMessages(id);
    }

    @GetMapping(path = "/conversations")
    public ConversationDTO getConversationInfo(@RequestParam("id") UUID id,
                                               @RequestParam("otherId") UUID otherId) {
        return processorService.getConversationInfo(id, otherId);
    }

    @GetMapping(path = "/conversations/users/{id}")
    public List<ConversationDTO> getConversationsByUserId(@PathVariable("id") UUID id) {
        return processorService.getConversationsByUserId(id);
    }
}
