package com.udea.GPX;

import com.udea.GPX.controller.EventController;
import com.udea.GPX.model.Event;
import com.udea.GPX.model.EventCategory;
import com.udea.GPX.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@SpringBootTest
public class EventControllerTests {

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController eventController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllEvents_shouldReturnOK() {
        // Arrange
        List<Event> events = Arrays.asList(
                new Event(1L, "Evento 1", "Ubicación 1", "Detalles 1", LocalDate.now(), LocalDate.now()),
                new Event(2L, "Evento 2", "Ubicación 2", "Detalles 2", LocalDate.now(), LocalDate.now())
        );
        when(eventService.getAllEvents()).thenReturn(events);

        // Act
        ResponseEntity<List<Event>> response = eventController.getAllEvents();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    void getEventById_whenEventExists_shouldReturnOK() {
        // Arrange
        Long eventId = 1L;
        Event event = new Event(eventId, "Evento 1", "Ubicación 1", "Detalles 1", LocalDate.now(), LocalDate.now());
        when(eventService.getEventById(eventId)).thenReturn(Optional.of(event));

        // Act
        ResponseEntity<Event> response = eventController.getEventById(eventId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Evento 1", Objects.requireNonNull(response.getBody()).getName());
    }

    @Test
    void getEventById_whenEventNotExists_shouldReturnNotFound() {
        // Arrange
        Long eventId = 1L;
        when(eventService.getEventById(eventId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Event> response = eventController.getEventById(eventId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getEventsByDate_shouldReturnOK() {
        // Arrange
        LocalDate date = LocalDate.now();
        List<Event> events = Arrays.asList(
                new Event(1L, "Evento 1", "Ubicación 1", "Detalles 1", date, date),
                new Event(2L, "Evento 2", "Ubicación 2", "Detalles 2", date, date)
        );
        when(eventService.getEventsByDate(date)).thenReturn(events);

        // Act
        ResponseEntity<List<Event>> response = eventController.getEventsByDate(date);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    void getCategoriesByEventId_shouldReturnOK() {
        // Arrange
        Long eventId = 1L;
        List<EventCategory> categories = Arrays.asList(
                new EventCategory(),
                new EventCategory()
        );
        when(eventService.getCategoriesByEventId(eventId)).thenReturn(categories);

        // Act
        ResponseEntity<List<EventCategory>> response = eventController.getCategoriesByEventId(eventId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
    }
}