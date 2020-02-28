package com.bsuir.sdtt.client;

import com.bsuir.sdtt.dto.catalog.CategoryDTO;
import com.bsuir.sdtt.dto.catalog.CommentDTO;
import com.bsuir.sdtt.dto.catalog.OfferDTO;
import com.bsuir.sdtt.util.CatalogClientProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Class of Catalog Client
 *
 * @author Stsiapan Balashenka
 * @version 1.0
 */
@Component
@Slf4j
public class CatalogClient {
    private final RestTemplate restTemplate;

    @Value("${catalog.url}")
    private String baseUrl;

    @Autowired
    public CatalogClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public OfferDTO save(OfferDTO offerDTO) {
        log.info("Start method CatalogClient.save: {}", offerDTO);

        StringBuilder finalUrl = new StringBuilder(baseUrl);
        finalUrl.append(CatalogClientProperty.API_CATALOG_OFFERS);

        log.info("Final URL: {}", finalUrl.toString());

        return getResponseEntityOffer(finalUrl, HttpMethod.POST, offerDTO).getBody();
    }


    public OfferDTO update(OfferDTO offerDTO) {
        log.info("Start method CatalogClient.update: {}", offerDTO);

        StringBuilder finalUrl = new StringBuilder(baseUrl);
        finalUrl.append(CatalogClientProperty.API_CATALOG_OFFERS);

        log.info("Final URL: {}", finalUrl.toString());

        return getResponseEntityOffer(finalUrl, HttpMethod.PUT, offerDTO).getBody();
    }

    public OfferDTO addCommentToOffer(UUID id, CommentDTO commentDTO) {
        log.info("Start method CatalogClient.addCommentToOffer: ID = {}, Comment DTO = {}",
                id, commentDTO);

        StringBuilder finalUrl = new StringBuilder(baseUrl);
        finalUrl.append(CatalogClientProperty.API_CATALOG_OFFERS);
        finalUrl.append(id);

        log.info("Final URL: {}", finalUrl.toString());

        return getResponseEntityOffer(finalUrl, HttpMethod.PUT, commentDTO).getBody();
    }

    public OfferDTO getOfferDTO(UUID id) {
        log.info("Start method CatalogClient.getOfferDTO ID: {}", id);

        StringBuilder finalUrl = new StringBuilder(baseUrl);
        finalUrl.append(CatalogClientProperty.API_CATALOG_OFFERS);
        finalUrl.append(id);

        log.info("Final URL: {}", finalUrl.toString());

        ResponseEntity<OfferDTO> responseEntity = restTemplate
                .exchange(finalUrl.toString(), HttpMethod.GET,
                        getHttpEntityHeader(""), OfferDTO.class);

        log.info("Offer DTO: {}", responseEntity.getBody());

        return responseEntity.getBody();
    }

    public List<OfferDTO> getOffersDtoByFilter
            (String name, String category, String priceFrom, String priceTo) {
        log.info("Start method CatalogClient.getOffersDtoByFilter" +
                        " Category = {} Price From = {} Price To = {}",
                category, priceFrom, priceTo);

        StringBuilder finalUrl = new StringBuilder(baseUrl);
        finalUrl.append(CatalogClientProperty.API_CATALOG_OFFERS_FILTER);
        finalUrl.append("?");

        if (name != null) {
            finalUrl.append("firstName=");
            finalUrl.append(name);
        }

        if (category != null && name == null) {
            finalUrl.append("&category=");
            finalUrl.append(category);
        } else if (category != null && name != null) {
            finalUrl.append("&category=");
            finalUrl.append(category);
        }

        if (priceFrom != null && category == null) {
            finalUrl.append("&priceFrom=");
            finalUrl.append(priceFrom);
        } else if (priceFrom != null && category != null) {
            finalUrl.append("&priceFrom=");
            finalUrl.append(priceFrom);
        }

        if (priceTo != null && priceFrom == null) {
            finalUrl.append("&priceTo=");
            finalUrl.append(priceTo);
        } else if (priceTo != null && priceFrom != null) {
            finalUrl.append("&priceTo=");
            finalUrl.append(priceTo);
        }

        log.info("Final URL: {}", finalUrl.toString());

        ResponseEntity<OfferDTO[]> responseEntity = restTemplate
                .exchange(finalUrl.toString(), HttpMethod.GET,
                        getHttpEntityHeader(""), OfferDTO[].class);

        log.info("Size Orders DTO: {}", Objects
                .requireNonNull(responseEntity.getBody()).length);
        return Arrays.asList(responseEntity.getBody());
    }

    public List<CategoryDTO> getAllCategories() {
        log.info("Start method CatalogClient.getAllCategories");

        StringBuilder finalUrl = new StringBuilder(baseUrl);
        finalUrl.append(CatalogClientProperty.API_CATALOG_CATEGORIES);

        log.info("Final URL: {}", finalUrl.toString());

        ResponseEntity<CategoryDTO[]> responseEntity = restTemplate
                .exchange(finalUrl.toString(), HttpMethod.GET,
                        getHttpEntityHeader(""), CategoryDTO[].class);

        log.info("Size Categories DTO: {}", Objects
                .requireNonNull(responseEntity.getBody()).length);

        return Arrays.asList(responseEntity.getBody());
    }

    public List<CommentDTO> getAllCommentsByOfferId(UUID id) {
        log.info("Start method CatalogClient.getAllCommentsByOfferId ID: {}", id);

        StringBuilder finalUrl = new StringBuilder(baseUrl);
        finalUrl.append(CatalogClientProperty.API_CATALOG_COMMENTS);
        finalUrl.append(id);

        log.info("Final URL: {}", finalUrl.toString());

        ResponseEntity<CommentDTO[]> responseEntity = restTemplate
                .exchange(finalUrl.toString(), HttpMethod.GET,
                        getHttpEntityHeader(""), CommentDTO[].class);

        log.info("Size Comments DTO: {}", Objects
                .requireNonNull(responseEntity.getBody()).length);

        return Arrays.asList(responseEntity.getBody());
    }

    private <T> HttpEntity<T> getHttpEntityHeader(T template) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return new HttpEntity<>(template, headers);
    }

    private <U> ResponseEntity<OfferDTO> getResponseEntityOffer(StringBuilder finalUrl,
                                                                HttpMethod httpMethod, U template) {
        return restTemplate.exchange(finalUrl.toString(),
                httpMethod, getHttpEntityHeader(template), OfferDTO.class);
    }
}
