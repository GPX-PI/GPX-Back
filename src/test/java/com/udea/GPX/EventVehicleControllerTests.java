package com.udea.GPX;

import com.udea.GPX.controller.EventVehicleController;
import com.udea.GPX.model.Event;
import com.udea.GPX.model.EventVehicle;
import com.udea.GPX.model.User;
import com.udea.GPX.model.Vehicle;
import com.udea.GPX.service.EventVehicleService;
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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@SpringBootTest
public class EventVehicleControllerTests {

    @Mock
    private EventVehicleService eventVehicleService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private EventVehicleController eventVehicleController;

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
    void getVehiclesByEventId_whenNotAdmin_shouldReturnOK() {
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
}