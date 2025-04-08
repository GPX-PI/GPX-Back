package com.udea.GPX;

import com.udea.GPX.controller.CategoryController;
import com.udea.GPX.model.Category;
import com.udea.GPX.service.CategoryService;
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
import static org.mockito.Mockito.when;

@SpringBootTest
class CategoryControllerTests {

    @Mock
    private CategoryService categoryService;

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
}