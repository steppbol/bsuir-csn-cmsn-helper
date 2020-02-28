package com.bsuir.sdtt.repository;

import com.bsuir.sdtt.entity.Category;
import com.bsuir.sdtt.entity.Offer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Interface of Offer repository that extends CrudRepository.
 * Contains CRUD methods and methods for updating offer.
 *
 * @author Stsiapan Balashenka
 * @version 1.0
 */
@Repository
public interface OfferRepository extends CrudRepository<Offer, UUID> {
    /**
     * Method that finds Offer object by Category in database.
     *
     * @param category parameter to be searched
     * @return List of founded objects
     */
    List<Offer> findAllByCategory(Category category);

    List<Offer> findAllByNameContaining(String name);

    List<Offer> findAllByNameContainingAndCategory(String name, Category category);
}
