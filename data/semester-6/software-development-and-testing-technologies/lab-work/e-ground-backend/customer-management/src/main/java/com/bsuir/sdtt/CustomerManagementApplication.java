package com.bsuir.sdtt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The service that allows work with customer: create, update, read, delete.
 *
 * @author Stsiapan Balashenka
 * @version 1.0
 * @since 08-02-2019
 */
@SpringBootApplication
public class CustomerManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(CustomerManagementApplication.class, args);
    }
}
