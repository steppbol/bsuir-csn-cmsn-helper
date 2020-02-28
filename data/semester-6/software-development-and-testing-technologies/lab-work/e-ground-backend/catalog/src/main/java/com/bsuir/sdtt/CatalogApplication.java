package com.bsuir.sdtt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * The service that allows work with entity: create, update, read, delete.
 *
 * @author Stsiapan Balashenka
 * @version 1.0
 * @since 20-02-2019
 */
@SpringBootApplication
@EnableJpaRepositories
public class CatalogApplication {
    public static void main(String[] args) {
        SpringApplication.run(CatalogApplication.class, args);
    }
}