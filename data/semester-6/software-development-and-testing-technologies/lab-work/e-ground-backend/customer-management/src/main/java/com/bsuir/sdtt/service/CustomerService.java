package com.bsuir.sdtt.service;

import com.bsuir.sdtt.dto.AuthorizationParameterDTO;
import com.bsuir.sdtt.entity.Customer;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;

/**
 * Class of customer service that allows you to work with offers and implements CustomerService.
 *
 * @author Stsiapan Balashenka, Eugene Korenik
 * @version 1.1
 */
public interface CustomerService {
    Customer create(Customer customer, String image) throws EntityExistsException;

    Customer authorization(AuthorizationParameterDTO authorizationDTO);

    Customer findById(UUID id);

    List<Customer> findAll();

    Customer update(Customer customer, String image) throws EntityNotFoundException;

    Customer updatePassword(Customer customer);

    void delete(UUID id);
}
