package com.bsuir.sdtt.service;

import com.bsuir.sdtt.entity.Comment;
import com.bsuir.sdtt.entity.Offer;

import java.util.List;
import java.util.UUID;

/**
 * Interface of offer service. Contains CRUD methods and methods for updating offer.
 *
 * @author Stsiapan Balashenka
 * @version 1.0
 */
public interface OfferService {
    Offer create(Offer offer, String image);

    List<Offer> findAll();

    List<Offer> findAllByFilter(String name, String category, String priceFrom, String priceTo);

    Offer findById(UUID id);

    Offer update(Offer offer, String image);

    Offer addComment(UUID id, Comment comment);

    void delete(UUID id);

    Offer changeCategory(UUID offerId, String category);
}
