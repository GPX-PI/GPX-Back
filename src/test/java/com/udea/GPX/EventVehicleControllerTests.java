package com.udea.GPX;

import com.udea.GPX.controller.EventVehicleController;
import com.udea.GPX.model.Event;
import com.udea.GPX.model.EventVehicle;
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

    @InjectMocks
    private EventVehicleController eventVehicleController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllEventVehicles_shouldReturnOK() {
        // Arrange
        List<EventVehicle> eventVehicles = Arrays.asList(
                new EventVehicle(1L, new Event(), new Vehicle()),
                new EventVehicle(2L, new Event(), new Vehicle())
        );
        when(eventVehicleService.getAllEventVehicles()).thenReturn(eventVehicles);

        // Act
        ResponseEntity<List<EventVehicle>> response = eventVehicleController.getAllEventVehicles();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    void getEventVehicleById_whenEventVehicleExists_shouldReturnOK() {
        // Arrange
        Long eventVehicleId = 1L;
        EventVehicle eventVehicle = new EventVehicle(eventVehicleId, new Event(), new Vehicle());
        when(eventVehicleService.getEventVehicleById(eventVehicleId)).thenReturn(Optional.of(eventVehicle));

        // Act
        ResponseEntity<EventVehicle> response = eventVehicleController.getEventVehicleById(eventVehicleId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(eventVehicleId, Objects.requireNonNull(response.getBody()).getId());
    }

    @Test
    void getEventVehicleById_whenEventVehicleNotExists_shouldReturnNotFound() {
        // Arrange
        Long eventVehicleId = 1L;
        when(eventVehicleService.getEventVehicleById(eventVehicleId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<EventVehicle> response = eventVehicleController.getEventVehicleById(eventVehicleId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getVehiclesByEventId_shouldReturnOK() {
        // Arrange
        Long eventId = 1L;
        List<EventVehicle> eventVehicles = Arrays.asList(
                new EventVehicle(1L, new Event(), new Vehicle()),
                new EventVehicle(2L, new Event(), new Vehicle())
        );
        when(eventVehicleService.getVehiclesByEventId(eventId)).thenReturn(eventVehicles);

        // Act
        ResponseEntity<List<EventVehicle>> response = eventVehicleController.getVehiclesByEventId(eventId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
    }
}