package com.udea.GPX.service;

import com.udea.GPX.model.Category;
import com.udea.GPX.repository.ICategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("CategoryService Tests")
class CategoryServiceTest {

    @Mock
    private ICategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Test Category");
    }

    // ========== GET ALL CATEGORIES TESTS ==========

    @Test
    @DisplayName("getAllCategories - Debe retornar todas las categorías")
    void getAllCategories_shouldReturnAllCategories() {
        // Given
        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Category 2");

        List<Category> categories = Arrays.asList(testCategory, category2);
        when(categoryRepository.findAll()).thenReturn(categories);

        // When
        List<Category> result = categoryService.getAllCategories();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(testCategory, category2);
        verify(categoryRepository).findAll();
    }

    @Test
    @DisplayName("getAllCategories - Debe retornar lista vacía cuando no hay categorías")
    void getAllCategories_shouldReturnEmptyListWhenNoCategories() {
        // Given
        when(categoryRepository.findAll()).thenReturn(List.of());

        // When
        List<Category> result = categoryService.getAllCategories();

        // Then
        assertThat(result).isEmpty();
        verify(categoryRepository).findAll();
    }

    // ========== GET CATEGORY BY ID TESTS ==========

    @Test
    @DisplayName("getCategoryById - Debe retornar categoría existente")
    void getCategoryById_shouldReturnExistingCategory() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        // When
        Category result = categoryService.getCategoryById(1L);

        // Then
        assertThat(result).isEqualTo(testCategory);
        verify(categoryRepository).findById(1L);
    }

    @Test
    @DisplayName("getCategoryById - Debe retornar null para categoría inexistente")
    void getCategoryById_shouldReturnNullForNonExistentCategory() {
        // Given
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Category result = categoryService.getCategoryById(999L);

        // Then
        assertThat(result).isNull();
        verify(categoryRepository).findById(999L);
    }

    // ========== SAVE CATEGORY TESTS ==========

    @Test
    @DisplayName("save - Debe guardar nueva categoría exitosamente")
    void save_shouldSaveNewCategorySuccessfully() {
        // Given
        Category newCategory = new Category();
        newCategory.setName("New Category");

        Category savedCategory = new Category();
        savedCategory.setId(3L);
        savedCategory.setName("New Category");

        when(categoryRepository.save(newCategory)).thenReturn(savedCategory);

        // When
        Category result = categoryService.save(newCategory);

        // Then
        assertThat(result).isEqualTo(savedCategory);
        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getName()).isEqualTo("New Category");
        verify(categoryRepository).save(newCategory);
    }

    @Test
    @DisplayName("save - Debe actualizar categoría existente")
    void save_shouldUpdateExistingCategory() {
        // Given
        testCategory.setName("Updated Category");
        when(categoryRepository.save(testCategory)).thenReturn(testCategory);

        // When
        Category result = categoryService.save(testCategory);

        // Then
        assertThat(result).isEqualTo(testCategory);
        assertThat(result.getName()).isEqualTo("Updated Category");
        verify(categoryRepository).save(testCategory);
    }

    // ========== DELETE CATEGORY TESTS ==========

    @Test
    @DisplayName("delete - Debe eliminar categoría exitosamente")
    void delete_shouldDeleteCategorySuccessfully() {
        // Given
        doNothing().when(categoryRepository).deleteById(1L);

        // When
        categoryService.delete(1L);

        // Then
        verify(categoryRepository).deleteById(1L);
    }

    @Test
    @DisplayName("delete - Debe manejar eliminación de categoría inexistente")
    void delete_shouldHandleDeletionOfNonExistentCategory() {
        // Given
        doNothing().when(categoryRepository).deleteById(999L);

        // When
        categoryService.delete(999L);

        // Then
        verify(categoryRepository).deleteById(999L);
    }

    // ========== EDGE CASES ==========

    @Test
    @DisplayName("save - Debe manejar categoría con nombre nulo")
    void save_shouldHandleCategoryWithNullName() {
        // Given
        Category categoryWithNullName = new Category();
        categoryWithNullName.setName(null);

        when(categoryRepository.save(categoryWithNullName)).thenReturn(categoryWithNullName);

        // When
        Category result = categoryService.save(categoryWithNullName);

        // Then
        assertThat(result).isEqualTo(categoryWithNullName);
        assertThat(result.getName()).isNull();
        verify(categoryRepository).save(categoryWithNullName);
    }

    @Test
    @DisplayName("save - Debe manejar categoría con nombre vacío")
    void save_shouldHandleCategoryWithEmptyName() {
        // Given
        Category categoryWithEmptyName = new Category();
        categoryWithEmptyName.setName("");

        when(categoryRepository.save(categoryWithEmptyName)).thenReturn(categoryWithEmptyName);

        // When
        Category result = categoryService.save(categoryWithEmptyName);

        // Then
        assertThat(result).isEqualTo(categoryWithEmptyName);
        assertThat(result.getName()).isEmpty();
        verify(categoryRepository).save(categoryWithEmptyName);
    }
}
