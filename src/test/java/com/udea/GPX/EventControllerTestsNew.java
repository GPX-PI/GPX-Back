package com.udea.gpx;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.udea.gpx.controller.EventController;
import com.udea.gpx.model.Event;
import com.udea.gpx.model.EventCategory;
import com.udea.gpx.service.EventService;
import com.udea.gpx.service.FileTransactionService;
import com.udea.gpx.util.AuthUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventController Unit Tests")
class EventControllerTestsNew {

    @Mock
    private EventService eventService;

    @Mock
    private AuthUtils authUtils;

    @Mock
    private FileTransactionService fileTransactionService;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private EventController eventController;

    private Event testEvent1;
    private Event testEvent2;
    private EventCategory testEventCategory1;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        testEvent1 = new Event(1L, "Evento 1", "Ubicación 1", "Detalles 1", LocalDate.now(), LocalDate.now());
        testEvent1.setPicture("ruta/imagen1.jpg");

        testEvent2 = new Event(2L, "Evento 2", "Ubicación 2", "Detalles 2", LocalDate.now(), LocalDate.now());
        testEvent2.setPicture("ruta/imagen2.jpg");

        testEventCategory1 = new EventCategory();
        objectMapper = new ObjectMapper();
    }

    // ========== GET ALL EVENTS TESTS ==========

    @Test
    @DisplayName("getAllEvents - Debe retornar página de eventos exitosamente")
    void getAllEvents_shouldReturnPagedEvents() {
        // Given
        List<Event> events = Arrays.asList(testEvent1, testEvent2);
        Page<Event> page = new PageImpl<>(events);
        when(eventService.getAllEvents(any(Pageable.class))).thenReturn(page);

        // When
        ResponseEntity<Page<Event>> response = eventController.getAllEvents(0, 10, "id", "asc");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Page<Event> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(2, responseBody.getContent().size());
        assertEquals("ruta/imagen1.jpg", responseBody.getContent().get(0).getPicture());
        assertEquals("ruta/imagen2.jpg", responseBody.getContent().get(1).getPicture());
        verify(eventService).getAllEvents(any(Pageable.class));
    }

    // ========== GET EVENT BY ID TESTS ==========

    @Test
    @DisplayName("getEventById - Debe retornar evento cuando existe")
    void getEventById_whenEventExists_shouldReturnEvent() {
        // Given
        when(eventService.getEventById(1L)).thenReturn(Optional.of(testEvent1));

        // When
        ResponseEntity<Event> response = eventController.getEventById(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Event responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("Evento 1", responseBody.getName());
        assertEquals("ruta/imagen1.jpg", responseBody.getPicture());
        verify(eventService).getEventById(1L);
    }

    @Test
    @DisplayName("getEventById - Debe retornar NOT_FOUND cuando no existe")
    void getEventById_whenEventNotExists_shouldReturnNotFound() {
        // Given
        when(eventService.getEventById(1L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<Event> response = eventController.getEventById(1L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(eventService).getEventById(1L);
    }

    // ========== GET CURRENT EVENTS TESTS ==========

    @Test
    @DisplayName("getCurrentEvents - Debe retornar eventos actuales")
    void getCurrentEvents_shouldReturnCurrentEvents() {
        // Given
        List<Event> currentEvents = Arrays.asList(testEvent1, testEvent2);
        when(eventService.getCurrentEvents()).thenReturn(currentEvents);

        // When
        ResponseEntity<List<Event>> response = eventController.getCurrentEvents();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Event> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(2, responseBody.size());
        assertEquals("ruta/imagen1.jpg", responseBody.get(0).getPicture());
        assertEquals("ruta/imagen2.jpg", responseBody.get(1).getPicture());
        verify(eventService).getCurrentEvents();
    }

    // ========== GET PAST EVENTS TESTS ==========

    @Test
    @DisplayName("getPastEvents - Debe retornar eventos pasados")
    void getPastEvents_shouldReturnPastEvents() {
        // Given
        Event pastEvent1 = new Event(1L, "Evento Pasado 1", "Ubicación 1", "Detalles 1",
                LocalDate.now().minusDays(10), LocalDate.now().minusDays(5));
        pastEvent1.setPicture("ruta/pasado1.jpg");
        Event pastEvent2 = new Event(2L, "Evento Pasado 2", "Ubicación 2", "Detalles 2",
                LocalDate.now().minusDays(20), LocalDate.now().minusDays(15));
        pastEvent2.setPicture("ruta/pasado2.jpg");
        List<Event> pastEvents = Arrays.asList(pastEvent1, pastEvent2);
        when(eventService.getPastEvents()).thenReturn(pastEvents);

        // When
        ResponseEntity<List<Event>> response = eventController.getPastEvents();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Event> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(2, responseBody.size());
        assertEquals("ruta/pasado1.jpg", responseBody.get(0).getPicture());
        assertEquals("ruta/pasado2.jpg", responseBody.get(1).getPicture());
        verify(eventService).getPastEvents();
    }

    // ========== GET CATEGORIES BY EVENT ID TESTS ==========

    @Test
    @DisplayName("getCategoriesByEventId - Debe retornar categorías del evento")
    void getCategoriesByEventId_shouldReturnEventCategories() {
        // Given
        List<EventCategory> categories = Arrays.asList(testEventCategory1, new EventCategory());
        when(eventService.getCategoriesByEventId(1L)).thenReturn(categories);

        // When
        ResponseEntity<List<EventCategory>> response = eventController.getCategoriesByEventId(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<EventCategory> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(2, responseBody.size());
        verify(eventService).getCategoriesByEventId(1L);
    }

    // ========== CREATE EVENT TESTS ==========

    @Test
    @DisplayName("createEvent - Debe crear evento cuando el usuario es admin")
    void createEvent_whenAdmin_shouldCreateEvent() {
        // Given
        Event newEvent = new Event();
        Event savedEvent = new Event(1L, "Nuevo Evento", "Ubicación", "Detalles", LocalDate.now(), LocalDate.now());
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        when(eventService.createEvent(newEvent)).thenReturn(savedEvent);

        // When
        ResponseEntity<Event> response = eventController.createEvent(newEvent);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Event responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(savedEvent, responseBody);
        verify(authUtils).isCurrentUserAdmin();
        verify(eventService).createEvent(newEvent);
    }

    @Test
    @DisplayName("createEvent - Debe retornar FORBIDDEN cuando el usuario no es admin")
    void createEvent_whenNotAdmin_shouldReturnForbidden() {
        // Given
        Event newEvent = new Event();
        when(authUtils.isCurrentUserAdmin()).thenReturn(false);

        // When
        ResponseEntity<Event> response = eventController.createEvent(newEvent);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
        verify(authUtils).isCurrentUserAdmin();
        verify(eventService, never()).createEvent(any(Event.class));
    }

    // ========== UPDATE EVENT TESTS ==========

    @Test
    @DisplayName("updateEvent - Debe actualizar evento cuando el usuario es admin")
    void updateEvent_whenAdmin_shouldUpdateEvent() {
        // Given
        Event eventToUpdate = new Event();
        Event updatedEvent = new Event(1L, "Evento Actualizado", "Ubicación", "Detalles", LocalDate.now(),
                LocalDate.now());
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        when(eventService.updateEvent(1L, eventToUpdate)).thenReturn(updatedEvent);

        // When
        ResponseEntity<Event> response = eventController.updateEvent(1L, eventToUpdate);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Event responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(updatedEvent, responseBody);
        verify(authUtils).isCurrentUserAdmin();
        verify(eventService).updateEvent(1L, eventToUpdate);
    }

    @Test
    @DisplayName("updateEvent - Debe retornar FORBIDDEN cuando el usuario no es admin")
    void updateEvent_whenNotAdmin_shouldReturnForbidden() {
        // Given
        Event eventToUpdate = new Event();
        when(authUtils.isCurrentUserAdmin()).thenReturn(false);

        // When
        ResponseEntity<Event> response = eventController.updateEvent(1L, eventToUpdate);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
        verify(authUtils).isCurrentUserAdmin();
        verify(eventService, never()).updateEvent(anyLong(), any(Event.class));
    }

    @Test
    @DisplayName("updateEvent - Debe retornar NOT_FOUND cuando el evento no existe")
    void updateEvent_whenEventNotFound_shouldReturnNotFound() {
        // Given
        Event eventToUpdate = new Event();
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        when(eventService.updateEvent(99L, eventToUpdate)).thenThrow(new RuntimeException("Event not found"));

        // When
        ResponseEntity<Event> response = eventController.updateEvent(99L, eventToUpdate);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(authUtils).isCurrentUserAdmin();
        verify(eventService).updateEvent(99L, eventToUpdate);
    }

    // ========== DELETE EVENT TESTS ==========

    @Test
    @DisplayName("deleteEvent - Debe eliminar evento cuando el usuario es admin")
    void deleteEvent_whenAdmin_shouldDeleteEvent() {
        // Given
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);

        // When
        ResponseEntity<Void> response = eventController.deleteEvent(1L);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(authUtils).isCurrentUserAdmin();
        verify(eventService).deleteEvent(1L);
    }

    @Test
    @DisplayName("deleteEvent - Debe retornar FORBIDDEN cuando el usuario no es admin")
    void deleteEvent_whenNotAdmin_shouldReturnForbidden() {
        // Given
        when(authUtils.isCurrentUserAdmin()).thenReturn(false);

        // When
        ResponseEntity<Void> response = eventController.deleteEvent(1L);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
        verify(authUtils).isCurrentUserAdmin();
        verify(eventService, never()).deleteEvent(anyLong());
    }

    // ========== UPDATE EVENT PICTURE TESTS ==========

    @Test
    @DisplayName("updateEventPicture - Debe actualizar imagen cuando el usuario es admin y el archivo es válido")
    void updateEventPicture_whenAdminAndValidFile_shouldUpdatePicture() {
        // Given
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(eventService.getEventById(1L)).thenReturn(Optional.of(testEvent1));
        when(fileTransactionService.updateFileTransactional(any(), any(), any(), any()))
                .thenReturn("new/image/path.jpg");
        when(eventService.updateEventPictureUrl(1L, "new/image/path.jpg")).thenReturn(testEvent1);

        // When
        ResponseEntity<?> response = eventController.updateEventPicture(1L, multipartFile, null);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("Imagen de evento actualizada exitosamente", responseBody.get("message"));
        verify(authUtils).isCurrentUserAdmin();
        verify(eventService).getEventById(1L);
        verify(fileTransactionService).updateFileTransactional(any(), any(), eq("image"), eq("uploads/events/"));
        verify(eventService).updateEventPictureUrl(1L, "new/image/path.jpg");
    }

    @Test
    @DisplayName("updateEventPicture - Debe retornar FORBIDDEN cuando el usuario no es admin")
    void updateEventPicture_whenNotAdmin_shouldReturnForbidden() {
        // Given
        when(authUtils.isCurrentUserAdmin()).thenReturn(false);

        // When
        ResponseEntity<?> response = eventController.updateEventPicture(1L, multipartFile, null);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(authUtils).isCurrentUserAdmin();
        verify(eventService, never()).getEventById(anyLong());
    }

    @Test
    @DisplayName("updateEventPicture - Debe retornar BAD_REQUEST cuando el archivo es nulo o vacío")
    void updateEventPicture_whenFileIsNullOrEmpty_shouldReturnBadRequest() {
        // Given
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);

        // When
        ResponseEntity<?> response = eventController.updateEventPicture(1L, null, null);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("Archivo de imagen requerido", responseBody.get("error"));
        verify(authUtils).isCurrentUserAdmin();
    }

    @Test
    @DisplayName("updateEventPicture - Debe retornar NOT_FOUND cuando el evento no existe")
    void updateEventPicture_whenEventNotFound_shouldReturnNotFound() {
        // Given
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(eventService.getEventById(99L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<?> response = eventController.updateEventPicture(99L, multipartFile, null);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(authUtils).isCurrentUserAdmin();
        verify(eventService).getEventById(99L);
    }

    // ========== UPDATE EVENT PICTURE URL TESTS ==========

    @Test
    @DisplayName("updateEventPictureUrl - Debe actualizar URL de imagen cuando el usuario es admin")
    void updateEventPictureUrl_whenAdmin_shouldUpdatePictureUrl() {
        // Given
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("pictureUrl", "http://example.com/new-image.jpg");
        Event updatedEvent = new Event(1L, "Evento 1", "Ubicación 1", "Detalles 1", LocalDate.now(), LocalDate.now());
        updatedEvent.setPicture("http://example.com/new-image.jpg");

        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        when(eventService.updateEventPictureUrl(1L, "http://example.com/new-image.jpg")).thenReturn(updatedEvent);

        // When
        ResponseEntity<Event> response = eventController.updateEventPictureUrl(1L, requestBody);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Event responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(updatedEvent, responseBody);
        verify(authUtils).isCurrentUserAdmin();
        verify(eventService).updateEventPictureUrl(1L, "http://example.com/new-image.jpg");
    }

    @Test
    @DisplayName("updateEventPictureUrl - Debe retornar FORBIDDEN cuando el usuario no es admin")
    void updateEventPictureUrl_whenNotAdmin_shouldReturnForbidden() {
        // Given
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("pictureUrl", "http://example.com/new-image.jpg");
        when(authUtils.isCurrentUserAdmin()).thenReturn(false);

        // When
        ResponseEntity<Event> response = eventController.updateEventPictureUrl(1L, requestBody);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
        verify(authUtils).isCurrentUserAdmin();
        verify(eventService, never()).updateEventPictureUrl(anyLong(), anyString());
    }

    @Test
    @DisplayName("updateEventPictureUrl - Debe retornar NOT_FOUND cuando el evento no existe")
    void updateEventPictureUrl_whenEventNotFound_shouldReturnNotFound() {
        // Given
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("pictureUrl", "http://example.com/new-image.jpg");
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        when(eventService.updateEventPictureUrl(99L, "http://example.com/new-image.jpg"))
                .thenThrow(new RuntimeException("Event not found"));

        // When
        ResponseEntity<Event> response = eventController.updateEventPictureUrl(99L, requestBody);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(authUtils).isCurrentUserAdmin();
        verify(eventService).updateEventPictureUrl(99L, "http://example.com/new-image.jpg");
    }

    // ========== REMOVE EVENT PICTURE TESTS ==========

    @Test
    @DisplayName("removeEventPicture - Debe eliminar imagen cuando el usuario es admin")
    void removeEventPicture_whenAdmin_shouldRemovePicture() {
        // Given
        Event updatedEvent = new Event(1L, "Evento 1", "Ubicación 1", "Detalles 1", LocalDate.now(), LocalDate.now());
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        when(eventService.removeEventPicture(1L)).thenReturn(updatedEvent);

        // When
        ResponseEntity<Event> response = eventController.removeEventPicture(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Event responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(updatedEvent, responseBody);
        verify(authUtils).isCurrentUserAdmin();
        verify(eventService).removeEventPicture(1L);
    }

    @Test
    @DisplayName("removeEventPicture - Debe retornar FORBIDDEN cuando el usuario no es admin")
    void removeEventPicture_whenNotAdmin_shouldReturnForbidden() {
        // Given
        when(authUtils.isCurrentUserAdmin()).thenReturn(false);

        // When
        ResponseEntity<Event> response = eventController.removeEventPicture(1L);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
        verify(authUtils).isCurrentUserAdmin();
        verify(eventService, never()).removeEventPicture(anyLong());
    }

    @Test
    @DisplayName("removeEventPicture - Debe retornar NOT_FOUND cuando el evento no existe")
    void removeEventPicture_whenEventNotFound_shouldReturnNotFound() {
        // Given
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        when(eventService.removeEventPicture(99L)).thenThrow(new RuntimeException("Event not found"));

        // When
        ResponseEntity<Event> response = eventController.removeEventPicture(99L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(authUtils).isCurrentUserAdmin();
        verify(eventService).removeEventPicture(99L);
    }

    @Test
    @DisplayName("removeEventPicture - Debe retornar NOT_FOUND cuando hay error inesperado")
    void removeEventPicture_whenUnexpectedError_shouldReturnNotFound() {
        // Given
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        when(eventService.removeEventPicture(1L)).thenThrow(new RuntimeException("Unexpected error"));

        // When
        ResponseEntity<Event> response = eventController.removeEventPicture(1L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(authUtils).isCurrentUserAdmin();
        verify(eventService).removeEventPicture(1L);
    }
}
