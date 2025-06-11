package com.udea.gpx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.JsonNode;
import com.udea.gpx.model.Event;
import com.udea.gpx.model.EventCategory;
import com.udea.gpx.service.EventService;
import com.udea.gpx.service.FileTransactionService;
import com.udea.gpx.util.AuthUtils;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/events")
@Tag(name = "Eventos", description = "Gestión de eventos de carreras gpx")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private AuthUtils authUtils;

    @Autowired
    private FileTransactionService fileTransactionService;

    @SuppressWarnings("unused")
    @Autowired
    private HttpServletRequest request;

    @GetMapping
    @Operation(summary = "Listar eventos con paginación", description = "Obtiene una lista paginada de todos los eventos disponibles")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de eventos obtenida exitosamente", content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<Event>> getAllEvents(
            @Parameter(description = "Número de página (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenar", example = "id", schema = @Schema(allowableValues = { "id",
                    "name", "startDate", "endDate" })) @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Dirección del ordenamiento", example = "asc", schema = @Schema(allowableValues = {
                    "asc", "desc" })) @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(eventService.getAllEvents(pageable));
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
    @Operation(summary = "Crear nuevo evento", description = "Crea un nuevo evento de carrera. Solo accesible para administradores.")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Evento creado exitosamente", content = @Content(schema = @Schema(implementation = Event.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Solo administradores"),
            @ApiResponse(responseCode = "400", description = "Datos del evento inválidos")
    })
    public ResponseEntity<Event> createEvent(
            @Parameter(description = "Datos del evento a crear", required = true) @Valid @RequestBody Event event) {
        if (!authUtils.isCurrentUserAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventService.createEvent(event));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @Valid @RequestBody Event event) {
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
    public ResponseEntity<?> updateEventPicture(
            @PathVariable Long id,
            @RequestParam(value = "eventPhoto", required = false) MultipartFile eventPhoto,
            @RequestParam(value = "event", required = false) String eventJson) {

        if (!authUtils.isCurrentUserAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (eventPhoto == null || eventPhoto.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Archivo de imagen requerido");
            errorResponse.put("errorType", "VALIDATION_ERROR");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // Obtener evento actual
        Optional<Event> eventOpt = eventService.getEventById(id);
        if (eventOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Event currentEvent = eventOpt.get();

        // Actualizar imagen usando el servicio transaccional
        String newPicturePath = fileTransactionService.updateFileTransactional(
                eventPhoto, currentEvent.getPicture(), "image", "uploads/events/");

        // Actualizar evento en base de datos
        Event updatedEvent = eventService.updateEventPictureUrl(id, newPicturePath);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Imagen de evento actualizada exitosamente");
        response.put("event", updatedEvent);

        return ResponseEntity.ok(response);
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