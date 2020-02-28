package com.bsuir.sdtt.service.impl;

import com.bsuir.sdtt.dto.AuthorizationParameterDTO;
import com.bsuir.sdtt.entity.Customer;
import com.bsuir.sdtt.repository.CustomerRepository;
import com.bsuir.sdtt.service.CustomerService;
import com.bsuir.sdtt.service.GoogleDriveService;
import com.bsuir.sdtt.validation.CustomerValidator;
import com.bsuir.sdtt.validation.ValidationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Class of customer service that allows you to work with a customer and implements CustomerService.
 *
 * @author Stsiapan Balashenka, Eugene Korenik
 * @version 1.1
 */
@Slf4j
@Service
@Transactional
public class DefaultCustomerService implements CustomerService {
    /**
     * Field of customer repository.
     */
    private final CustomerRepository customerRepository;

    private final GoogleDriveService imageService;

    private final PasswordEncoder passwordEncoder;

    private final CustomerValidator customerValidator;

    /**
     * Constructor that accepts a object CustomerDao class.
     *
     * @param customerRepository object of CustomerRepository class
     * @param imageService       object of GoogleDriveService class
     */
    @Autowired
    public DefaultCustomerService(CustomerRepository customerRepository,
                                  GoogleDriveService imageService, PasswordEncoder passwordEncoder,
                                  CustomerValidator customerValidator) {
        this.customerRepository = customerRepository;
        this.imageService = imageService;
        this.passwordEncoder = passwordEncoder;
        this.customerValidator = customerValidator;
    }

    /**
     * Method that save Customer in database.
     *
     * @param customer object that needs to save
     * @return saved object of Customer class
     */
    @Override
    public Customer create(Customer customer, String image) throws EntityExistsException {
        String errorMessage = customerValidator.isValid(customer, ValidationType.FOR_CREATING);
        if (!errorMessage.isEmpty()) {
            throw new EntityExistsException(errorMessage);
        }
        if (image != null && !image.equals("")) {
            try {
                setImage(customer, image);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        String password = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(password);
        customerRepository.save(customer);
        return customer;
    }

    @Override
    public Customer authorization(AuthorizationParameterDTO authorizationDTO) {
        String email = authorizationDTO.getEmail();
        Optional<Customer> optionalCustomer = customerRepository.findByEmail(email);
        Customer foundCustomer = optionalCustomer.<EntityNotFoundException>orElseThrow(() -> {
            throw new EntityNotFoundException("Customer with email = "
                    + email + " not found");
        });

        if (passwordEncoder.matches(authorizationDTO.getPassword(), foundCustomer.getPassword())) {
            return foundCustomer;
        } else {
            return null;
        }
    }

    /**
     * Method that finds an object in database.
     *
     * @param id UUID of the object to be found
     * @return founded object or NullPointerException
     */
    @Override
    public Customer findById(UUID id) throws EntityNotFoundException {
        return customerRepository.findById(id).<EntityNotFoundException>orElseThrow(() -> {
            throw new EntityNotFoundException("Customer with id =" + id.toString()
                    + " not found");
        });
    }

    /**
     * Method that finds all objects in database.
     *
     * @return founded objects
     */
    @Override
    public List<Customer> findAll() {
        Iterable<Customer> saveCustomers = customerRepository.findAll();
        List<Customer> createdCustomers = new ArrayList<>();
        for (Customer customer : saveCustomers) {
            createdCustomers.add(customer);
        }
        return createdCustomers;
    }

    /**
     * Method that save updated object.
     *
     * @param customer updated customer that needs to save
     * @return updated and saved customer
     */
    @Override
    public Customer update(Customer customer, String image) throws EntityExistsException {
        String errorMessage = customerValidator.isValid(customer, ValidationType.FOR_UPDATING);
        if (!errorMessage.isEmpty()) {
            throw new EntityExistsException(errorMessage);
        }
        if (image != null && !image.equals("")) {
            try {
                if (customer.getImageId() != null && customer.getCompressedImageId() != null) {
                    imageService.deleteImageFromGoogleDrive(customer.getImageId(),
                            customer.getCompressedImageId());
                }
                setImage(customer, image);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return customerRepository.save(customer);
    }

    /**
     * Method that update customer password
     *
     * @param customer customer with updated password needs to save
     * @return updated and saved customer
     */
    public Customer updatePassword(Customer customer) {
        String password = customer.getPassword();
        String newPassword = passwordEncoder.encode(password);
        customer.setPassword(newPassword);
        return customerRepository.save(customer);
    }

    /**
     * Method that delete object.
     *
     * @param id object that needs to delete
     */
    @Override
    public void delete(UUID id) throws EntityNotFoundException {
        customerRepository.delete(findById(id));
    }

    private void setImage(Customer offer, String image) throws IOException, GeneralSecurityException {
        File imageFile = imageService.convertStringToFile(image);
        String imageId = imageService.saveImageToGoogleDrive(imageFile);

        String compressedImagePath = imageService.compressionImage(imageFile);
        File compressedImageFile = new File(compressedImagePath);
        String compressedImageId = imageService.saveImageToGoogleDrive(compressedImageFile);

        offer.setImageId(imageId);
        offer.setCompressedImageId(compressedImageId);
        imageFile.delete();
        compressedImageFile.delete();
    }
}
