package com.bsuir.sdtt.service.impl;

import com.bsuir.sdtt.entity.Category;
import com.bsuir.sdtt.repository.CategoryRepository;
import com.bsuir.sdtt.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Class of category service that allows you to work with a category and implements CategoryService.
 *
 * @author Stsiapan Balashenka
 * @version 1.0
 */
@Service
@Transactional
public class DefaultCategoryService implements CategoryService {
    /**
     * Field of Category Repository.
     */
    private final CategoryRepository categoryRepository;

    /**
     * Constructor that accepts a object CategoryRepository class.
     *
     * @param categoryRepository object of CategoryRepository class
     */
    @Autowired
    public DefaultCategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Method that save Category in database.
     *
     * @param category object that needs to save
     * @return saved object of Category class
     */
    @Override
    public Category create(Category category) {
        return categoryRepository.save(category);
    }

    /**
     * Method that finds an object in database.
     *
     * @param id Long of the object to be found
     * @return founded object or NullPointerException
     */
    @Override
    public Category findById(UUID id) throws EntityNotFoundException {
        return categoryRepository.findById(id).<EntityNotFoundException>orElseThrow(() -> {
            throw new EntityNotFoundException("Category with id = "
                    + id.toString() + " not found.");
        });
    }

    /**
     * Method that finds all objects in database.
     *
     * @return founded objects
     */
    @Override
    public List<Category> findAll() {
        Iterable<Category> saveCategories = categoryRepository.findAll();
        List<Category> createdCategories = new ArrayList<>();
        for (Category category : saveCategories) {
            createdCategories.add(category);
        }
        return createdCategories;
    }

    /**
     * Method that save updated object.
     *
     * @param category updated category that needs to save
     * @return updated and saved category
     */
    @Override
    public Category update(Category category) {
        return create(category);
    }

    /**
     * Method that delete object.
     *
     * @param id object that needs to delete
     */
    @Override
    public void delete(UUID id) {
        try {
            categoryRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(e.getMessage());
        }
    }
}
