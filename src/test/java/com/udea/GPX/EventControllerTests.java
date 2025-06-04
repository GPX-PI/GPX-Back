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
        Event event1 = new Event(1L, "Evento 1", "Ubicación 1", "Detalles 1", LocalDate.now(), LocalDate.now());
        event1.setPicture("ruta/imagen1.jpg");
        Event event2 = new Event(2L, "Evento 2", "Ubicación 2", "Detalles 2", LocalDate.now(), LocalDate.now());
        event2.setPicture("ruta/imagen2.jpg");
        List<Event> events = Arrays.asList(event1, event2);
        when(eventService.getAllEvents()).thenReturn(events);

        // Act
        ResponseEntity<List<Event>> response = eventController.getAllEvents();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
        assertEquals("ruta/imagen1.jpg", response.getBody().get(0).getPicture());
        assertEquals("ruta/imagen2.jpg", response.getBody().get(1).getPicture());
    }

    @Test
    void getEventById_whenEventExists_shouldReturnOK() {
        // Arrange
        Long eventId = 1L;
        Event event = new Event(eventId, "Evento 1", "Ubicación 1", "Detalles 1", LocalDate.now(), LocalDate.now());
        event.setPicture("ruta/imagen1.jpg");
        when(eventService.getEventById(eventId)).thenReturn(Optional.of(event));

        // Act
        ResponseEntity<Event> response = eventController.getEventById(eventId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Evento 1", Objects.requireNonNull(response.getBody()).getName());
        assertEquals("ruta/imagen1.jpg", response.getBody().getPicture());
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
    void getCurrentEvents_shouldReturnOK() {
        // Arrange
        Event event1 = new Event(1L, "Evento Actual 1", "Ubicación 1", "Detalles 1", LocalDate.now(), LocalDate.now());
        event1.setPicture("ruta/actual1.jpg");
        Event event2 = new Event(2L, "Evento Actual 2", "Ubicación 2", "Detalles 2", LocalDate.now(), LocalDate.now());
        event2.setPicture("ruta/actual2.jpg");
        List<Event> events = Arrays.asList(event1, event2);
        when(eventService.getCurrentEvents()).thenReturn(events);

        // Act
        ResponseEntity<List<Event>> response = eventController.getCurrentEvents();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
        assertEquals("ruta/actual1.jpg", response.getBody().get(0).getPicture());
        assertEquals("ruta/actual2.jpg", response.getBody().get(1).getPicture());
    }

    @Test
    void getPastEvents_shouldReturnOK() {
        // Arrange
        Event event1 = new Event(1L, "Evento Pasado 1", "Ubicación 1", "Detalles 1", LocalDate.now().minusDays(10),
                LocalDate.now().minusDays(5));
        event1.setPicture("ruta/pasado1.jpg");
        Event event2 = new Event(2L, "Evento Pasado 2", "Ubicación 2", "Detalles 2", LocalDate.now().minusDays(20),
                LocalDate.now().minusDays(15));
        event2.setPicture("ruta/pasado2.jpg");
        List<Event> events = Arrays.asList(event1, event2);
        when(eventService.getPastEvents()).thenReturn(events);

        // Act
        ResponseEntity<List<Event>> response = eventController.getPastEvents();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
        assertEquals("ruta/pasado1.jpg", response.getBody().get(0).getPicture());
        assertEquals("ruta/pasado2.jpg", response.getBody().get(1).getPicture());
    }

    @Test
    void getCategoriesByEventId_shouldReturnOK() {
        // Arrange
        Long eventId = 1L;
        List<EventCategory> categories = Arrays.asList(
                new EventCategory(),
                new EventCategory());
        when(eventService.getCategoriesByEventId(eventId)).thenReturn(categories);

        // Act
        ResponseEntity<List<EventCategory>> response = eventController.getCategoriesByEventId(eventId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
    }
}