package com.udea.GPX.controller;

import com.udea.GPX.model.Event;
import com.udea.GPX.model.EventCategory;
import com.udea.GPX.service.EventService;
import com.udea.GPX.util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private AuthUtils authUtils;

    @SuppressWarnings("unused")
    @Autowired
    private HttpServletRequest request;

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        return eventService.getEventById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/current")
    public ResponseEntity<List<Event>> getCurrentEvents() {
        return ResponseEntity.ok(eventService.getCurrentEvents());
    }

    @GetMapping("/past")
    public ResponseEntity<List<Event>> getPastEvents() {
        return ResponseEntity.ok(eventService.getPastEvents());
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        if (!authUtils.isCurrentUserAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventService.createEvent(event));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody Event event) {
        if (!authUtils.isCurrentUserAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            return ResponseEntity.ok(eventService.updateEvent(id, event));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        if (!authUtils.isCurrentUserAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/categories")
    public ResponseEntity<List<EventCategory>> getCategoriesByEventId(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getCategoriesByEventId(id));
    }

    // Endpoint para subir imagen de evento con archivo
    @PutMapping("/{id}/picture")
    public ResponseEntity<Event> updateEventPicture(
            @PathVariable Long id,
            @RequestParam(value = "eventPhoto", required = false) MultipartFile eventPhoto,
            @RequestParam(value = "event", required = false) String eventJson) {

        if (!authUtils.isCurrentUserAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            Event updatedEvent;
            if (eventPhoto != null && !eventPhoto.isEmpty()) {
                // Subir archivo
                updatedEvent = eventService.updateEventPicture(id, eventPhoto);
            } else {
                // Este caso se maneja en el endpoint de JSON
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(updatedEvent);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Endpoint para actualizar imagen de evento con URL
    @PutMapping(value = "/{id}/picture", consumes = "application/json")
    public ResponseEntity<Event> updateEventPictureUrl(
            @PathVariable Long id,
            @RequestBody JsonNode requestBody) {

        if (!authUtils.isCurrentUserAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            String pictureUrl = requestBody.get("pictureUrl").asText();
            Event updatedEvent = eventService.updateEventPictureUrl(id, pictureUrl);
            return ResponseEntity.ok(updatedEvent);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Endpoint para eliminar imagen de evento
    @DeleteMapping("/{id}/picture")
    public ResponseEntity<Event> removeEventPicture(@PathVariable Long id) {
        if (!authUtils.isCurrentUserAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            Event updatedEvent = eventService.removeEventPicture(id);
            return ResponseEntity.ok(updatedEvent);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}