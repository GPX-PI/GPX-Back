package com.udea.gpx;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.udea.gpx.controller.EventCategoryController;
import com.udea.gpx.model.Category;
import com.udea.gpx.model.EventCategory;
import com.udea.gpx.model.User;
import com.udea.gpx.service.EventCategoryService;
import com.udea.gpx.util.AuthUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventCategoryController Unit Tests")
class EventCategoryControllerUnitTest {

    @Mock
    private EventCategoryService eventCategoryService;

    @Mock
    private AuthUtils authUtils;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private EventCategoryController eventCategoryController;

    private User adminUser;
    private User regularUser;
    private EventCategory testEventCategory;
    private List<EventCategory> testEventCategories;

    @BeforeEach
    void setUp() {
        // Setup admin user
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setEmail("admin@test.com");
        adminUser.setAdmin(true);

        // Setup regular user
        regularUser = new User();
        regularUser.setId(2L);
        regularUser.setEmail("user@test.com");
        regularUser.setAdmin(false);

        // Setup test event category
        testEventCategory = new EventCategory();
        testEventCategory.setId(1L);
        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");
        testEventCategory.setCategory(category);

        // Setup test list
        testEventCategories = new ArrayList<>();
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

        testEventCategories.add(eventCategory1);
        testEventCategories.add(eventCategory2);
    }

    // ========== GET ALL EVENT CATEGORIES TESTS ==========

    @Test
    @DisplayName("getAllEventCategories - Debe retornar OK cuando el usuario es admin")
    void getAllEventCategories_whenAdmin_shouldReturnOK() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            // Given
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(adminUser);
            when(eventCategoryService.getAll()).thenReturn(testEventCategories);

            // When
            ResponseEntity<List<EventCategory>> response = eventCategoryController.getAllEventCategories();

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(2, response.getBody().size());
            verify(eventCategoryService).getAll();
        }
    }

    @Test
    @DisplayName("getAllEventCategories - Debe retornar FORBIDDEN cuando el usuario no es admin")
    void getAllEventCategories_whenNotAdmin_shouldReturnForbidden() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            // Given
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(regularUser);

            // When
            ResponseEntity<List<EventCategory>> response = eventCategoryController.getAllEventCategories();

            // Then
            assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
            assertNull(response.getBody());
            verify(eventCategoryService, never()).getAll();
        }
    }

    // ========== CREATE EVENT CATEGORY TESTS ==========

    @Test
    @DisplayName("createEventCategory - Debe retornar CREATED cuando el usuario es admin")
    void createEventCategory_whenAdmin_shouldReturnCreated() {
        // Given
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        when(eventCategoryService.save(testEventCategory)).thenReturn(testEventCategory);

        // When
        ResponseEntity<EventCategory> response = eventCategoryController.createEventCategory(testEventCategory);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testEventCategory, response.getBody());
        verify(authUtils).isCurrentUserAdmin();
        verify(eventCategoryService).save(testEventCategory);
    }

    @Test
    @DisplayName("createEventCategory - Debe retornar FORBIDDEN cuando el usuario no es admin")
    void createEventCategory_whenNotAdmin_shouldReturnForbidden() {
        // Given
        when(authUtils.isCurrentUserAdmin()).thenReturn(false);

        // When
        ResponseEntity<EventCategory> response = eventCategoryController.createEventCategory(testEventCategory);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
        verify(authUtils).isCurrentUserAdmin();
        verify(eventCategoryService, never()).save(any());
    }

    // ========== DELETE EVENT CATEGORY TESTS ==========

    @Test
    @DisplayName("deleteEventCategory - Debe retornar NO_CONTENT cuando el usuario es admin")
    void deleteEventCategory_whenAdmin_shouldReturnNoContent() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            // Given
            Long categoryId = 1L;
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(adminUser);

            // When
            ResponseEntity<Void> response = eventCategoryController.deleteEventCategory(categoryId);

            // Then
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            verify(eventCategoryService).delete(categoryId);
        }
    }

    @Test
    @DisplayName("deleteEventCategory - Debe retornar FORBIDDEN cuando el usuario no es admin")
    void deleteEventCategory_whenNotAdmin_shouldReturnForbidden() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            // Given
            Long categoryId = 1L;
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(regularUser);

            // When
            ResponseEntity<Void> response = eventCategoryController.deleteEventCategory(categoryId);

            // Then
            assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
            verify(eventCategoryService, never()).delete(any());
        }
    }

    // ========== GET EVENT CATEGORY BY ID TESTS ==========

    @Test
    @DisplayName("getEventCategoryById - Debe retornar OK cuando el usuario es admin y la categoría existe")
    void getEventCategoryById_whenAdminAndExists_shouldReturnOK() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            // Given
            Long categoryId = 1L;
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(adminUser);
            when(eventCategoryService.getById(categoryId)).thenReturn(testEventCategory);

            // When
            ResponseEntity<EventCategory> response = eventCategoryController.getEventCategoryById(categoryId);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(testEventCategory, response.getBody());
            verify(eventCategoryService).getById(categoryId);
        }
    }

    @Test
    @DisplayName("getEventCategoryById - Debe retornar NOT_FOUND cuando el usuario es admin y la categoría no existe")
    void getEventCategoryById_whenAdminAndNotExists_shouldReturnNotFound() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            // Given
            Long categoryId = 999L;
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(adminUser);
            when(eventCategoryService.getById(categoryId)).thenReturn(null);

            // When
            ResponseEntity<EventCategory> response = eventCategoryController.getEventCategoryById(categoryId);

            // Then
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            verify(eventCategoryService).getById(categoryId);
        }
    }

    @Test
    @DisplayName("getEventCategoryById - Debe retornar FORBIDDEN cuando el usuario no es admin")
    void getEventCategoryById_whenNotAdmin_shouldReturnForbidden() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            // Given
            Long categoryId = 1L;
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(regularUser);

            // When
            ResponseEntity<EventCategory> response = eventCategoryController.getEventCategoryById(categoryId);

            // Then
            assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
            assertNull(response.getBody());
            verify(eventCategoryService, never()).getById(any());
        }
    }

    // ========== GET CATEGORIES BY EVENT ID TESTS ==========

    @Test
    @DisplayName("getCategoriesByEventId - Debe retornar OK sin verificar autorización")
    void getCategoriesByEventId_shouldReturnOK() {
        // Given
        Long eventId = 1L;
        when(eventCategoryService.getByEventId(eventId)).thenReturn(testEventCategories);

        // When
        ResponseEntity<List<EventCategory>> response = eventCategoryController.getCategoriesByEventId(eventId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(eventCategoryService).getByEventId(eventId);
    }

    @Test
    @DisplayName("getCategoriesByEventId - Debe retornar lista vacía cuando no hay categorías")
    void getCategoriesByEventId_whenNoCategories_shouldReturnEmptyList() {
        // Given
        Long eventId = 999L;
        List<EventCategory> emptyList = new ArrayList<>();
        when(eventCategoryService.getByEventId(eventId)).thenReturn(emptyList);

        // When
        ResponseEntity<List<EventCategory>> response = eventCategoryController.getCategoriesByEventId(eventId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(eventCategoryService).getByEventId(eventId);
    }

    // ========== HELPER METHODS TESTS ==========

    @Test
    @DisplayName("Constructor - Debe crear instancia correctamente")
    void constructor_shouldCreateInstanceCorrectly() {
        // Given & When
        EventCategoryController controller = new EventCategoryController(eventCategoryService, authUtils);

        // Then
        assertNotNull(controller);
    }
}