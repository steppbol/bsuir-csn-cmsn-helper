package com.bsuir.sdtt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * The service that allows work with inventory: create, update, read, delete order.
 *
 * @author Stsiapan Balashenka
 * @version 1.0
 * @since 20-02-2019
 */
@SpringBootApplication
@EnableJpaRepositories
public class FavouriteItemManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(FavouriteItemManagementApplication.class, args);
    }
}
