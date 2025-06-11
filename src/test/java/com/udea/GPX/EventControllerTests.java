package com.udea.gpx;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.udea.gpx.controller.EventController;
import com.udea.gpx.model.Event;
import com.udea.gpx.model.EventCategory;
import com.udea.gpx.service.EventService;
import com.udea.gpx.util.AuthUtils;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EventControllerTests {

    @InjectMocks
    private EventController eventController;

    @Mock
    private EventService eventService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Authentication authentication;

    @Mock
    private AuthUtils authUtils;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private com.udea.gpx.service.FileTransactionService fileTransactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Inyectar el mock de FileTransactionService usando reflection
        ReflectionTestUtils.setField(eventController, "fileTransactionService", fileTransactionService);
    }

    @Test
    void getAllEvents_shouldReturnOK() {
        // Arrange
        Event event1 = new Event(1L, "Evento 1", "Ubicación 1", "Detalles 1", LocalDate.now(), LocalDate.now());
        event1.setPicture("ruta/imagen1.jpg");
        Event event2 = new Event(2L, "Evento 2", "Ubicación 2", "Detalles 2", LocalDate.now(), LocalDate.now());
        event2.setPicture("ruta/imagen2.jpg");
        List<Event> events = Arrays.asList(event1, event2);

        // Mock para paginación
        Page<Event> page = new PageImpl<>(events);
        when(eventService.getAllEvents(any(Pageable.class))).thenReturn(page);

        // Act
        ResponseEntity<Page<Event>> response = eventController.getAllEvents(0, 10, "id", "asc");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Page<Event> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(2, responseBody.getContent().size());
        assertEquals("ruta/imagen1.jpg", responseBody.getContent().get(0).getPicture());
        assertEquals("ruta/imagen2.jpg", responseBody.getContent().get(1).getPicture());
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
        Event responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("Evento 1", responseBody.getName());
        assertEquals("ruta/imagen1.jpg", responseBody.getPicture());
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
        List<Event> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(2, responseBody.size());
        assertEquals("ruta/actual1.jpg", responseBody.get(0).getPicture());
        assertEquals("ruta/actual2.jpg", responseBody.get(1).getPicture());
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
        List<Event> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(2, responseBody.size());
        assertEquals("ruta/pasado1.jpg", responseBody.get(0).getPicture());
        assertEquals("ruta/pasado2.jpg", responseBody.get(1).getPicture());
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
        List<EventCategory> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(2, responseBody.size());
    }

    @Test
    void createEvent_whenAdmin_shouldReturnCreated() {
        Event event = new Event();
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        when(eventService.createEvent(event)).thenReturn(event);
        ResponseEntity<Event> response = eventController.createEvent(event);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Event responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(event, responseBody);
    }

    @Test
    void createEvent_whenNotAdmin_shouldReturnForbidden() {
        Event event = new Event();
        when(authUtils.isCurrentUserAdmin()).thenReturn(false);
        ResponseEntity<Event> response = eventController.createEvent(event);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void updateEvent_whenAdmin_shouldReturnOK() {
        Event event = new Event();
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        when(eventService.updateEvent(1L, event)).thenReturn(event);
        ResponseEntity<Event> response = eventController.updateEvent(1L, event);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Event responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(event, responseBody);
    }

    @Test
    void updateEvent_whenNotAdmin_shouldReturnForbidden() {
        Event event = new Event();
        when(authUtils.isCurrentUserAdmin()).thenReturn(false);
        ResponseEntity<Event> response = eventController.updateEvent(1L, event);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void updateEvent_whenAdminAndNotFound_shouldReturnNotFound() {
        Event event = new Event();
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        doThrow(new RuntimeException()).when(eventService).updateEvent(1L, event);
        ResponseEntity<Event> response = eventController.updateEvent(1L, event);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteEvent_whenAdmin_shouldReturnNoContent() {
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        ResponseEntity<Void> response = eventController.deleteEvent(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void deleteEvent_whenNotAdmin_shouldReturnForbidden() {
        when(authUtils.isCurrentUserAdmin()).thenReturn(false);
        ResponseEntity<Void> response = eventController.deleteEvent(1L);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void updateEventPicture_whenAdminAndFile_shouldReturnOK() throws Exception {
        // Arrange
        Long eventId = 1L;
        Event existingEvent = new Event(eventId, "Evento Test", "Ubicación", "Detalles", LocalDate.now(),
                LocalDate.now());
        existingEvent.setPicture("old-picture.jpg");

        Event updatedEvent = new Event(eventId, "Evento Test", "Ubicación", "Detalles", LocalDate.now(),
                LocalDate.now());
        updatedEvent.setPicture("new-picture.jpg");

        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(eventService.getEventById(eventId)).thenReturn(Optional.of(existingEvent));
        when(fileTransactionService.updateFileTransactional(
                eq(multipartFile), eq("old-picture.jpg"), eq("image"), eq("uploads/events/")))
                .thenReturn("new-picture.jpg");
        when(eventService.updateEventPictureUrl(eventId, "new-picture.jpg")).thenReturn(updatedEvent);

        // Act
        ResponseEntity<?> response = eventController.updateEventPicture(eventId, multipartFile, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Imagen de evento actualizada exitosamente", responseBody.get("message"));
        assertEquals(updatedEvent, responseBody.get("event"));
    }

    @Test
    void updateEventPicture_whenNotAdmin_shouldReturnForbidden() {
        when(authUtils.isCurrentUserAdmin()).thenReturn(false);
        ResponseEntity<?> response = eventController.updateEventPicture(1L, multipartFile, null);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void updateEventPicture_whenAdminAndFileNotFound_shouldReturnNotFound() throws Exception {
        // Arrange
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(eventService.getEventById(1L)).thenReturn(Optional.empty()); // Evento no encontrado

        // Act
        ResponseEntity<?> response = eventController.updateEventPicture(1L, multipartFile, null);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateEventPicture_whenAdminAndFileNull_shouldReturnBadRequest() {
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        ResponseEntity<?> response = eventController.updateEventPicture(1L, null, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updateEventPictureUrl_whenAdmin_shouldReturnOK() {
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("pictureUrl", "http://test.com/img.jpg");
        Event event = new Event();
        when(eventService.updateEventPictureUrl(1L, "http://test.com/img.jpg")).thenReturn(event);
        ResponseEntity<Event> response = eventController.updateEventPictureUrl(1L, node);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Event responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(event, responseBody);
    }

    @Test
    void updateEventPictureUrl_whenNotAdmin_shouldReturnForbidden() {
        when(authUtils.isCurrentUserAdmin()).thenReturn(false);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("pictureUrl", "http://test.com/img.jpg");
        ResponseEntity<Event> response = eventController.updateEventPictureUrl(1L, node);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void updateEventPictureUrl_whenAdminAndNotFound_shouldReturnNotFound() {
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("pictureUrl", "http://test.com/img.jpg");
        doThrow(new RuntimeException()).when(eventService).updateEventPictureUrl(1L, "http://test.com/img.jpg");
        ResponseEntity<Event> response = eventController.updateEventPictureUrl(1L, node);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void removeEventPicture_whenAdmin_shouldReturnOK() {
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        Event event = new Event();
        when(eventService.removeEventPicture(1L)).thenReturn(event);
        ResponseEntity<Event> response = eventController.removeEventPicture(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Event responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(event, responseBody);
    }

    @Test
    void removeEventPicture_whenNotAdmin_shouldReturnForbidden() {
        when(authUtils.isCurrentUserAdmin()).thenReturn(false);
        ResponseEntity<Event> response = eventController.removeEventPicture(1L);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void removeEventPicture_whenAdminAndNotFound_shouldReturnNotFound() {
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        doThrow(new RuntimeException()).when(eventService).removeEventPicture(1L);
        ResponseEntity<Event> response = eventController.removeEventPicture(1L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}