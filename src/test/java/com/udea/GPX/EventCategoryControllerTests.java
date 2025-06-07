package com.udea.GPX;

import com.udea.GPX.controller.EventCategoryController;
import com.udea.GPX.model.Category;
import com.udea.GPX.model.Event;
import com.udea.GPX.model.EventCategory;
import com.udea.GPX.model.User;
import com.udea.GPX.service.EventCategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@SpringBootTest
@Disabled("No se ejecuta en la CI/CD")

public class EventCategoryControllerTests {

    @InjectMocks
    private EventCategoryController eventCategoryController;

    @Mock
    private EventCategoryService eventCategoryService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private User buildUser(Long id, boolean admin) {
        return new User(
                id,
                "TestUser",
                "TestLastName",
                "123456789",
                "3101234567",
                admin,
                "test@ejemplo.com",
                "piloto",
                LocalDate.of(1990, 1, 1),
                "CC",
                "EquipoTest",
                "EPSX",
                "O+",
                "3000000000",
                "Ninguna",
                "wikilocUser",
                "SeguroX",
                "terrapirataUser",
                "instaUser",
                "fbUser");
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        ReflectionTestUtils.setField(eventCategoryController, "request", request);
    }

    @Test
    void getAllEventCategories_whenAdmin_shouldReturnOK() {
        // Arrange
        User adminUser = buildUser(1L, true);
        when(authentication.getPrincipal()).thenReturn(adminUser);
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
    void getAllEventCategories_whenNotAdmin_shouldReturnForbidden() {
        // Arrange
        User nonAdminUser = buildUser(1L, false);
        when(authentication.getPrincipal()).thenReturn(nonAdminUser);

        // Act
        ResponseEntity<List<EventCategory>> response = eventCategoryController.getAllEventCategories();

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getEventCategoryById_whenCategoryExists_shouldReturnOK() {
        // Arrange
        Long categoryId = 1L;
        User adminUser = buildUser(1L, true);
        when(authentication.getPrincipal()).thenReturn(adminUser);

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
        User adminUser = buildUser(1L, true);
        when(authentication.getPrincipal()).thenReturn(adminUser);

        when(eventCategoryService.getById(categoryId)).thenReturn(null);

        // Act
        ResponseEntity<EventCategory> response = eventCategoryController.getEventCategoryById(categoryId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getEventCategoryById_whenNotAdmin_shouldReturnForbidden() {
        // Arrange
        Long categoryId = 1L;
        User nonAdminUser = buildUser(1L, false);
        when(authentication.getPrincipal()).thenReturn(nonAdminUser);

        // Act
        ResponseEntity<EventCategory> response = eventCategoryController.getEventCategoryById(categoryId);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }
}