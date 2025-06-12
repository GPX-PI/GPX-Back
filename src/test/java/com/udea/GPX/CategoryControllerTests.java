package com.udea.gpx;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.udea.gpx.controller.CategoryController;
import com.udea.gpx.model.Category;
import com.udea.gpx.service.CategoryService;
import com.udea.gpx.util.AuthUtils;

import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryController Unit Tests")
class CategoryControllerTests {

    @Mock
    private CategoryService categoryService;

    @Mock
    private AuthUtils authUtils;

    @InjectMocks
    private CategoryController categoryController;
    private Category testCategory1;
    private Category testCategory2;

    @BeforeEach
    void setUp() {
        testCategory1 = new Category(1L, "Autos", "Categoría para autos");
        testCategory2 = new Category(2L, "Motos", "Categoría para motos");
    }

    // ========== GET ALL CATEGORIES TESTS ==========

    @Test
    @DisplayName("getAllCategories - Debe retornar todas las categorías")
    void getAllCategories_shouldReturnAllCategories() {
        // Given
        List<Category> categories = Arrays.asList(testCategory1, testCategory2);
        when(categoryService.getAllCategories()).thenReturn(categories);

        // When
        ResponseEntity<List<Category>> response = categoryController.getAllCategories();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Category> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(2, responseBody.size());
        assertEquals("Autos", responseBody.get(0).getName());
        assertEquals("Motos", responseBody.get(1).getName());
        verify(categoryService).getAllCategories();
    }

    // ========== GET CATEGORY BY ID TESTS ==========

    @Test
    @DisplayName("getCategoryById - Debe retornar categoría cuando existe")
    void getCategoryById_whenCategoryExists_shouldReturnCategory() {
        // Given
        when(categoryService.getCategoryById(1L)).thenReturn(testCategory1);

        // When
        ResponseEntity<Category> response = categoryController.getCategoryById(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Category responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("Autos", responseBody.getName());
        assertEquals("Categoría para autos", responseBody.getDetails());
        verify(categoryService).getCategoryById(1L);
    }

    @Test
    @DisplayName("getCategoryById - Debe retornar NOT_FOUND cuando no existe")
    void getCategoryById_whenCategoryNotExists_shouldReturnNotFound() {
        // Given
        when(categoryService.getCategoryById(99L)).thenReturn(null);

        // When
        ResponseEntity<Category> response = categoryController.getCategoryById(99L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(categoryService).getCategoryById(99L);
    }

    // ========== CREATE CATEGORY TESTS ==========

    @Test
    @DisplayName("createCategory - Debe crear categoría cuando el usuario es admin")
    void createCategory_whenAdmin_shouldCreateAndReturnCategory() {
        // Given
        Category newCategory = new Category(null, "Bicicletas", "Categoría para bicicletas");
        Category savedCategory = new Category(3L, "Bicicletas", "Categoría para bicicletas");
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        when(categoryService.save(newCategory)).thenReturn(savedCategory);

        // When
        ResponseEntity<Category> response = categoryController.createCategory(newCategory);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Category responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(savedCategory, responseBody);
        verify(authUtils).isCurrentUserAdmin();
        verify(categoryService).save(newCategory);
    }

    @Test
    @DisplayName("createCategory - Debe retornar FORBIDDEN cuando el usuario no es admin")
    void createCategory_whenNotAdmin_shouldReturnForbidden() {
        // Given
        Category newCategory = new Category(null, "Bicicletas", "Categoría para bicicletas");
        when(authUtils.isCurrentUserAdmin()).thenReturn(false);

        // When
        ResponseEntity<Category> response = categoryController.createCategory(newCategory);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
        verify(authUtils).isCurrentUserAdmin();
        verify(categoryService, never()).save(any(Category.class));
    }

    // ========== UPDATE CATEGORY TESTS ==========

    @Test
    @DisplayName("updateCategory - Debe actualizar categoría cuando el usuario es admin y existe")
    void updateCategory_whenAdminAndExists_shouldUpdateAndReturnCategory() {
        // Given
        Category categoryToUpdate = new Category(null, "Autos", "Descripción actualizada");
        Category updatedCategory = new Category(1L, "Autos", "Descripción actualizada");
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        when(categoryService.save(any(Category.class))).thenReturn(updatedCategory);

        // When
        ResponseEntity<Category> response = categoryController.updateCategory(1L, categoryToUpdate);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Category responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(updatedCategory, responseBody);
        verify(authUtils).isCurrentUserAdmin();
        verify(categoryService).save(any(Category.class));
    }

    @Test
    @DisplayName("updateCategory - Debe retornar FORBIDDEN cuando el usuario no es admin")
    void updateCategory_whenNotAdmin_shouldReturnForbidden() {
        // Given
        Category categoryToUpdate = new Category(null, "Autos", "Descripción actualizada");
        when(authUtils.isCurrentUserAdmin()).thenReturn(false);

        // When
        ResponseEntity<Category> response = categoryController.updateCategory(1L, categoryToUpdate);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
        verify(authUtils).isCurrentUserAdmin();
        verify(categoryService, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("updateCategory - Debe retornar NOT_FOUND cuando el usuario es admin pero la categoría no existe")
    void updateCategory_whenAdminAndNotExists_shouldReturnNotFound() {
        // Given
        Category categoryToUpdate = new Category(null, "Autos", "Descripción actualizada");
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        when(categoryService.save(any(Category.class))).thenThrow(new RuntimeException("Category not found"));

        // When
        ResponseEntity<Category> response = categoryController.updateCategory(99L, categoryToUpdate);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(authUtils).isCurrentUserAdmin();
        verify(categoryService).save(any(Category.class));
    }

    // ========== DELETE CATEGORY TESTS ==========

    @Test
    @DisplayName("deleteCategory - Debe eliminar categoría cuando el usuario es admin y existe")
    void deleteCategory_whenAdminAndExists_shouldDeleteAndReturnNoContent() {
        // Given
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);

        // When
        ResponseEntity<Void> response = categoryController.deleteCategory(1L);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(authUtils).isCurrentUserAdmin();
        verify(categoryService).delete(1L);
    }

    @Test
    @DisplayName("deleteCategory - Debe retornar FORBIDDEN cuando el usuario no es admin")
    void deleteCategory_whenNotAdmin_shouldReturnForbidden() {
        // Given
        when(authUtils.isCurrentUserAdmin()).thenReturn(false);

        // When
        ResponseEntity<Void> response = categoryController.deleteCategory(1L);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
        verify(authUtils).isCurrentUserAdmin();
        verify(categoryService, never()).delete(anyLong());
    }

    @Test
    @DisplayName("deleteCategory - Debe retornar NOT_FOUND cuando el usuario es admin pero la categoría no existe")
    void deleteCategory_whenAdminAndNotExists_shouldReturnNotFound() {
        // Given
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        doThrow(new RuntimeException("Category not found")).when(categoryService).delete(99L);

        // When
        ResponseEntity<Void> response = categoryController.deleteCategory(99L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(authUtils).isCurrentUserAdmin();
        verify(categoryService).delete(99L);
    }
}