package com.udea.gpx.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.udea.gpx.model.Event;
import com.udea.gpx.model.EventCategory;
import com.udea.gpx.service.EventService;
import com.udea.gpx.util.AuthUtils;
import jakarta.validation.Valid;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/events")
@Tag(name = "Eventos", description = "Gestión de eventos de carreras gpx")
public class EventController {
    private final EventService eventService;
    private final AuthUtils authUtils;

    public EventController(EventService eventService, AuthUtils authUtils) {
        this.eventService = eventService;
        this.authUtils = authUtils;
    }

    @GetMapping
    @Operation(summary = "Listar eventos con paginación", description = "Obtiene una lista paginada de todos los eventos disponibles")
    @ApiResponse(responseCode = "200", description = "Lista de eventos obtenida exitosamente", content = @Content(schema = @Schema(implementation = Page.class)))
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
    @ApiResponse(responseCode = "201", description = "Evento creado exitosamente", content = @Content(schema = @Schema(implementation = Event.class)))
    @ApiResponse(responseCode = "403", description = "Acceso denegado - Solo administradores")
    @ApiResponse(responseCode = "400", description = "Datos del evento inválidos")
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
        List<EventCategory> categories = eventService.getCategoriesByEventId(id);
        return ResponseEntity.ok(categories);
    }

    // ========== SOLO GESTIÓN DE URLs DE IMÁGENES ==========

    @PutMapping(value = "/{id}/picture", consumes = "application/json")
    @Operation(summary = "Actualizar imagen del evento", description = "Actualiza la URL de la imagen del evento")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponse(responseCode = "200", description = "Imagen actualizada exitosamente")
    @ApiResponse(responseCode = "403", description = "Acceso denegado - Solo administradores")
    @ApiResponse(responseCode = "400", description = "URL de imagen inválida")
    @ApiResponse(responseCode = "404", description = "Evento no encontrado")
    public ResponseEntity<Event> updateEventPictureUrl(
            @PathVariable Long id,
            @RequestBody JsonNode requestBody) {

        if (!authUtils.isCurrentUserAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            String pictureUrl = requestBody.get("pictureUrl").asText();

            // Validar URL de imagen
            if (pictureUrl != null && !pictureUrl.trim().isEmpty() && !isValidImageUrl(pictureUrl)) {
                return ResponseEntity.badRequest().build();
            }

            Event updatedEvent = eventService.updateEventPictureUrl(id, pictureUrl);
            return ResponseEntity.ok(updatedEvent);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}/picture")
    @Operation(summary = "Eliminar imagen del evento", description = "Elimina la imagen del evento")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponse(responseCode = "200", description = "Imagen eliminada exitosamente")
    @ApiResponse(responseCode = "403", description = "Acceso denegado - Solo administradores")
    @ApiResponse(responseCode = "404", description = "Evento no encontrado")
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

    // ========== MÉTODOS AUXILIARES ==========

    /**
     * Valida que la URL sea una imagen válida de fuentes permitidas
     */
    private boolean isValidImageUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return true; // Permitir vacío para eliminar imagen
        }

        // Debe ser HTTPS
        if (!url.startsWith("https://")) {
            return false;
        }

        // Debe contener una extensión de imagen válida o ser de servicios conocidos
        String lowerUrl = url.toLowerCase();
        return lowerUrl.matches(".*\\.(jpg|jpeg|png|webp|gif)(\\?.*)?$") ||
                lowerUrl.contains("imgur.com") ||
                lowerUrl.contains("cloudinary.com") ||
                lowerUrl.contains("drive.google.com") ||
                lowerUrl.contains("dropbox.com") ||
                lowerUrl.contains("unsplash.com") ||
                lowerUrl.contains("plus.unsplash.com") ||
                lowerUrl.contains("pexels.com") ||
                lowerUrl.contains("googleusercontent.com") ||
                lowerUrl.contains("lh3.googleusercontent.com") ||
                lowerUrl.contains("lh4.googleusercontent.com") ||
                lowerUrl.contains("lh5.googleusercontent.com") ||
                lowerUrl.contains("lh6.googleusercontent.com");
    }
}