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
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.udea.gpx.controller.EventVehicleController;
import com.udea.gpx.dto.ParticipantDTO;
import com.udea.gpx.model.Event;
import com.udea.gpx.model.EventVehicle;
import com.udea.gpx.model.User;
import com.udea.gpx.model.Vehicle;
import com.udea.gpx.model.Category;
import com.udea.gpx.service.EventVehicleService;
import com.udea.gpx.service.UserService;
import com.udea.gpx.service.VehicleService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventVehicleController Unit Tests")
class EventVehicleControllerTests {

    @Mock
    private EventVehicleService eventVehicleService;

    @Mock
    private UserService userService;

    @Mock
    private VehicleService vehicleService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private OAuth2User oauth2User;

    @InjectMocks
    private EventVehicleController eventVehicleController;

    private User adminUser;
    private User regularUser;
    private Vehicle testVehicle;
    private Event testEvent;
    private EventVehicle testEventVehicle;

    @BeforeEach
    void setUp() {
        // Setup admin user
        adminUser = createUser(1L, "admin@test.com", "Admin", "User", true);

        // Setup regular user
        regularUser = createUser(2L, "user@test.com", "Regular", "User", false);

        // Setup test vehicle
        testVehicle = createVehicle(1L, "Test Vehicle", "ABC123", regularUser);

        // Setup test event
        testEvent = createEvent(1L, "Test Event");

        // Setup test event vehicle
        testEventVehicle = createEventVehicle(1L, testEvent, testVehicle);
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

    private Vehicle createVehicle(Long id, String name, String plates, User user) {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(id);
        vehicle.setName(name);
        vehicle.setPlates(plates);
        vehicle.setUser(user);

        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");
        vehicle.setCategory(category);

        return vehicle;
    }

    private Event createEvent(Long id, String name) {
        Event event = new Event();
        event.setId(id);
        event.setName(name);
        return event;
    }

    private EventVehicle createEventVehicle(Long id, Event event, Vehicle vehicle) {
        EventVehicle eventVehicle = new EventVehicle();
        eventVehicle.setId(id);
        eventVehicle.setEvent(event);
        eventVehicle.setVehicleId(vehicle);
        return eventVehicle;
    } // ========== GET ALL EVENT VEHICLES TESTS ==========

    @Test
    @DisplayName("getAllEventVehicles - Debe retornar OK cuando el usuario es admin")
    void getAllEventVehicles_whenAdmin_shouldReturnOK() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            // Given
            List<EventVehicle> eventVehicles = Arrays.asList(testEventVehicle,
                    createEventVehicle(2L, testEvent, testVehicle));

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(adminUser);
            when(eventVehicleService.getAllEventVehicles()).thenReturn(eventVehicles);

            // When
            ResponseEntity<List<EventVehicle>> response = eventVehicleController.getAllEventVehicles();

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(2, response.getBody().size());
            verify(eventVehicleService).getAllEventVehicles();
        }
    }

    @Test
    @DisplayName("getAllEventVehicles - Debe retornar FORBIDDEN cuando el usuario no es admin")
    void getAllEventVehicles_whenNotAdmin_shouldReturnForbidden() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            // Given
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(regularUser);

            // When
            ResponseEntity<List<EventVehicle>> response = eventVehicleController.getAllEventVehicles();

            // Then
            assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
            assertNull(response.getBody());
            verify(eventVehicleService, never()).getAllEventVehicles();
        }
    }

    // ========== GET EVENT VEHICLE BY ID TESTS ==========

    @Test
    @DisplayName("getEventVehicleById - Debe retornar OK cuando el usuario es admin")
    void getEventVehicleById_whenAdmin_shouldReturnOK() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            // Given
            Long eventVehicleId = 1L;

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(adminUser);
            when(eventVehicleService.getEventVehicleById(eventVehicleId)).thenReturn(Optional.of(testEventVehicle));

            // When
            ResponseEntity<EventVehicle> response = eventVehicleController.getEventVehicleById(eventVehicleId);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(eventVehicleId, response.getBody().getId());
            verify(eventVehicleService).getEventVehicleById(eventVehicleId);
        }
    }

    @Test
    @DisplayName("getEventVehicleById - Debe retornar OK cuando el usuario es propietario del vehículo")
    void getEventVehicleById_whenOwner_shouldReturnOK() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            // Given
            Long eventVehicleId = 1L;

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(regularUser);
            when(eventVehicleService.getEventVehicleById(eventVehicleId)).thenReturn(Optional.of(testEventVehicle));

            // When
            ResponseEntity<EventVehicle> response = eventVehicleController.getEventVehicleById(eventVehicleId);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(eventVehicleId, response.getBody().getId());
            verify(eventVehicleService).getEventVehicleById(eventVehicleId);
        }
    }

    @Test
    @DisplayName("getEventVehicleById - Debe retornar FORBIDDEN cuando el usuario no es propietario ni admin")
    void getEventVehicleById_whenNotOwnerNorAdmin_shouldReturnForbidden() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            // Given
            Long eventVehicleId = 1L;
            User otherUser = createUser(99L, "other@test.com", "Other", "User", false);

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(otherUser);
            when(eventVehicleService.getEventVehicleById(eventVehicleId)).thenReturn(Optional.of(testEventVehicle));

            // When
            ResponseEntity<EventVehicle> response = eventVehicleController.getEventVehicleById(eventVehicleId);

            // Then
            assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
            verify(eventVehicleService).getEventVehicleById(eventVehicleId);
        }
    }

    @Test
    @DisplayName("getEventVehicleById - Debe retornar NOT_FOUND cuando el EventVehicle no existe")
    void getEventVehicleById_whenNotFound_shouldReturnNotFound() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            // Given
            Long eventVehicleId = 999L;

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(adminUser);
            when(eventVehicleService.getEventVehicleById(eventVehicleId)).thenReturn(Optional.empty());

            // When
            ResponseEntity<EventVehicle> response = eventVehicleController.getEventVehicleById(eventVehicleId);

            // Then
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            verify(eventVehicleService).getEventVehicleById(eventVehicleId);
        }
    }

    // ========== GET VEHICLES BY EVENT ID TESTS ==========

    @Test
    @DisplayName("getVehiclesByEventId - Debe retornar OK sin verificar autorización")
    void getVehiclesByEventId_shouldReturnOK() {
        // Given
        Long eventId = 1L;
        List<EventVehicle> eventVehicles = Arrays.asList(testEventVehicle,
                createEventVehicle(2L, testEvent, testVehicle));
        when(eventVehicleService.getVehiclesByEventId(eventId)).thenReturn(eventVehicles);

        // When
        ResponseEntity<List<EventVehicle>> response = eventVehicleController.getVehiclesByEventId(eventId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(eventVehicleService).getVehiclesByEventId(eventId);
    }

    // ========== GET EVENT PARTICIPANTS TESTS ==========

    @Test
    @DisplayName("getEventParticipants - Debe retornar lista de participantes exitosamente")
    void getEventParticipants_shouldReturnParticipantsSuccessfully() {
        // Given
        Long eventId = 1L;
        List<EventVehicle> eventVehicles = Arrays.asList(testEventVehicle);
        when(eventVehicleService.getVehiclesByEventId(eventId)).thenReturn(eventVehicles);

        // When
        ResponseEntity<List<ParticipantDTO>> response = eventVehicleController.getEventParticipants(eventId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        ParticipantDTO participant = response.getBody().get(0);
        assertEquals(testEventVehicle.getId(), participant.getEventVehicleId());
        assertEquals(regularUser.getId(), participant.getUserId());
        assertEquals(regularUser.getFirstName() + " " + regularUser.getLastName(), participant.getUserName());
        verify(eventVehicleService).getVehiclesByEventId(eventId);
    }

    @Test
    @DisplayName("getEventParticipants - Debe retornar INTERNAL_SERVER_ERROR cuando hay excepción")
    void getEventParticipants_whenException_shouldReturnInternalServerError() {
        // Given
        Long eventId = 1L;
        when(eventVehicleService.getVehiclesByEventId(eventId)).thenThrow(new RuntimeException("Database error"));

        // When
        ResponseEntity<List<ParticipantDTO>> response = eventVehicleController.getEventParticipants(eventId);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(eventVehicleService).getVehiclesByEventId(eventId);
    } // ========== CREATE EVENT VEHICLE TESTS ==========

    @Test
    @DisplayName("createEventVehicle - Debe retornar CREATED cuando el usuario es propietario y no está registrado")
    void createEventVehicle_whenUserOwnsVehicleAndNotRegistered_shouldReturnCreated() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            // Given
            EventVehicle newEventVehicle = new EventVehicle();
            newEventVehicle.setVehicleId(testVehicle);
            newEventVehicle.setEvent(testEvent);

            EventVehicle savedEventVehicle = createEventVehicle(10L, testEvent, testVehicle);

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(regularUser);
            when(vehicleService.getVehicleById(testVehicle.getId())).thenReturn(Optional.of(testVehicle));
            when(eventVehicleService.getVehiclesByEventId(testEvent.getId())).thenReturn(List.of()); // No registrations
            when(eventVehicleService.createEventVehicle(any(EventVehicle.class))).thenReturn(savedEventVehicle);

            // When
            ResponseEntity<?> response = eventVehicleController.createEventVehicle(newEventVehicle);

            // Then
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertEquals(savedEventVehicle, response.getBody());
            verify(vehicleService).getVehicleById(testVehicle.getId());
            verify(eventVehicleService).getVehiclesByEventId(testEvent.getId());
            verify(eventVehicleService).createEventVehicle(any(EventVehicle.class));
        }
    }

    @Test
    @DisplayName("createEventVehicle - Debe retornar NOT_FOUND cuando el vehículo no existe")
    void createEventVehicle_whenVehicleNotFound_shouldReturnNotFound() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            // Given
            EventVehicle newEventVehicle = new EventVehicle();
            newEventVehicle.setVehicleId(testVehicle);
            newEventVehicle.setEvent(testEvent);

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(regularUser);
            when(vehicleService.getVehicleById(testVehicle.getId())).thenReturn(Optional.empty());

            // When
            ResponseEntity<?> response = eventVehicleController.createEventVehicle(newEventVehicle);

            // Then
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNotNull(response.getBody());

            @SuppressWarnings("unchecked")
            Map<String, String> errorResponse = (Map<String, String>) response.getBody();
            assertEquals("Vehículo no encontrado", errorResponse.get("error"));
            verify(vehicleService).getVehicleById(testVehicle.getId());
        }
    }

    @Test
    @DisplayName("createEventVehicle - Debe retornar FORBIDDEN cuando el usuario no es propietario ni admin")
    void createEventVehicle_whenUserNotOwnerAndNotAdmin_shouldReturnForbidden() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            // Given
            User otherUser = createUser(99L, "other@test.com", "Other", "User", false);
            EventVehicle newEventVehicle = new EventVehicle();
            newEventVehicle.setVehicleId(testVehicle);
            newEventVehicle.setEvent(testEvent);

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(otherUser);
            when(vehicleService.getVehicleById(testVehicle.getId())).thenReturn(Optional.of(testVehicle));

            // When
            ResponseEntity<?> response = eventVehicleController.createEventVehicle(newEventVehicle);

            // Then
            assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
            assertNotNull(response.getBody());

            @SuppressWarnings("unchecked")
            Map<String, String> errorResponse = (Map<String, String>) response.getBody();
            assertEquals("Sin permisos", errorResponse.get("error"));
            verify(vehicleService).getVehicleById(testVehicle.getId());
        }
    }

    @Test
    @DisplayName("createEventVehicle - Debe retornar CONFLICT cuando el usuario ya está registrado")
    void createEventVehicle_whenUserAlreadyRegistered_shouldReturnConflict() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            // Given
            EventVehicle newEventVehicle = new EventVehicle();
            newEventVehicle.setVehicleId(testVehicle);
            newEventVehicle.setEvent(testEvent);

            List<EventVehicle> existingRegistrations = Arrays.asList(testEventVehicle); // User already registered

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(regularUser);
            when(vehicleService.getVehicleById(testVehicle.getId())).thenReturn(Optional.of(testVehicle));
            when(eventVehicleService.getVehiclesByEventId(testEvent.getId())).thenReturn(existingRegistrations);

            // When
            ResponseEntity<?> response = eventVehicleController.createEventVehicle(newEventVehicle);

            // Then
            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
            assertNotNull(response.getBody());

            @SuppressWarnings("unchecked")
            Map<String, String> errorResponse = (Map<String, String>) response.getBody();
            assertEquals("Usuario ya inscrito", errorResponse.get("error"));
            verify(vehicleService).getVehicleById(testVehicle.getId());
            verify(eventVehicleService).getVehiclesByEventId(testEvent.getId());
        }
    }

    @Test
    @DisplayName("createEventVehicle - Debe retornar INTERNAL_SERVER_ERROR cuando hay excepción inesperada")
    void createEventVehicle_whenUnexpectedError_shouldReturnInternalServerError() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            // Given
            EventVehicle newEventVehicle = new EventVehicle();
            newEventVehicle.setVehicleId(testVehicle);
            newEventVehicle.setEvent(testEvent);

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(regularUser);
            when(vehicleService.getVehicleById(testVehicle.getId())).thenThrow(new RuntimeException("Database error"));

            // When
            ResponseEntity<?> response = eventVehicleController.createEventVehicle(newEventVehicle);

            // Then
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertNotNull(response.getBody());

            @SuppressWarnings("unchecked")
            Map<String, String> errorResponse = (Map<String, String>) response.getBody();
            assertEquals("Error al procesar inscripción", errorResponse.get("error"));
            verify(vehicleService).getVehicleById(testVehicle.getId());
        }
    }

    // ========== DELETE EVENT VEHICLE TESTS ==========

    @Test
    @DisplayName("deleteEventVehicle - Debe retornar NO_CONTENT cuando el usuario es admin")
    void deleteEventVehicle_whenAdmin_shouldReturnNoContent() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            // Given
            Long eventVehicleId = 1L;

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(adminUser);
            when(eventVehicleService.getEventVehicleById(eventVehicleId)).thenReturn(Optional.of(testEventVehicle));

            // When
            ResponseEntity<Void> response = eventVehicleController.deleteEventVehicle(eventVehicleId);

            // Then
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            verify(eventVehicleService).getEventVehicleById(eventVehicleId);
            verify(eventVehicleService).deleteEventVehicle(eventVehicleId);
        }
    }

    @Test
    @DisplayName("deleteEventVehicle - Debe retornar NO_CONTENT cuando el usuario es propietario")
    void deleteEventVehicle_whenOwner_shouldReturnNoContent() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            // Given
            Long eventVehicleId = 1L;

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(regularUser);
            when(eventVehicleService.getEventVehicleById(eventVehicleId)).thenReturn(Optional.of(testEventVehicle));

            // When
            ResponseEntity<Void> response = eventVehicleController.deleteEventVehicle(eventVehicleId);

            // Then
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            verify(eventVehicleService).getEventVehicleById(eventVehicleId);
            verify(eventVehicleService).deleteEventVehicle(eventVehicleId);
        }
    }

    @Test
    @DisplayName("deleteEventVehicle - Debe retornar FORBIDDEN cuando el usuario no es propietario ni admin")
    void deleteEventVehicle_whenNotOwnerNorAdmin_shouldReturnForbidden() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            // Given
            Long eventVehicleId = 1L;
            User otherUser = createUser(99L, "other@test.com", "Other", "User", false);

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(otherUser);
            when(eventVehicleService.getEventVehicleById(eventVehicleId)).thenReturn(Optional.of(testEventVehicle));

            // When
            ResponseEntity<Void> response = eventVehicleController.deleteEventVehicle(eventVehicleId);

            // Then
            assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
            verify(eventVehicleService).getEventVehicleById(eventVehicleId);
            verify(eventVehicleService, never()).deleteEventVehicle(eventVehicleId);
        }
    }

    @Test
    @DisplayName("deleteEventVehicle - Debe retornar NOT_FOUND cuando el EventVehicle no existe")
    void deleteEventVehicle_whenNotFound_shouldReturnNotFound() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            // Given
            Long eventVehicleId = 999L;

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(adminUser);
            when(eventVehicleService.getEventVehicleById(eventVehicleId)).thenReturn(Optional.empty());

            // When
            ResponseEntity<Void> response = eventVehicleController.deleteEventVehicle(eventVehicleId);

            // Then
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            verify(eventVehicleService).getEventVehicleById(eventVehicleId);
            verify(eventVehicleService, never()).deleteEventVehicle(eventVehicleId);
        }
    }

    // ========== OAUTH2 AUTHENTICATION TESTS ==========

    @Test
    @DisplayName("getAllEventVehicles - Debe funcionar con autenticación OAuth2")
    void getAllEventVehicles_withOAuth2User_shouldReturnOK() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            // Given
            String email = "admin@test.com";
            List<EventVehicle> eventVehicles = Arrays.asList(testEventVehicle);

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(oauth2User);
            when(oauth2User.getAttribute("email")).thenReturn(email);
            when(userService.findByEmail(email)).thenReturn(adminUser);
            when(eventVehicleService.getAllEventVehicles()).thenReturn(eventVehicles);

            // When
            ResponseEntity<List<EventVehicle>> response = eventVehicleController.getAllEventVehicles();

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(1, response.getBody().size());
            verify(userService).findByEmail(email);
            verify(eventVehicleService).getAllEventVehicles();
        }
    }

    @Test
    @DisplayName("getAuthenticatedUser - Debe lanzar excepción cuando OAuth2 user no existe en BD")
    void getAuthenticatedUser_whenOAuth2UserNotFoundInDB_shouldThrowException() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            // Given
            String email = "nonexistent@test.com";

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(oauth2User);
            when(oauth2User.getAttribute("email")).thenReturn(email);
            when(userService.findByEmail(email)).thenReturn(null);

            // When & Then
            assertThrows(RuntimeException.class, () -> {
                eventVehicleController.getAllEventVehicles();
            });

            verify(userService).findByEmail(email);
        }
    }

    @Test
    @DisplayName("getAuthenticatedUser - Debe lanzar excepción cuando el principal no es válido")
    void getAuthenticatedUser_whenInvalidPrincipal_shouldThrowException() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            // Given
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn("invalid-principal");

            // When & Then
            assertThrows(RuntimeException.class, () -> {
                eventVehicleController.getAllEventVehicles();
            });
        }
    }
}