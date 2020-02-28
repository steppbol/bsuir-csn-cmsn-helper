package com.bsuir.sdtt.validation;

import com.bsuir.sdtt.entity.Customer;
import com.bsuir.sdtt.repository.CustomerRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Class for validation customer data before creating or updating.
 * Check if a customer with certain phone number and email exists.
 *
 * @author Eugene Korenik
 * @version 1.0
 */
@Component
public class CustomerValidator {

    /**
     * Field of customer repository
     */
    private CustomerRepository customerRepository;


    public CustomerValidator(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public String isValid(Customer customer, ValidationType validationType) {
        String phoneNumber = customer.getPhoneNumber();
        String email = customer.getEmail();
        StringBuilder errorBuilder = new StringBuilder();

        Optional<Customer> withSuchPhone = customerRepository
                .findByPhoneNumber(phoneNumber);
        if (withSuchPhone.isPresent()) {
            if (validationType == ValidationType.FOR_CREATING ||
                    (validationType == ValidationType.FOR_UPDATING &&
                            withSuchPhone.get().getId().compareTo(customer.getId()) != 0)) {
                errorBuilder.append(buildErrorMessage("phone", phoneNumber));
            }
        }

        Optional<Customer> withSuchEmail = customerRepository.findByEmail(email);
        if (withSuchEmail.isPresent()) {
            if (validationType == ValidationType.FOR_CREATING ||
                    (validationType == ValidationType.FOR_UPDATING &&
                            withSuchEmail.get().getId().compareTo(customer.getId()) != 0)) {
                errorBuilder.append(buildErrorMessage("email", email));
            }
        }
        return errorBuilder.toString();
    }

    private String buildErrorMessage(String type, String value) {
        StringBuilder builder = new StringBuilder("Customer with such ");
        builder.append(type);
        builder.append(" (");
        builder.append(value);
        builder.append(") already exist");
        return builder.toString();
    }
}
