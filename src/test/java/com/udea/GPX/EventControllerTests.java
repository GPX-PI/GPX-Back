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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.udea.gpx.controller.EventController;
import com.udea.gpx.model.Event;
import com.udea.gpx.model.EventCategory;
import com.udea.gpx.service.EventService;
import com.udea.gpx.util.AuthUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventController Unit Tests")
class EventControllerTest {

    @Mock
    private EventService eventService;

    @Mock
    private AuthUtils authUtils;

    @InjectMocks
    private EventController eventController;

    private Event testEvent1;
    private Event testEvent2;
    private EventCategory testEventCategory1;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        testEvent1 = new Event(1L, "Evento 1", "Ubicación 1", "Detalles 1", LocalDate.now(), LocalDate.now());
        testEvent1.setPicture("https://example.com/image1.jpg");

        testEvent2 = new Event(2L, "Evento 2", "Ubicación 2", "Detalles 2", LocalDate.now(), LocalDate.now());
        testEvent2.setPicture("https://example.com/image2.jpg");

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
        assertEquals("https://example.com/image1.jpg", responseBody.getContent().get(0).getPicture());
        assertEquals("https://example.com/image2.jpg", responseBody.getContent().get(1).getPicture());
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
        assertEquals("https://example.com/image1.jpg", responseBody.getPicture());
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
        assertEquals("https://example.com/image1.jpg", responseBody.get(0).getPicture());
        assertEquals("https://example.com/image2.jpg", responseBody.get(1).getPicture());
        verify(eventService).getCurrentEvents();
    }

    // ========== GET PAST EVENTS TESTS ==========

    @Test
    @DisplayName("getPastEvents - Debe retornar eventos pasados")
    void getPastEvents_shouldReturnPastEvents() {
        // Given
        Event pastEvent1 = new Event(1L, "Evento Pasado 1", "Ubicación 1", "Detalles 1",
                LocalDate.now().minusDays(10), LocalDate.now().minusDays(5));
        pastEvent1.setPicture("https://example.com/past1.jpg");
        Event pastEvent2 = new Event(2L, "Evento Pasado 2", "Ubicación 2", "Detalles 2",
                LocalDate.now().minusDays(20), LocalDate.now().minusDays(15));
        pastEvent2.setPicture("https://example.com/past2.jpg");
        List<Event> pastEvents = Arrays.asList(pastEvent1, pastEvent2);
        when(eventService.getPastEvents()).thenReturn(pastEvents);

        // When
        ResponseEntity<List<Event>> response = eventController.getPastEvents();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Event> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(2, responseBody.size());
        assertEquals("https://example.com/past1.jpg", responseBody.get(0).getPicture());
        assertEquals("https://example.com/past2.jpg", responseBody.get(1).getPicture());
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

    // ========== UPDATE EVENT PICTURE URL TESTS ==========

    @Test
    @DisplayName("updateEventPictureUrl - Debe actualizar URL de imagen cuando el usuario es admin")
    void updateEventPictureUrl_whenAdmin_shouldUpdatePictureUrl() {
        // Given
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("pictureUrl", "https://example.com/new-image.jpg");
        Event updatedEvent = new Event(1L, "Evento 1", "Ubicación 1", "Detalles 1", LocalDate.now(), LocalDate.now());
        updatedEvent.setPicture("https://example.com/new-image.jpg");

        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        when(eventService.updateEventPictureUrl(1L, "https://example.com/new-image.jpg")).thenReturn(updatedEvent);

        // When
        ResponseEntity<Event> response = eventController.updateEventPictureUrl(1L, requestBody);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Event responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(updatedEvent, responseBody);
        verify(authUtils).isCurrentUserAdmin();
        verify(eventService).updateEventPictureUrl(1L, "https://example.com/new-image.jpg");
    }

    @Test
    @DisplayName("updateEventPictureUrl - Debe retornar FORBIDDEN cuando el usuario no es admin")
    void updateEventPictureUrl_whenNotAdmin_shouldReturnForbidden() {
        // Given
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("pictureUrl", "https://example.com/new-image.jpg");
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
        requestBody.put("pictureUrl", "https://example.com/new-image.jpg");
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        when(eventService.updateEventPictureUrl(99L, "https://example.com/new-image.jpg"))
                .thenThrow(new RuntimeException("Event not found"));

        // When
        ResponseEntity<Event> response = eventController.updateEventPictureUrl(99L, requestBody);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(authUtils).isCurrentUserAdmin();
        verify(eventService).updateEventPictureUrl(99L, "https://example.com/new-image.jpg");
    }

    @Test
    @DisplayName("updateEventPictureUrl - Debe retornar BAD_REQUEST cuando la URL es inválida")
    void updateEventPictureUrl_whenInvalidUrl_shouldReturnBadRequest() {
        // Given
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("pictureUrl", "http://invalid-url.com/image.jpg"); // HTTP en lugar de HTTPS
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);

        // When
        ResponseEntity<Event> response = eventController.updateEventPictureUrl(1L, requestBody);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(authUtils).isCurrentUserAdmin();
        verify(eventService, never()).updateEventPictureUrl(anyLong(), anyString());
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

    // ========== TESTS PARA VALIDACIÓN DE URLs DE IMAGEN EN EVENTOS ==========

    @Test
    @DisplayName("updateEventPictureUrl - Debe aceptar URLs HTTPS válidas con extensiones de imagen")
    void updateEventPictureUrl_withValidHttpsImageUrls_shouldAccept() {
        // Given
        ObjectNode requestBody = objectMapper.createObjectNode();
        Event updatedEvent = new Event(1L, "Evento Test", "Ubicación", "Detalles", LocalDate.now(), LocalDate.now());
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        when(eventService.updateEventPictureUrl(eq(1L), anyString())).thenReturn(updatedEvent);

        // Test URLs HTTPS válidas con diferentes extensiones
        String[] validUrls = {
                "https://example.com/image.jpg",
                "https://example.com/image.jpeg",
                "https://example.com/image.png",
                "https://example.com/image.webp",
                "https://example.com/image.gif",
                "https://example.com/path/to/image.jpg?param=value",
                "https://subdomain.example.com/image.png"
        };

        for (String url : validUrls) {
            requestBody.put("pictureUrl", url);

            // When
            ResponseEntity<Event> response = eventController.updateEventPictureUrl(1L, requestBody);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode(), "Failed for URL: " + url);
            verify(eventService, atLeastOnce()).updateEventPictureUrl(eq(1L), eq(url));
        }
    }

    @Test
    @DisplayName("updateEventPictureUrl - Debe rechazar URLs HTTP (no seguras)")
    void updateEventPictureUrl_withHttpUrls_shouldReject() {
        // Given
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("pictureUrl", "http://example.com/image.jpg"); // HTTP no HTTPS
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);

        // When
        ResponseEntity<Event> response = eventController.updateEventPictureUrl(1L, requestBody);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(eventService, never()).updateEventPictureUrl(anyLong(), anyString());
    }

    @Test
    @DisplayName("updateEventPictureUrl - Debe rechazar URLs sin extensiones de imagen válidas")
    void updateEventPictureUrl_withInvalidExtensions_shouldReject() {
        // Given
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);

        String[] invalidUrls = {
                "https://example.com/document.pdf",
                "https://example.com/file.txt",
                "https://example.com/video.mp4",
                "https://example.com/audio.mp3",
                "https://example.com/noextension"
        };

        for (String url : invalidUrls) {
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("pictureUrl", url);

            // When
            ResponseEntity<Event> response = eventController.updateEventPictureUrl(1L, requestBody);

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Should reject URL: " + url);
        }

        verify(eventService, never()).updateEventPictureUrl(anyLong(), anyString());
    }

    @Test
    @DisplayName("updateEventPictureUrl - Debe aceptar URLs de servicios de imagen conocidos")
    void updateEventPictureUrl_withKnownImageServices_shouldAccept() {
        // Given
        Event updatedEvent = new Event(1L, "Evento Test", "Ubicación", "Detalles", LocalDate.now(), LocalDate.now());
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        when(eventService.updateEventPictureUrl(eq(1L), anyString())).thenReturn(updatedEvent);

        String[] knownServiceUrls = {
                "https://imgur.com/a/abc123",
                "https://res.cloudinary.com/demo/image/upload/sample.jpg",
                "https://drive.google.com/file/d/1ABC/view",
                "https://www.dropbox.com/s/abc123/image.jpg",
                "https://unsplash.com/photos/abc123",
                "https://images.unsplash.com/photo-123",
                "https://plus.unsplash.com/premium_photo-123",
                "https://images.pexels.com/photos/123/image.jpg",
                "https://lh3.googleusercontent.com/abc123",
                "https://lh4.googleusercontent.com/abc123",
                "https://lh5.googleusercontent.com/abc123",
                "https://lh6.googleusercontent.com/abc123"
        };

        for (String url : knownServiceUrls) {
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("pictureUrl", url);

            // When
            ResponseEntity<Event> response = eventController.updateEventPictureUrl(1L, requestBody);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode(), "Failed for service URL: " + url);
        }
        verify(eventService, times(knownServiceUrls.length)).updateEventPictureUrl(eq(1L), anyString());
    }

    @Test
    @DisplayName("updateEventPictureUrl - Debe aceptar URL vacía para eliminar imagen")
    void updateEventPictureUrl_withEmptyUrl_shouldAcceptToRemoveImage() {
        // Given
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("pictureUrl", ""); // URL vacía para eliminar
        Event updatedEvent = new Event(1L, "Evento Test", "Ubicación", "Detalles", LocalDate.now(), LocalDate.now());
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        when(eventService.updateEventPictureUrl(eq(1L), anyString())).thenReturn(updatedEvent);

        // When
        ResponseEntity<Event> response = eventController.updateEventPictureUrl(1L, requestBody);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(eventService).updateEventPictureUrl(eq(1L), eq(""));
    }

    @Test
    @DisplayName("updateEventPictureUrl - Debe manejar URLs con parámetros de query correctamente")
    void updateEventPictureUrl_withQueryParameters_shouldHandleCorrectly() {
        // Given
        Event updatedEvent = new Event(1L, "Evento Test", "Ubicación", "Detalles", LocalDate.now(), LocalDate.now());
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        when(eventService.updateEventPictureUrl(eq(1L), anyString())).thenReturn(updatedEvent);

        String[] urlsWithParams = {
                "https://example.com/image.jpg?v=1&size=large",
                "https://example.com/image.png?width=800&height=600",
                "https://example.com/path/image.webp?token=abc123&format=webp"
        };

        for (String url : urlsWithParams) {
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("pictureUrl", url);

            // When
            ResponseEntity<Event> response = eventController.updateEventPictureUrl(1L, requestBody);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode(), "Failed for URL with params: " + url);
        }

        verify(eventService, times(urlsWithParams.length)).updateEventPictureUrl(eq(1L), anyString());
    }

    @Test
    @DisplayName("updateEventPictureUrl - Debe rechazar URLs con protocolos no seguros")
    void updateEventPictureUrl_withUnsafeProtocols_shouldReject() {
        // Given
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);

        String[] unsafeUrls = {
                "ftp://example.com/image.jpg",
                "javascript:alert('xss')",
                "data:image/jpeg;base64,abc123",
                "file:///etc/passwd",
                "//example.com/image.jpg" // Protocol-relative URL
        };
        for (String url : unsafeUrls) {
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("pictureUrl", url);

            // When
            ResponseEntity<Event> response = eventController.updateEventPictureUrl(1L, requestBody);

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Should reject unsafe URL: " + url);
        }

        verify(eventService, never()).updateEventPictureUrl(anyLong(), anyString());
    }

    // ========== TESTS PARA MÉTODOS DE VALIDACIÓN DE URLS EN EVENTCONTROLLER
    // ==========

    @Test
    @DisplayName("hasValidImageExtension - debería validar extensiones de imagen correctamente en EventController")
    void hasValidImageExtension_shouldValidateImageExtensions() throws Exception {
        // Usando reflection para acceder al método privado
        java.lang.reflect.Method method = EventController.class.getDeclaredMethod("hasValidImageExtension",
                String.class);
        method.setAccessible(true);

        // Casos válidos - HTTPS con extensiones válidas
        assertTrue((Boolean) method.invoke(eventController, "https://example.com/image.jpg"));
        assertTrue((Boolean) method.invoke(eventController, "https://example.com/image.jpeg"));
        assertTrue((Boolean) method.invoke(eventController, "https://example.com/image.png"));
        assertTrue((Boolean) method.invoke(eventController, "https://example.com/image.webp"));
        assertTrue((Boolean) method.invoke(eventController, "https://example.com/image.gif"));

        // Casos válidos - HTTP con extensiones válidas
        assertTrue((Boolean) method.invoke(eventController, "http://example.com/image.jpg"));
        assertTrue((Boolean) method.invoke(eventController, "http://example.com/image.png"));

        // Casos válidos - con parámetros de consulta
        assertTrue((Boolean) method.invoke(eventController, "https://example.com/image.jpg?v=123"));
        assertTrue(
                (Boolean) method.invoke(eventController, "https://example.com/path/image.png?size=large&format=webp"));

        // Casos inválidos - sin protocolo
        assertFalse((Boolean) method.invoke(eventController, "example.com/image.jpg"));
        assertFalse((Boolean) method.invoke(eventController, "//example.com/image.jpg"));

        // Casos inválidos - extensiones no válidas
        assertFalse((Boolean) method.invoke(eventController, "https://example.com/document.pdf"));
        assertFalse((Boolean) method.invoke(eventController, "https://example.com/file.txt"));
        assertFalse((Boolean) method.invoke(eventController, "https://example.com/video.mp4"));

        // Casos inválidos - sin extensión
        assertFalse((Boolean) method.invoke(eventController, "https://example.com/image"));
        assertFalse((Boolean) method.invoke(eventController, "https://example.com/"));
    }

    @Test
    @DisplayName("isValidImageUrl - debería validar URLs de imagen completas en EventController")
    void isValidImageUrl_shouldValidateCompleteImageUrls() throws Exception {
        java.lang.reflect.Method method = EventController.class.getDeclaredMethod("isValidImageUrl", String.class);
        method.setAccessible(true);

        // Casos válidos - URLs vacías (permitidas para eliminar imagen)
        assertTrue((Boolean) method.invoke(eventController, ""));
        assertTrue((Boolean) method.invoke(eventController, "   "));
        assertTrue((Boolean) method.invoke(eventController, (String) null));

        // Casos válidos - extensiones directas
        assertTrue((Boolean) method.invoke(eventController, "https://example.com/image.jpg"));
        assertTrue((Boolean) method.invoke(eventController, "https://example.com/image.png"));

        // Casos válidos - servicios conocidos
        assertTrue((Boolean) method.invoke(eventController, "https://imgur.com/abc123"));
        assertTrue((Boolean) method.invoke(eventController, "https://cloudinary.com/image/upload/xyz"));
        assertTrue((Boolean) method.invoke(eventController, "https://drive.google.com/file/d/123"));
        assertTrue((Boolean) method.invoke(eventController, "https://dropbox.com/s/abc/image"));
        assertTrue((Boolean) method.invoke(eventController, "https://unsplash.com/photos/123"));
        assertTrue((Boolean) method.invoke(eventController, "https://plus.unsplash.com/premium_photo-123"));
        assertTrue((Boolean) method.invoke(eventController, "https://pexels.com/photo/123"));
        assertTrue((Boolean) method.invoke(eventController, "https://googleusercontent.com/abc"));
        assertTrue((Boolean) method.invoke(eventController, "https://lh3.googleusercontent.com/abc"));
        assertTrue((Boolean) method.invoke(eventController, "https://lh4.googleusercontent.com/abc"));
        assertTrue((Boolean) method.invoke(eventController, "https://lh5.googleusercontent.com/abc"));
        assertTrue((Boolean) method.invoke(eventController, "https://lh6.googleusercontent.com/abc"));

        // Casos inválidos - no HTTPS
        assertFalse((Boolean) method.invoke(eventController, "http://example.com/image.jpg"));
        assertFalse((Boolean) method.invoke(eventController, "ftp://example.com/image.jpg"));

        // Casos inválidos - extensión no válida en dominio desconocido
        assertFalse((Boolean) method.invoke(eventController, "https://unknown-site.com/file.txt"));
        assertFalse((Boolean) method.invoke(eventController, "https://unknown-site.com/video.mp4"));

        // Casos inválidos - sin extensión en dominio desconocido
        assertFalse((Boolean) method.invoke(eventController, "https://unknown-site.com/somefile"));
    }

    @Test
    @DisplayName("Validación de URLs en EventController - casos edge y de seguridad")
    void eventUrlValidation_edgeCasesAndSecurity() throws Exception {
        java.lang.reflect.Method imageMethod = EventController.class.getDeclaredMethod("hasValidImageExtension",
                String.class);
        imageMethod.setAccessible(true);

        // Edge cases - URLs con caracteres especiales
        assertTrue((Boolean) imageMethod.invoke(eventController, "https://example.com/my%20image.jpg"));

        // Edge cases - URLs muy largas
        String longUrl = "https://example.com/" + "a".repeat(1000) + "/image.jpg";
        assertTrue((Boolean) imageMethod.invoke(eventController, longUrl));

        // Edge cases - múltiples puntos
        assertTrue((Boolean) imageMethod.invoke(eventController, "https://example.com/file.name.with.dots.jpg"));

        // Edge cases - extensiones en mayúsculas (deberían fallar ya que usamos
        // endsWith exacto)
        assertFalse((Boolean) imageMethod.invoke(eventController, "https://example.com/image.JPG"));

        // Casos de seguridad - intentos de bypass
        assertFalse((Boolean) imageMethod.invoke(eventController, "javascript:alert('xss')"));
        assertFalse((Boolean) imageMethod.invoke(eventController, "data:text/html,<script>alert('xss')</script>"));
        assertFalse((Boolean) imageMethod.invoke(eventController, "file:///etc/passwd"));
        assertFalse((Boolean) imageMethod.invoke(eventController, "ftp://malicious.com/file.jpg"));
    }
}
