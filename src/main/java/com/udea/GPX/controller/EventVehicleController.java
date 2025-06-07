package com.udea.GPX.controller;

import com.udea.GPX.model.EventVehicle;
import com.udea.GPX.model.User;
import com.udea.GPX.model.Vehicle;
import com.udea.GPX.service.EventVehicleService;
import com.udea.GPX.service.UserService;
import com.udea.GPX.service.VehicleService;
import com.udea.GPX.dto.ParticipantDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/event-vehicles")
public class EventVehicleController {

    @Autowired
    private EventVehicleService eventVehicleService;

    @Autowired
    private UserService userService;

    @Autowired
    private VehicleService vehicleService;

    @SuppressWarnings("unused")
    @Autowired
    private HttpServletRequest request;

    /**
     * Obtiene el usuario autenticado, manejando tanto autenticación local como
     * OAuth2
     */
    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();

        if (principal instanceof User) {
            // Autenticación local - el principal ya es un User
            return (User) principal;
        } else if (principal instanceof OAuth2User oauth2User) {
            // Autenticación OAuth2 - necesitamos buscar el User en la base de datos
            String email = oauth2User.getAttribute("email");
            if (email != null) {
                User user = userService.findByEmail(email);
                if (user != null) {
                    return user;
                } else {
                    throw new RuntimeException("Usuario OAuth2 no encontrado en la base de datos");
                }
            }
        }

        throw new RuntimeException("Usuario no autenticado correctamente");
    }

    @GetMapping
    public ResponseEntity<List<EventVehicle>> getAllEventVehicles() {
        User authUser = getAuthenticatedUser();
        if (!authUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        return ResponseEntity.ok(eventVehicleService.getAllEventVehicles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventVehicle> getEventVehicleById(@PathVariable Long id) {
        User authUser = getAuthenticatedUser();
        EventVehicle ev = eventVehicleService.getEventVehicleById(id).orElse(null);
        if (ev == null) {
            return ResponseEntity.notFound().build();
        }
        if (!authUser.isAdmin() && (ev.getVehicleId() == null || ev.getVehicleId().getUser() == null
                || !ev.getVehicleId().getUser().getId().equals(authUser.getId()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(ev);
    }

    @GetMapping("/byevent/{eventId}")
    public ResponseEntity<List<EventVehicle>> getVehiclesByEventId(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventVehicleService.getVehiclesByEventId(eventId));
    }

    @GetMapping("/participants/{eventId}")
    public ResponseEntity<List<ParticipantDTO>> getEventParticipants(@PathVariable Long eventId) {
        try {
            List<EventVehicle> eventVehicles = eventVehicleService.getVehiclesByEventId(eventId);
            List<ParticipantDTO> participants = eventVehicles.stream()
                    .filter(ev -> ev.getVehicleId() != null && ev.getVehicleId().getUser() != null)
                    .map(ev -> {
                        Vehicle vehicle = ev.getVehicleId();
                        User user = vehicle.getUser();
                        String categoryName = vehicle.getCategory() != null ? vehicle.getCategory().getName()
                                : "Sin categoría";
                        Long categoryId = vehicle.getCategory() != null ? vehicle.getCategory().getId() : null;

                        String fullName = user.getFirstName() +
                                (user.getLastName() != null ? " " + user.getLastName() : "");

                        return new ParticipantDTO(
                                ev.getId(),
                                user.getId(),
                                fullName,
                                user.getPicture(),
                                user.getTeamName(),
                                vehicle.getId(),
                                vehicle.getName(),
                                vehicle.getPlates(),
                                vehicle.getSoat(),
                                categoryId,
                                categoryName,
                                null // Fecha de registro - se puede agregar después si el modelo EventVehicle tiene
                                     // fecha
                        );
                    })
                    .toList();

            return ResponseEntity.ok(participants);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<?> createEventVehicle(@RequestBody EventVehicle eventVehicle) {
        User authUser = getAuthenticatedUser();

        try {
            // Cargar el vehículo completo desde la base de datos
            Long vehicleId = eventVehicle.getVehicleId().getId();
            Vehicle vehicle = vehicleService.getVehicleById(vehicleId)
                    .orElse(null);

            if (vehicle == null) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Vehículo no encontrado");
                errorResponse.put("message", "El vehículo seleccionado no existe.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            // Validar que el vehículo pertenece al usuario autenticado (si no es admin)
            if (!authUser.isAdmin() && (vehicle.getUser() == null
                    || !vehicle.getUser().getId().equals(authUser.getId()))) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Sin permisos");
                errorResponse.put("message", "No tienes permisos para inscribir este vehículo.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            }

            // Verificar si el usuario ya está inscrito en este evento
            Long eventId = eventVehicle.getEvent().getId();
            Long userId = authUser.getId();

            List<EventVehicle> existingRegistrations = eventVehicleService.getVehiclesByEventId(eventId);
            boolean userAlreadyRegistered = existingRegistrations.stream()
                    .anyMatch(ev -> ev.getVehicleId() != null
                            && ev.getVehicleId().getUser() != null
                            && ev.getVehicleId().getUser().getId().equals(userId));

            if (userAlreadyRegistered) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Usuario ya inscrito");
                errorResponse.put("message",
                        "Ya tienes un vehículo inscrito en este evento. Solo se permite una inscripción por persona.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }

            // Establecer el vehículo completo en el EventVehicle
            eventVehicle.setVehicleId(vehicle);

            EventVehicle savedEventVehicle = eventVehicleService.createEventVehicle(eventVehicle);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedEventVehicle);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al procesar inscripción");
            errorResponse.put("message", "No se pudo completar la inscripción. Intenta nuevamente.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEventVehicle(@PathVariable Long id) {
        User authUser = getAuthenticatedUser();
        EventVehicle ev = eventVehicleService.getEventVehicleById(id).orElse(null);
        if (ev == null) {
            return ResponseEntity.notFound().build();
        }
        if (!authUser.isAdmin() && (ev.getVehicleId() == null || ev.getVehicleId().getUser() == null
                || !ev.getVehicleId().getUser().getId().equals(authUser.getId()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        eventVehicleService.deleteEventVehicle(id);
        return ResponseEntity.noContent().build();
    }
}