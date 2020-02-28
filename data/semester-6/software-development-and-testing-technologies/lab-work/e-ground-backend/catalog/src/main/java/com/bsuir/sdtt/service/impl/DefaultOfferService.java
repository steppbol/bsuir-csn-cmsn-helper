package com.bsuir.sdtt.service.impl;

import com.bsuir.sdtt.entity.Category;
import com.bsuir.sdtt.entity.Comment;
import com.bsuir.sdtt.entity.Offer;
import com.bsuir.sdtt.repository.CategoryRepository;
import com.bsuir.sdtt.repository.CommentRepository;
import com.bsuir.sdtt.repository.OfferRepository;
import com.bsuir.sdtt.service.GoogleDriveService;
import com.bsuir.sdtt.service.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Class of offer service that allows you to work with offers and implements OfferService.
 *
 * @author Stsiapan Balashenka
 * @version 1.0
 */
@Service
@Transactional
public class DefaultOfferService implements OfferService {
    private final OfferRepository offerRepository;

    private final CategoryRepository categoryRepository;

    private final CommentRepository commentRepository;

    private final GoogleDriveService imageService;

    @Autowired
    public DefaultOfferService(OfferRepository offerRepository,
                               CategoryRepository categoryRepository, CommentRepository commentRepository, GoogleDriveService imageService) {
        this.offerRepository = offerRepository;
        this.categoryRepository = categoryRepository;
        this.commentRepository = commentRepository;
        this.imageService = imageService;
    }

    /**
     * Method that save object in database.
     *
     * @param offer object that needs to save
     * @return saved object of Offer class
     */
    @Override
    public Offer create(Offer offer, String image) {
        if (image != null && !image.equals("")) {
            try {
                setImage(offer, image);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return offerRepository.save(offer);
    }

    /**
     * Method that finds all objects in database.
     *
     * @return founded objects
     */
    @Override
    public List<Offer> findAll() {
        Iterable<Offer> saveOffer = offerRepository.findAll();
        List<Offer> createdOffers = new ArrayList<>();
        for (Offer offer : saveOffer) {
            createdOffers.add(offer);
        }
        return createdOffers;
    }

    @Override
    public List<Offer> findAllByFilter(String name, String category, String priceFrom,
                                       String priceTo) {
        Iterable<Offer> saveOffer;

        Category foundCategory = null;

        double priceFromTemp;

        double priceToTemp;

        List<Offer> foundOffers = new ArrayList<>();

        if (category != null) {
            foundCategory = categoryRepository.findFirstByName(category);
        }

        if (priceFrom != null && Double.valueOf(priceFrom) >= 0) {
            priceFromTemp = Double.valueOf(priceFrom);
        } else {
            priceFromTemp = 0;
        }

        if (priceTo != null && Double.valueOf(priceTo) > priceFromTemp) {
            priceToTemp = Double.valueOf(priceTo);
        } else {
            priceToTemp = Double.MAX_VALUE;
        }

        if (category != null && name == null) {
            saveOffer = offerRepository.findAllByCategory(foundCategory);
        } else if (category == null && name != null) {
            saveOffer = offerRepository.findAllByNameContaining(name);
        } else if (category != null && name != null) {
            saveOffer = offerRepository.findAllByNameContainingAndCategory(name, foundCategory);
        } else {
            saveOffer = offerRepository.findAll();
        }

        for (Offer offer : saveOffer) {
            if (offer.getPrice() >= priceFromTemp
                    && offer.getPrice() < priceToTemp) {
                foundOffers.add(offer);
            }
        }

        return foundOffers;
    }

    /**
     * Method that finds Offer object by Long in database.
     *
     * @param id parameter to be searched
     * @return list of founded objects
     */
    @Override
    public Offer findById(UUID id) {
        return offerRepository.findById(id)
                .orElseThrow(NullPointerException::new);
    }

    /**
     * Method that save updated object in database.
     *
     * @param offer updated offer that needs to save
     * @return updated and saved offer
     */
    @Override
    public Offer update(Offer offer, String image) {
        if (image != null && !image.equals("")) {
            try {
                if (offer.getImageId() != null && offer.getCompressedImageId() != null) {
                    imageService.deleteImageFromGoogleDrive(offer.getImageId(), offer.getCompressedImageId());
                }
                setImage(offer, image);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return offerRepository.save(offer);
    }

    @Override
    public Offer addComment(UUID id, Comment comment) {
        Offer offer = offerRepository.findById(id).orElseThrow(NullPointerException::new);
        comment.setOffer(offer);
        offer.getComments().add(comment);
        commentRepository.save(comment);
        return offerRepository.save(offer);
    }

    /**
     * Method that delete object in database.
     *
     * @param id parameter to be searched
     */
    @Override
    public void delete(UUID id) {
        offerRepository.deleteById(id);
    }

    /**
     * Method that changes Category to Offer.
     *
     * @param offerId  Offer class object
     * @param category Category class object
     * @return updated Offer
     */
    @Override
    public Offer changeCategory(UUID offerId, String category) {
        Offer offer = offerRepository
                .findById(offerId)
                .orElseThrow(NullPointerException::new);
        Category categoryByName = categoryRepository.findFirstByName(category);
        offer.setCategory(categoryByName);
        Offer savedOffer = offerRepository.save(offer);
        return savedOffer;
    }

    private void setImage(Offer offer, String image) throws IOException, GeneralSecurityException {
        File imageFile = imageService.convertStringToFile(image);
        String imageId = imageService.saveImageToGoogleDrive(imageFile);

        String comressedImagePath = imageService.compressionImage(imageFile);
        File comressedImageFile = new File(comressedImagePath);
        String comressedImageId = imageService.saveImageToGoogleDrive(comressedImageFile);

        offer.setImageId(imageId);
        offer.setCompressedImageId(comressedImageId);
        imageFile.delete();
        comressedImageFile.delete();
    }
}
