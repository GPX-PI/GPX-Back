package com.udea.GPX;

import com.udea.GPX.controller.CategoryController;
import com.udea.GPX.model.Category;
import com.udea.GPX.service.CategoryService;
import com.udea.GPX.util.AuthUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class CategoryControllerTests {

    @Mock
    private CategoryService categoryService;

    @Mock
    private AuthUtils authUtils;

    @InjectMocks
    private CategoryController categoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllCategories_shouldReturnAllCategories() {
        // Preparar datos de prueba
        Category category1 = new Category(1L, "Autos", "Categoría para autos");
        Category category2 = new Category(2L, "Motos", "Categoría para motos");
        List<Category> categories = Arrays.asList(category1, category2);

        when(categoryService.getAllCategories()).thenReturn(categories);

        // Ejecutar
        ResponseEntity<List<Category>> response = categoryController.getAllCategories();

        // Verificar
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
        assertEquals("Autos", response.getBody().get(0).getName());
        assertEquals("Motos", response.getBody().get(1).getName());
    }

    @Test
    void getCategoryById_whenCategoryExists_shouldReturnCategory() {
        // Preparar datos
        Category category = new Category(1L, "Autos", "Categoría para autos");

        when(categoryService.getCategoryById(1L)).thenReturn(category);

        // Ejecutar
        ResponseEntity<Category> response = categoryController.getCategoryById(1L);

        // Verificar
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Autos", Objects.requireNonNull(response.getBody()).getName());
        assertEquals("Categoría para autos", response.getBody().getDetails());
    }

    @Test
    void getCategoryById_whenCategoryNotExists_shouldReturn404() {
        when(categoryService.getCategoryById(99L)).thenReturn(null);

        // Ejecutar
        ResponseEntity<Category> response = categoryController.getCategoryById(99L);

        // Verificar
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void createCategory_whenAdmin_shouldCreateAndReturnCategory() {
        Category category = new Category(null, "Bicicletas", "Categoría para bicicletas");
        Category savedCategory = new Category(3L, "Bicicletas", "Categoría para bicicletas");
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        when(categoryService.save(category)).thenReturn(savedCategory);

        ResponseEntity<Category> response = categoryController.createCategory(category);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(savedCategory, response.getBody());
    }

    @Test
    void createCategory_whenNotAdmin_shouldReturnForbidden() {
        Category category = new Category(null, "Bicicletas", "Categoría para bicicletas");
        when(authUtils.isCurrentUserAdmin()).thenReturn(false);

        ResponseEntity<Category> response = categoryController.createCategory(category);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void updateCategory_whenAdminAndExists_shouldUpdateAndReturnCategory() {
        Category category = new Category(null, "Autos", "Actualizado");
        Category updatedCategory = new Category(1L, "Autos", "Actualizado");
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        when(categoryService.save(any(Category.class))).thenReturn(updatedCategory);

        ResponseEntity<Category> response = categoryController.updateCategory(1L, category);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedCategory, response.getBody());
    }

    @Test
    void updateCategory_whenNotAdmin_shouldReturnForbidden() {
        Category category = new Category(null, "Autos", "Actualizado");
        when(authUtils.isCurrentUserAdmin()).thenReturn(false);

        ResponseEntity<Category> response = categoryController.updateCategory(1L, category);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void updateCategory_whenAdminAndNotExists_shouldReturnNotFound() {
        Category category = new Category(null, "Autos", "Actualizado");
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        doThrow(new RuntimeException()).when(categoryService).save(org.mockito.ArgumentMatchers.any(Category.class));

        ResponseEntity<Category> response = categoryController.updateCategory(99L, category);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteCategory_whenAdminAndExists_shouldDeleteAndReturnNoContent() {
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        ResponseEntity<Void> response = categoryController.deleteCategory(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(categoryService).delete(1L);
    }

    @Test
    void deleteCategory_whenNotAdmin_shouldReturnForbidden() {
        when(authUtils.isCurrentUserAdmin()).thenReturn(false);
        ResponseEntity<Void> response = categoryController.deleteCategory(1L);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void deleteCategory_whenAdminAndNotExists_shouldReturnNotFound() {
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        doThrow(new RuntimeException()).when(categoryService).delete(99L);
        ResponseEntity<Void> response = categoryController.deleteCategory(99L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}