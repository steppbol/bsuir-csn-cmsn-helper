package com.bsuir.sdtt.service;

import com.bsuir.sdtt.entity.Category;

import java.util.List;
import java.util.UUID;

/**
 * Interface of category service. Contains CRUD methods.
 *
 * @author Stsiapan Balashenka
 * @version 1.0
 */
public interface CategoryService {
    Category create(Category categoryDto);

    Category findById(UUID id);

    List<Category> findAll();

    Category update(Category category);

    void delete(UUID id);
}
