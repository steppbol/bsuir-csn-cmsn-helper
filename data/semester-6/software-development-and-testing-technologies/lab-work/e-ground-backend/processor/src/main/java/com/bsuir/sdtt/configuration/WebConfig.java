package com.bsuir.sdtt.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Class of Web Rest configuration.
 *
 * @author Stsiapan Balashenka
 * @version 1.0
 */
@Configuration
public class WebConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
