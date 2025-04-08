package com.udea.GPX;

import com.udea.GPX.controller.EventCategoryController;
import com.udea.GPX.model.Category;
import com.udea.GPX.model.Event;
import com.udea.GPX.model.EventCategory;
import com.udea.GPX.service.EventCategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@SpringBootTest
public class EventCategoryControllerTests {

    @InjectMocks
    private EventCategoryController eventCategoryController;

    @Mock
    private EventCategoryService eventCategoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllEventCategories_shouldReturnOK() {
        // Arrange
        List<EventCategory> eventCategories = new ArrayList<>();
        EventCategory eventCategory1 = new EventCategory();
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Category 1");
        eventCategory1.setCategory(category1);

        EventCategory eventCategory2 = new EventCategory();
        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Category 2");
        eventCategory2.setCategory(category2);

        eventCategories.add(eventCategory1);
        eventCategories.add(eventCategory2);

        when(eventCategoryService.getAll()).thenReturn(eventCategories);

        // Act
        ResponseEntity<List<EventCategory>> response = eventCategoryController.getAllEventCategories();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    void getEventCategoryById_whenCategoryExists_shouldReturnOK() {
        // Arrange
        Long categoryId = 1L;
        EventCategory eventCategory = new EventCategory();
        Category category = new Category();
        Event event = new Event();
        category.setId(categoryId);
        category.setName("Category 1");
        eventCategory.setCategory(category);
        eventCategory.setEvent(event);
        eventCategory.setId(1L);

        when(eventCategoryService.getById(categoryId)).thenReturn(eventCategory);

        // Act
        ResponseEntity<EventCategory> response = eventCategoryController.getEventCategoryById(categoryId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Category 1", Objects.requireNonNull(response.getBody()).getCategory().getName());
    }

    @Test
    void getEventCategoryById_whenCategoryNotExists_shouldReturnNotFound() {
        // Arrange
        Long categoryId = 1L;

        when(eventCategoryService.getEventCategoryById(categoryId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<EventCategory> response = eventCategoryController.getEventCategoryById(categoryId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}