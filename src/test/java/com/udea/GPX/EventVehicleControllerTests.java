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
    } // ========== DELETE EVENT VEHICLE TESTS ==========

    @Test
    @DisplayName("deleteEventVehicle - Debe retornar NO_CONTENT cuando el usuario es admin")
    void deleteEventVehicle_whenAdmin_shouldReturnNoContent() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            // Given
            Long eventVehicleId = 1L;

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn(adminUser); // adminUser es instancia real de User
            when(eventVehicleService.getEventVehicleById(eventVehicleId)).thenReturn(Optional.of(testEventVehicle));

            // When
            ResponseEntity<Void> response = eventVehicleController.deleteEventVehicle(eventVehicleId);

            // Then
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            verify(eventVehicleService).getEventVehicleById(eventVehicleId);
        }
    }

    @Test
    @DisplayName("deleteEventVehicle - Debe retornar FORBIDDEN cuando el usuario no es dueño ni admin")
    void deleteEventVehicle_whenNotOwnerNorAdmin_shouldReturnForbidden() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            // Given
            User otherUser = createUser(99L, "other@test.com", "Other", "User", false);
            Long eventVehicleId = 1L;
            EventVehicle eventVehicle = createEventVehicle(eventVehicleId, testEvent, testVehicle);

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn(otherUser); // otro usuario real
            when(eventVehicleService.getEventVehicleById(eventVehicleId)).thenReturn(Optional.of(eventVehicle));

            // When
            ResponseEntity<Void> response = eventVehicleController.deleteEventVehicle(eventVehicleId);

            // Then
            assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        }
    }

    @Test
    @DisplayName("deleteEventVehicle - Debe retornar NO_CONTENT cuando el usuario es dueño")
    void deleteEventVehicle_whenOwner_shouldReturnNoContent() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            // Given
            Long eventVehicleId = 1L;
            EventVehicle eventVehicle = createEventVehicle(eventVehicleId, testEvent, testVehicle);

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn(regularUser); // regularUser es instancia real de User
            when(eventVehicleService.getEventVehicleById(eventVehicleId)).thenReturn(Optional.of(eventVehicle));

            // When
            ResponseEntity<Void> response = eventVehicleController.deleteEventVehicle(eventVehicleId);

            // Then
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        }
    }

    @Test
    @DisplayName("deleteEventVehicle - Debe retornar NOT_FOUND cuando el registro no existe")
    void deleteEventVehicle_whenNotFound_shouldReturnNotFound() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            // Given
            Long eventVehicleId = 1L;

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn(adminUser); // adminUser es instancia real de User
            when(eventVehicleService.getEventVehicleById(eventVehicleId)).thenReturn(Optional.empty());

            // When
            ResponseEntity<Void> response = eventVehicleController.deleteEventVehicle(eventVehicleId);

            // Then
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
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
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn(oauth2User);
            when(oauth2User.getAttribute("email")).thenReturn(email);
            when(userService.findByEmail(email)).thenReturn(adminUser);
            // Aseguramos que adminUser.isAdmin() devuelva true
            assertTrue(adminUser.isAdmin(), "El usuario adminUser debe ser admin para este test");
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
    @DisplayName("createEventVehicle - Debe retornar UNAUTHORIZED cuando no hay usuario autenticado")
    void createEventVehicle_shouldReturnUnauthorized_whenUserNotAuthenticated() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            // Given
            EventVehicle newEventVehicle = new EventVehicle();
            newEventVehicle.setVehicleId(testVehicle);
            newEventVehicle.setEvent(testEvent);

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(null);

            // When
            ResponseEntity<?> response = eventVehicleController.createEventVehicle(newEventVehicle);

            // Then
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        }
    }
}