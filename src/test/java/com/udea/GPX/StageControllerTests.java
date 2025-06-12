package com.udea.gpx;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

import com.udea.gpx.controller.StageController;
import com.udea.gpx.model.Event;
import com.udea.gpx.model.Stage;
import com.udea.gpx.model.User;
import com.udea.gpx.service.StageService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("StageController Unit Tests")
class StageControllerTests {

    @Mock
    private StageService stageService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private StageController stageController;

    private User adminUser;
    private User regularUser;
    private Event testEvent;
    private Stage testStage1;
    private Stage testStage2;

    @BeforeEach
    void setUp() {
        // Setup admin user
        adminUser = createUser(1L, "admin@test.com", "Admin", "User", true);

        // Setup regular user
        regularUser = createUser(2L, "user@test.com", "Regular", "User", false);

        // Setup test event
        testEvent = createEvent(1L, "Test Event");

        // Setup test stages
        testStage1 = createStage(1L, "Stage 1", 1, false, testEvent);
        testStage2 = createStage(2L, "Stage 2", 2, true, testEvent);
    }

    // Helper methods
    private User createUser(Long id, String email, String firstName, String lastName, boolean isAdmin) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAdmin(isAdmin);
        return user;
    }

    private Event createEvent(Long id, String name) {
        Event event = new Event();
        event.setId(id);
        event.setName(name);
        return event;
    }

    private Stage createStage(Long id, String name, int orderNumber, boolean isNeutralized, Event event) {
        Stage stage = new Stage();
        stage.setId(id);
        stage.setName(name);
        stage.setOrderNumber(orderNumber);
        stage.setNeutralized(isNeutralized);
        stage.setEvent(event);
        return stage;
    }

    // ========== GET ALL STAGES TESTS ==========

    @Test
    @DisplayName("getAllStages - Debe retornar OK con lista de stages")
    void getAllStages_shouldReturnOK() {
        // Given
        List<Stage> stages = Arrays.asList(testStage1, testStage2);
        when(stageService.getAllStages()).thenReturn(stages);

        // When
        ResponseEntity<List<Stage>> response = stageController.getAllStages();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Stage 1", response.getBody().get(0).getName());
        assertEquals("Stage 2", response.getBody().get(1).getName());
        verify(stageService).getAllStages();
    }

    // ========== GET STAGE BY ID TESTS ==========

    @Test
    @DisplayName("getStageById - Debe retornar OK cuando el stage existe")
    void getStageById_whenStageExists_shouldReturnOK() {
        // Given
        Long stageId = 1L;
        when(stageService.getStageById(stageId)).thenReturn(Optional.of(testStage1));

        // When
        ResponseEntity<Stage> response = stageController.getStageById(stageId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Stage 1", response.getBody().getName());
        assertEquals(1, response.getBody().getOrderNumber());
        verify(stageService).getStageById(stageId);
    }

    @Test
    @DisplayName("getStageById - Debe retornar NOT_FOUND cuando el stage no existe")
    void getStageById_whenStageNotExists_shouldReturnNotFound() {
        // Given
        Long stageId = 999L;
        when(stageService.getStageById(stageId)).thenReturn(Optional.empty());

        // When
        ResponseEntity<Stage> response = stageController.getStageById(stageId);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(stageService).getStageById(stageId);
    }

    // ========== GET STAGES BY EVENT ID TESTS ==========

    @Test
    @DisplayName("getStagesByEventId - Debe retornar OK con lista de stages del evento")
    void getStagesByEventId_shouldReturnOK() {
        // Given
        Long eventId = 1L;
        List<Stage> stages = Arrays.asList(testStage1, testStage2);
        when(stageService.getStagesByEventId(eventId)).thenReturn(stages);

        // When
        ResponseEntity<List<Stage>> response = stageController.getStagesByEventId(eventId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(stageService).getStagesByEventId(eventId);
    }

    // ========== CREATE STAGE TESTS ==========

    @Test
    @DisplayName("createStage - Debe retornar CREATED cuando el usuario es admin")
    void createStage_whenAdmin_shouldReturnCreated() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            // Given
            Stage newStage = createStage(null, "New Stage", 3, false, testEvent);
            Stage savedStage = createStage(3L, "New Stage", 3, false, testEvent);

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(adminUser);
            when(stageService.createStage(newStage)).thenReturn(savedStage);

            // When
            ResponseEntity<Stage> response = stageController.createStage(newStage);

            // Then
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(savedStage, response.getBody());
            verify(stageService).createStage(newStage);
        }
    }

    @Test
    @DisplayName("createStage - Debe retornar FORBIDDEN cuando el usuario no es admin")
    void createStage_whenNotAdmin_shouldReturnForbidden() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            // Given
            Stage newStage = createStage(null, "New Stage", 3, false, testEvent);

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(regularUser);

            // When
            ResponseEntity<Stage> response = stageController.createStage(newStage);

            // Then
            assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
            assertNull(response.getBody());
            verify(stageService, never()).createStage(any(Stage.class));
        }
    }

    // ========== UPDATE STAGE TESTS ==========

    @Test
    @DisplayName("updateStage - Debe retornar OK cuando el usuario es admin y el stage existe")
    void updateStage_whenAdmin_shouldReturnOK() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            // Given
            Long stageId = 1L;
            Stage updatedStage = createStage(null, "Updated Stage", 1, true, testEvent);
            Stage savedStage = createStage(stageId, "Updated Stage", 1, true, testEvent);

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(adminUser);
            when(stageService.updateStage(stageId, updatedStage)).thenReturn(savedStage);

            // When
            ResponseEntity<Stage> response = stageController.updateStage(stageId, updatedStage);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(savedStage, response.getBody());
            verify(stageService).updateStage(stageId, updatedStage);
        }
    }

    @Test
    @DisplayName("updateStage - Debe retornar FORBIDDEN cuando el usuario no es admin")
    void updateStage_whenNotAdmin_shouldReturnForbidden() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            // Given
            Long stageId = 1L;
            Stage updatedStage = createStage(null, "Updated Stage", 1, true, testEvent);

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(regularUser);

            // When
            ResponseEntity<Stage> response = stageController.updateStage(stageId, updatedStage);

            // Then
            assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
            assertNull(response.getBody());
            verify(stageService, never()).updateStage(anyLong(), any(Stage.class));
        }
    }

    @Test
    @DisplayName("updateStage - Debe retornar NOT_FOUND cuando el usuario es admin pero el stage no existe")
    void updateStage_whenAdminAndNotFound_shouldReturnNotFound() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            // Given
            Long stageId = 999L;
            Stage updatedStage = createStage(null, "Updated Stage", 1, true, testEvent);

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(adminUser);
            when(stageService.updateStage(stageId, updatedStage)).thenThrow(new RuntimeException("Stage not found"));

            // When
            ResponseEntity<Stage> response = stageController.updateStage(stageId, updatedStage);

            // Then
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNull(response.getBody());
            verify(stageService).updateStage(stageId, updatedStage);
        }
    }

    // ========== DELETE STAGE TESTS ==========

    @Test
    @DisplayName("deleteStage - Debe retornar NO_CONTENT cuando el usuario es admin")
    void deleteStage_whenAdmin_shouldReturnNoContent() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            // Given
            Long stageId = 1L;

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(adminUser);

            // When
            ResponseEntity<Void> response = stageController.deleteStage(stageId);

            // Then
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            assertNull(response.getBody());
            verify(stageService).deleteStage(stageId);
        }
    }

    @Test
    @DisplayName("deleteStage - Debe retornar FORBIDDEN cuando el usuario no es admin")
    void deleteStage_whenNotAdmin_shouldReturnForbidden() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            // Given
            Long stageId = 1L;

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(regularUser);

            // When
            ResponseEntity<Void> response = stageController.deleteStage(stageId);

            // Then
            assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
            assertNull(response.getBody());
            verify(stageService, never()).deleteStage(anyLong());
        }
    }
}