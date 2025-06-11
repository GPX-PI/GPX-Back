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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import com.udea.gpx.controller.EventVehicleController;
import com.udea.gpx.model.Event;
import com.udea.gpx.model.EventVehicle;
import com.udea.gpx.model.User;
import com.udea.gpx.model.Vehicle;
import com.udea.gpx.service.EventService;
import com.udea.gpx.service.EventVehicleService;
import com.udea.gpx.service.VehicleService;
import com.udea.gpx.util.AuthUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EventVehicleControllerTests {

    @InjectMocks
    private EventVehicleController eventVehicleController;

    @Mock
    private EventVehicleService eventVehicleService;

    @Mock
    private EventService eventService;

    @Mock
    private VehicleService vehicleService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private AuthUtils authUtils;

    private User buildUser(Long id, boolean admin) {
        User user = new User();
        user.setId(id);
        user.setAdmin(admin);
        return user;
    }

    private Vehicle buildVehicleWithUser(Long userId, boolean admin) {
        Vehicle vehicle = new Vehicle();
        User user = buildUser(userId, admin);
        vehicle.setUser(user);
        return vehicle;
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        ReflectionTestUtils.setField(eventVehicleController, "request", request);
    }

    @Test
    void getAllEventVehicles_whenAdmin_shouldReturnOK() {
        // Arrange
        User adminUser = buildUser(1L, true);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        List<EventVehicle> eventVehicles = Arrays.asList(
                new EventVehicle(1L, new Event(), new Vehicle()),
                new EventVehicle(2L, new Event(), new Vehicle()));
        when(eventVehicleService.getAllEventVehicles()).thenReturn(eventVehicles);

        // Act
        ResponseEntity<List<EventVehicle>> response = eventVehicleController.getAllEventVehicles();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    void getAllEventVehicles_whenNotAdmin_shouldReturnForbidden() {
        // Arrange
        User nonAdminUser = buildUser(1L, false);
        when(authentication.getPrincipal()).thenReturn(nonAdminUser);

        // Act
        ResponseEntity<List<EventVehicle>> response = eventVehicleController.getAllEventVehicles();

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getEventVehicleById_whenAdmin_shouldReturnOK() {
        // Arrange
        Long eventVehicleId = 1L;
        User adminUser = buildUser(10L, true);
        Vehicle vehicle = buildVehicleWithUser(20L, false);
        EventVehicle eventVehicle = new EventVehicle(eventVehicleId, new Event(), vehicle);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        when(eventVehicleService.getEventVehicleById(eventVehicleId)).thenReturn(Optional.of(eventVehicle));

        // Act
        ResponseEntity<EventVehicle> response = eventVehicleController.getEventVehicleById(eventVehicleId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(eventVehicleId, Objects.requireNonNull(response.getBody()).getId());
    }

    @Test
    void getEventVehicleById_whenOwner_shouldReturnOK() {
        // Arrange
        Long eventVehicleId = 1L;
        User ownerUser = buildUser(20L, false);
        Vehicle vehicle = buildVehicleWithUser(20L, false);
        EventVehicle eventVehicle = new EventVehicle(eventVehicleId, new Event(), vehicle);
        when(authentication.getPrincipal()).thenReturn(ownerUser);
        when(eventVehicleService.getEventVehicleById(eventVehicleId)).thenReturn(Optional.of(eventVehicle));

        // Act
        ResponseEntity<EventVehicle> response = eventVehicleController.getEventVehicleById(eventVehicleId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(eventVehicleId, Objects.requireNonNull(response.getBody()).getId());
    }

    @Test
    void getEventVehicleById_whenNotOwnerNorAdmin_shouldReturnForbidden() {
        // Arrange
        Long eventVehicleId = 1L;
        User otherUser = buildUser(99L, false);
        Vehicle vehicle = buildVehicleWithUser(20L, false);
        EventVehicle eventVehicle = new EventVehicle(eventVehicleId, new Event(), vehicle);
        when(authentication.getPrincipal()).thenReturn(otherUser);
        when(eventVehicleService.getEventVehicleById(eventVehicleId)).thenReturn(Optional.of(eventVehicle));

        // Act
        ResponseEntity<EventVehicle> response = eventVehicleController.getEventVehicleById(eventVehicleId);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void getEventVehicleById_whenNotFound_shouldReturnNotFound() {
        // Arrange
        Long eventVehicleId = 1L;
        User adminUser = buildUser(10L, true);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        when(eventVehicleService.getEventVehicleById(eventVehicleId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<EventVehicle> response = eventVehicleController.getEventVehicleById(eventVehicleId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getVehiclesByEventId_whenAdmin_shouldReturnOK() {
        // Arrange
        Long eventId = 1L;
        List<EventVehicle> eventVehicles = Arrays.asList(
                new EventVehicle(1L, new Event(), new Vehicle()),
                new EventVehicle(2L, new Event(), new Vehicle()));
        when(eventVehicleService.getVehiclesByEventId(eventId)).thenReturn(eventVehicles);

        // Act
        ResponseEntity<List<EventVehicle>> response = eventVehicleController.getVehiclesByEventId(eventId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    void createEventVehicle_whenUserOwnsVehicleAndNotRegistered_shouldReturnCreated() {
        // Arrange
        User user = buildUser(1L, false);
        Vehicle vehicle = buildVehicleWithUser(1L, false);
        Event event = new Event();
        event.setId(100L);
        EventVehicle eventVehicle = new EventVehicle();
        eventVehicle.setVehicleId(vehicle);
        eventVehicle.setEvent(event);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(user);
        when(vehicleService.getVehicleById(vehicle.getId())).thenReturn(Optional.of(vehicle));
        when(eventVehicleService.getVehiclesByEventId(event.getId())).thenReturn(List.of());
        EventVehicle savedEventVehicle = new EventVehicle(10L, event, vehicle);
        when(eventVehicleService.createEventVehicle(eventVehicle)).thenReturn(savedEventVehicle);

        // Act
        ResponseEntity<?> response = eventVehicleController.createEventVehicle(eventVehicle);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(savedEventVehicle, response.getBody());
    }

    @Test
    void createEventVehicle_whenVehicleNotFound_shouldReturnNotFound() {
        // Arrange
        User user = buildUser(1L, false);
        Vehicle vehicle = buildVehicleWithUser(1L, false);
        Event event = new Event();
        event.setId(100L);
        EventVehicle eventVehicle = new EventVehicle();
        eventVehicle.setVehicleId(vehicle);
        eventVehicle.setEvent(event);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(user);
        when(vehicleService.getVehicleById(vehicle.getId())).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = eventVehicleController.createEventVehicle(eventVehicle);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Vehículo no encontrado", ((java.util.Map<?, ?>) response.getBody()).get("error"));
    }

    @Test
    void createEventVehicle_whenUserNotOwnerAndNotAdmin_shouldReturnForbidden() {
        // Arrange
        User user = buildUser(2L, false); // No es dueño
        Vehicle vehicle = buildVehicleWithUser(1L, false); // Dueño es 1L
        Event event = new Event();
        event.setId(100L);
        EventVehicle eventVehicle = new EventVehicle();
        eventVehicle.setVehicleId(vehicle);
        eventVehicle.setEvent(event);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(user);
        when(vehicleService.getVehicleById(vehicle.getId())).thenReturn(Optional.of(vehicle));

        // Act
        ResponseEntity<?> response = eventVehicleController.createEventVehicle(eventVehicle);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Sin permisos", ((java.util.Map<?, ?>) response.getBody()).get("error"));
    }

    @Test
    void createEventVehicle_whenUserAlreadyRegistered_shouldReturnConflict() {
        // Arrange
        User user = buildUser(1L, false);
        Vehicle vehicle = buildVehicleWithUser(1L, false);
        Event event = new Event();
        event.setId(100L);
        EventVehicle eventVehicle = new EventVehicle();
        eventVehicle.setVehicleId(vehicle);
        eventVehicle.setEvent(event);
        EventVehicle existing = new EventVehicle(99L, event, vehicle);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(user);
        when(vehicleService.getVehicleById(vehicle.getId())).thenReturn(Optional.of(vehicle));
        when(eventVehicleService.getVehiclesByEventId(event.getId())).thenReturn(List.of(existing));

        // Act
        ResponseEntity<?> response = eventVehicleController.createEventVehicle(eventVehicle);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Usuario ya inscrito", ((java.util.Map<?, ?>) response.getBody()).get("error"));
    }

    @Test
    void createEventVehicle_whenUnexpectedError_shouldReturnInternalServerError() {
        // Arrange
        User user = buildUser(1L, false);
        Vehicle vehicle = buildVehicleWithUser(1L, false);
        Event event = new Event();
        event.setId(100L);
        EventVehicle eventVehicle = new EventVehicle();
        eventVehicle.setVehicleId(vehicle);
        eventVehicle.setEvent(event);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(user);
        when(vehicleService.getVehicleById(vehicle.getId())).thenThrow(new RuntimeException("DB error"));

        // Act
        ResponseEntity<?> response = eventVehicleController.createEventVehicle(eventVehicle);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error al procesar inscripción", ((java.util.Map<?, ?>) response.getBody()).get("error"));
    }
}