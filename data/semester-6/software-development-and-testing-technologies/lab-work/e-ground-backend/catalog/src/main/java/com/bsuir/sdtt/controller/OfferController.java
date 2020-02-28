package com.bsuir.sdtt.controller;

import com.bsuir.sdtt.dto.CommentDTO;
import com.bsuir.sdtt.dto.OfferDTO;
import com.bsuir.sdtt.entity.Category;
import com.bsuir.sdtt.entity.Comment;
import com.bsuir.sdtt.entity.Offer;
import com.bsuir.sdtt.repository.CategoryRepository;
import com.bsuir.sdtt.service.OfferService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Class of Offer REST Controller. Contains CRUD methods and methods for updating offer.
 *
 * @author Stsiapan Balashenka
 * @version 1.0
 */
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(value = "api/v1/catalog/offers")
public class OfferController {
    /**
     * Field of Offer Service.
     */
    private final OfferService offerService;

    /**
     * Field of Category Repository.
     */
    private final CategoryRepository categoryRepository;

    /**
     * Field of Model Mapper converter.
     */
    private final ModelMapper modelMapper;

    @Autowired
    public OfferController(OfferService offerService,
                           CategoryRepository categoryRepository,
                           ModelMapper modelMapper) {
        this.offerService = offerService;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Method that converts DTO to class object and create it.
     *
     * @param offerDTO data transfer object
     * @return created object of Offer class
     */
    @PostMapping
    public OfferDTO create(@Validated @RequestBody OfferDTO offerDTO) {
        log.debug("In create method offer controller");
        Offer offerTemp = new Offer();
        modelMapper.map(offerDTO, offerTemp);
        Category category = categoryRepository.findFirstByName(offerDTO.getCategory());
        if (category == null) {
            Category categorySave = categoryRepository.save(new Category(offerDTO.getCategory()));
            offerTemp.setCategory(categorySave);
        } else {
            offerTemp.setCategory(category);
        }

        OfferDTO offerDTOTemp = new OfferDTO();
        modelMapper.map(offerService.create(offerTemp, offerDTO.getImage()), offerDTOTemp);
        return offerDTOTemp;
    }

    /**
     * Method that save updated object.
     *
     * @param offerDTO updated Offer that needs to save
     * @return updated and saved offer
     */
    @PutMapping
    public OfferDTO update(@Validated @RequestBody OfferDTO offerDTO) {
        log.debug("In update method offer controller");
        Offer offerTemp = new Offer();
        modelMapper.map(offerDTO, offerTemp);
        OfferDTO offerDTOTemp = new OfferDTO();
        modelMapper.map(offerService.update(offerTemp, offerDTO.getImage()), offerDTOTemp);
        return offerDTOTemp;
    }

    /**
     * Method that let add comment to offer.
     *
     * @param commentDTO comment
     * @return updated and saved offer
     */
    @PutMapping(path = "/{id}")
    public OfferDTO addComment(@PathVariable("id") UUID id, @Validated @RequestBody CommentDTO commentDTO) {
        Comment comment = new Comment();
        modelMapper.map(commentDTO, comment);
        OfferDTO offerDTOTemp = new OfferDTO();
        modelMapper.map(offerService.addComment(id, comment), offerDTOTemp);
        return offerDTOTemp;
    }

    /**
     * Method that changes Category to Offer.
     *
     * @param offerId      Offer Long
     * @param categoryName Category to change
     * @return updated Offer
     */
    @PutMapping(path = "/{offerId}/categories/{categoryName}")
    public OfferDTO changeCategory(@PathVariable("offerId") UUID offerId,
                                   @PathVariable("categoryName") String categoryName) {
        log.debug("In changeCategory method offer controller");
        OfferDTO offerDTOTemp = new OfferDTO();
        modelMapper.map(offerService.changeCategory(offerId, categoryName), offerDTOTemp);
        return offerDTOTemp;
    }

    /**
     * Method that finds Offer object by Long.
     *
     * @param id parameter to be searched
     * @return List of founded objects
     */
    @GetMapping(path = "/{id}")
    public OfferDTO getById(@PathVariable("id") UUID id) {
        log.debug("In getById method offer controller");
        OfferDTO offerDTOTemp = new OfferDTO();
        modelMapper.map(offerService.findById(id), offerDTOTemp);
        return offerDTOTemp;
    }

    /**
     * Method that finds all objects.
     *
     * @return founded objects
     */
    @GetMapping
    public List<OfferDTO> getAll() {
        log.debug("In getAll method offer controller");
        List<Offer> offersTemp = offerService.findAll();
        List<OfferDTO> offersDTOTemp = new ArrayList<>();
        toOfferDTOList(offersTemp, offersDTOTemp);
        return offersDTOTemp;
    }

    @GetMapping(path = "/filter")
    public List<OfferDTO> getAllByFilter(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "priceFrom", required = false) String priceFrom,
            @RequestParam(value = "priceTo", required = false) String priceTo) {
        log.debug("In getAllByFilter method offer controller");
        List<Offer> offersTemp = offerService.findAllByFilter(name, category, priceFrom, priceTo);
        List<OfferDTO> offersDTOTemp = new ArrayList<>();
        toOfferDTOList(offersTemp, offersDTOTemp);
        return offersDTOTemp;
    }

    /**
     * Method that delete object.
     *
     * @param id object that needs to delete
     */
    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable("id") UUID id) {
        log.debug("In delete method offer controller");
        offerService.delete(id);
    }

    private void toOfferDTOList(List<Offer> offersTemp, List<OfferDTO> offersDtoTemp) {
        for (Offer offer : offersTemp) {
            OfferDTO offerDtoTemp = new OfferDTO();
            modelMapper.map(offer, offerDtoTemp);
            offersDtoTemp.add(offerDtoTemp);
        }
    }
}
