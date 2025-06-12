package com.udea.gpx.service;

import org.springframework.stereotype.Service;

import com.udea.gpx.model.Category;
import com.udea.gpx.repository.ICategoryRepository;

import java.util.List;

@Service
public class CategoryService {
    private final ICategoryRepository categoryRepository;

    public CategoryService(ICategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }

}
