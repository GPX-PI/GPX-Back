package com.udea.gpx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.udea.gpx.dto.VehicleRequestDTO;
import com.udea.gpx.model.Category;
import com.udea.gpx.model.User;
import com.udea.gpx.model.Vehicle;
import com.udea.gpx.repository.IEventVehicleRepository;
import com.udea.gpx.repository.IStageResultRepository;
import com.udea.gpx.service.CategoryService;
import com.udea.gpx.service.UserService;
import com.udea.gpx.service.VehicleService;
import com.udea.gpx.util.AuthUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Map;
import java.util.HashMap;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private IEventVehicleRepository eventVehicleRepository;

    @Autowired
    private IStageResultRepository stageResultRepository;

    @SuppressWarnings("unused")
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private AuthUtils authUtils;

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
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        if (!authUtils.isCurrentUserAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @CrossOrigin(origins = "*")
    @ResponseBody
    /**
     * Retrieves a vehicle by its ID.
     *
     * @param id the ID of the vehicle to retrieve
     * @return ResponseEntity containing the vehicle if found, or 404 Not Found if
     *         not found
     */
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {
        Optional<Vehicle> vehicle = vehicleService.getVehicleById(id);
        if (vehicle.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (!authUtils.isCurrentUserOrAdmin(vehicle.get().getUser() != null ? vehicle.get().getUser().getId() : null)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(vehicle.get());
    }

    @PostMapping
    public ResponseEntity<Vehicle> createVehicle(@Valid @RequestBody VehicleRequestDTO vehicleData) {
        User authUser = getAuthenticatedUser();

        try {
            Vehicle vehicle = new Vehicle();
            vehicle.setName(vehicleData.getName());
            vehicle.setSoat(vehicleData.getSoat());
            vehicle.setPlates(vehicleData.getPlates());
            vehicle.setUser(authUser);

            // Manejar categoryId
            Category category = categoryService.getCategoryById(vehicleData.getCategoryId());
            if (category == null) {
                return ResponseEntity.badRequest().build();
            }
            vehicle.setCategory(category);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(vehicleService.createVehicle(vehicle));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable Long id,
            @Valid @RequestBody VehicleRequestDTO vehicleData) {
        Optional<Vehicle> existing = vehicleService.getVehicleById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (!authUtils
                .isCurrentUserOrAdmin(existing.get().getUser() != null ? existing.get().getUser().getId() : null)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            Vehicle vehicle = existing.get();
            vehicle.setName(vehicleData.getName());
            vehicle.setSoat(vehicleData.getSoat());
            vehicle.setPlates(vehicleData.getPlates());

            // Manejar categoryId
            Category category = categoryService.getCategoryById(vehicleData.getCategoryId());
            if (category == null) {
                return ResponseEntity.badRequest().build();
            }
            vehicle.setCategory(category);

            Vehicle updatedVehicle = vehicleService.updateVehicle(id, vehicle);
            return ResponseEntity.ok(updatedVehicle);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/bycategory/{categoryId}")
    public ResponseEntity<List<Vehicle>> getVehiclesByCategory(@PathVariable Long categoryId) {
        if (!authUtils.isCurrentUserAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<Vehicle> vehicles = vehicleService.getAllVehicles().stream()
                .filter(v -> v.getCategory() != null && v.getCategory().getId().equals(categoryId))
                .toList();
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/byuser/{userId}")
    public ResponseEntity<List<Vehicle>> getVehiclesByUser(@PathVariable Long userId) {
        if (!authUtils.isCurrentUserOrAdmin(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        List<Vehicle> vehicles = vehicleService.getAllVehicles().stream()
                .filter(v -> v.getUser() != null && v.getUser().getId().equals(userId))
                .toList();
        return ResponseEntity.ok(vehicles);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVehicle(@PathVariable Long id) {
        Optional<Vehicle> vehicleOpt = vehicleService.getVehicleById(id);
        if (vehicleOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Vehicle vehicle = vehicleOpt.get();
        if (!authUtils.isCurrentUserOrAdmin(vehicle.getUser() != null ? vehicle.getUser().getId() : null)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Verificar si el vehículo está vinculado a eventos o resultados
        boolean hasEventVehicles = eventVehicleRepository.findAll().stream()
                .anyMatch(ev -> ev.getVehicleId() != null && ev.getVehicleId().getId().equals(id));

        boolean hasStageResults = stageResultRepository.findAll().stream()
                .anyMatch(sr -> sr.getVehicle() != null && sr.getVehicle().getId().equals(id));

        if (hasEventVehicles || hasStageResults) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "No se puede eliminar el vehículo");
            errorResponse.put("message", "Este vehículo está vinculado a eventos y/o resultados de competencias. " +
                    "No es posible eliminarlo para mantener la integridad de los datos históricos.");
            errorResponse.put("vehicleName", vehicle.getName());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

        try {
            vehicleService.deleteVehicle(id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error de integridad de datos");
            errorResponse.put("message",
                    "No se puede eliminar el vehículo debido a restricciones de la base de datos. " +
                            "El vehículo está relacionado con otros registros del sistema.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }
}