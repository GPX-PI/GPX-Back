package com.udea.gpx;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

import com.udea.gpx.controller.CategoryController;
import com.udea.gpx.model.Category;
import com.udea.gpx.service.CategoryService;
import com.udea.gpx.util.AuthUtils;

import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CategoryControllerTests {

    @InjectMocks
    private CategoryController categoryController;

    @Mock
    private CategoryService categoryService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private AuthUtils authUtils;

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
        List<Category> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(2, responseBody.size());
        assertEquals("Autos", responseBody.get(0).getName());
        assertEquals("Motos", responseBody.get(1).getName());
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
        Category responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("Autos", responseBody.getName());
        assertEquals("Categoría para autos", responseBody.getDetails());
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
        Category responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(savedCategory, responseBody);
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
        Category responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(updatedCategory, responseBody);
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