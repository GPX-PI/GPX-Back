package com.udea.GPX;

import com.udea.GPX.controller.EventCategoryController;
import com.udea.GPX.model.Category;
import com.udea.GPX.model.Event;
import com.udea.GPX.model.EventCategory;
import com.udea.GPX.model.User;
import com.udea.GPX.service.EventCategoryService;
import org.junit.jupiter.api.BeforeEach;
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
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@SpringBootTest
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

    private User buildUser(boolean admin) {
        return new User(
                1L,
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
        User adminUser = buildUser(true);
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
        User nonAdminUser = buildUser(false);
        when(authentication.getPrincipal()).thenReturn(nonAdminUser);

        // Act
        ResponseEntity<List<EventCategory>> response = eventCategoryController.getAllEventCategories();

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void createEventCategory_whenAdmin_shouldReturnCreated() {
        // Arrange
        User adminUser = buildUser(true);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        EventCategory eventCategory = new EventCategory();
        when(eventCategoryService.save(eventCategory)).thenReturn(eventCategory);

        // Act
        ResponseEntity<EventCategory> response = eventCategoryController.createEventCategory(eventCategory);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(eventCategory, response.getBody());
    }

    @Test
    void createEventCategory_whenNotAdmin_shouldReturnForbidden() {
        // Arrange
        User nonAdminUser = buildUser(false);
        when(authentication.getPrincipal()).thenReturn(nonAdminUser);
        EventCategory eventCategory = new EventCategory();

        // Act
        ResponseEntity<EventCategory> response = eventCategoryController.createEventCategory(eventCategory);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void deleteEventCategory_whenAdmin_shouldReturnNoContent() {
        // Arrange
        User adminUser = buildUser(true);
        when(authentication.getPrincipal()).thenReturn(adminUser);

        // Act
        ResponseEntity<Void> response = eventCategoryController.deleteEventCategory(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void deleteEventCategory_whenNotAdmin_shouldReturnForbidden() {
        // Arrange
        User nonAdminUser = buildUser(false);
        when(authentication.getPrincipal()).thenReturn(nonAdminUser);

        // Act
        ResponseEntity<Void> response = eventCategoryController.deleteEventCategory(1L);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void getEventCategoryById_whenAdminAndExists_shouldReturnOK() {
        // Arrange
        User adminUser = buildUser(true);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        EventCategory eventCategory = new EventCategory();
        when(eventCategoryService.getById(1L)).thenReturn(eventCategory);

        // Act
        ResponseEntity<EventCategory> response = eventCategoryController.getEventCategoryById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(eventCategory, response.getBody());
    }

    @Test
    void getEventCategoryById_whenAdminAndNotFound_shouldReturnNotFound() {
        // Arrange
        User adminUser = buildUser(true);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        when(eventCategoryService.getById(1L)).thenReturn(null);

        // Act
        ResponseEntity<EventCategory> response = eventCategoryController.getEventCategoryById(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getEventCategoryById_whenNotAdmin_shouldReturnForbidden() {
        // Arrange
        User nonAdminUser = buildUser(false);
        when(authentication.getPrincipal()).thenReturn(nonAdminUser);

        // Act
        ResponseEntity<EventCategory> response = eventCategoryController.getEventCategoryById(1L);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void getCategoriesByEventId_shouldReturnOK() {
        // Arrange
        List<EventCategory> eventCategories = new ArrayList<>();
        when(eventCategoryService.getByEventId(1L)).thenReturn(eventCategories);

        // Act
        ResponseEntity<List<EventCategory>> response = eventCategoryController.getCategoriesByEventId(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(eventCategories, response.getBody());
    }
}