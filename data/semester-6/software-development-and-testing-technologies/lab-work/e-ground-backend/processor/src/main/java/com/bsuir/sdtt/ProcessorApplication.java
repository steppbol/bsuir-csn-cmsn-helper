package com.bsuir.sdtt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The service that allows work with other service: controls them.
 *
 * @author Stsiapan Balashenka
 * @version 1.0
 * @since 20-02-2019
 */
@SpringBootApplication
public class ProcessorApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProcessorApplication.class, args);
    }
}
