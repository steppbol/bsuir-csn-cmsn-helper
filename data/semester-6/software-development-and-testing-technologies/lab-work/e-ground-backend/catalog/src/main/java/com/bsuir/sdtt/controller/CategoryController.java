package com.bsuir.sdtt.controller;

import com.bsuir.sdtt.dto.CategoryDTO;
import com.bsuir.sdtt.entity.Category;
import com.bsuir.sdtt.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Class of Offer REST Controller. Contains CRUD methods.
 *
 * @author Stsiapan Balashenka
 * @version 1.0
 */
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(value = "api/v1/catalog/categories")
public class CategoryController {
    /**
     * Field of Category Service.
     */
    private final CategoryService categoryService;

    /**
     * Field of Model Mapper converter.
     */
    private ModelMapper modelMapper;

    /**
     * Constructor that accepts a object CategoryService class.
     *
     * @param categoryService object of CategoryService class
     */
    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
        this.modelMapper = new ModelMapper();
    }

    /**
     * Method that converts DTO to class object and create it.
     *
     * @param categoryDTO data transfer object
     * @return created object of Category class
     */
    @PostMapping
    public CategoryDTO create(@Validated @RequestBody CategoryDTO categoryDTO) {
        log.debug("In create method category controller");
        Category categoryTemp = new Category();
        modelMapper.map(categoryDTO, categoryTemp);
        CategoryDTO categoryDTOTemp = new CategoryDTO();
        modelMapper.map(categoryService.create(categoryTemp), categoryDTOTemp);
        return categoryDTOTemp;
    }

    /**
     * Method that save updated object.
     *
     * @param categoryDTO updated category that needs to save
     * @return updated and saved category
     */
    @PutMapping
    public CategoryDTO update(@Validated @RequestBody CategoryDTO categoryDTO) {
        log.debug("In update method category controller");
        Category categoryTemp = new Category();
        modelMapper.map(categoryDTO, categoryTemp);
        CategoryDTO categoryDTOTemp = new CategoryDTO();
        modelMapper.map(categoryService.update(categoryTemp), categoryDTOTemp);
        return categoryDTOTemp;
    }

    /**
     * Method that finds an object.
     *
     * @param id Long of the object to be found
     * @return founded object or NullPointerException
     */
    @GetMapping(path = "/{id}")
    public CategoryDTO getById(@PathVariable("id") UUID id) {
        log.debug("In getById method category controller");
        CategoryDTO categoryDTOTemp = new CategoryDTO();
        modelMapper.map(categoryService.findById(id), categoryDTOTemp);
        return categoryDTOTemp;
    }

    /**
     * Method that finds all objects.
     *
     * @return founded objects
     */
    @GetMapping
    public List<CategoryDTO> getAll() {
        log.debug("In getAll method category controller");
        List<CategoryDTO> categoriesDTOTemp = new ArrayList<>();
        List<Category> categoriesTemp = categoryService.findAll();

        for (Category category : categoriesTemp) {
            CategoryDTO categoryDTO = new CategoryDTO();
            modelMapper.map(category, categoryDTO);
            categoriesDTOTemp.add(categoryDTO);
        }

        return categoriesDTOTemp;
    }

    /**
     * Method that delete object.
     *
     * @param id Long object that needs to delete
     */
    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable("id") UUID id) {
        log.debug("In delete method category controller");
        categoryService.delete(id);
    }
}