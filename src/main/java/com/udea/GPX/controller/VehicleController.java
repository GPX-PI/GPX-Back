package com.udea.GPX.controller;

import com.udea.GPX.model.Vehicle;
import com.udea.GPX.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import com.udea.GPX.model.User;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @SuppressWarnings("unused")
    @Autowired
    private HttpServletRequest request;

    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!authUser.isAdmin()) {
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
        User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Vehicle> vehicle = vehicleService.getVehicleById(id);
        if (vehicle.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (!authUser.isAdmin()
                && (vehicle.get().getUser() == null || !vehicle.get().getUser().getId().equals(authUser.getId()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(vehicle.get());
    }

    @PostMapping
    public ResponseEntity<Vehicle> createVehicle(@RequestBody Vehicle vehicle) {
        User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        vehicle.setUser(authUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vehicleService.createVehicle(vehicle));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable Long id, @RequestBody Vehicle vehicle) {
        User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Vehicle> existing = vehicleService.getVehicleById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (!authUser.isAdmin()
                && (existing.get().getUser() == null || !existing.get().getUser().getId().equals(authUser.getId()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        vehicle.setUser(existing.get().getUser());
        try {
            Vehicle updatedVehicle = vehicleService.updateVehicle(id, vehicle);
            return ResponseEntity.ok(updatedVehicle);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/bycategory/{categoryId}")
    public ResponseEntity<List<Vehicle>> getVehiclesByCategory(@PathVariable Long categoryId) {
        User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!authUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<Vehicle> vehicles = vehicleService.getAllVehicles().stream()
                .filter(v -> v.getCategory() != null && v.getCategory().getId().equals(categoryId))
                .toList();
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/byuser/{userId}")
    public ResponseEntity<List<Vehicle>> getVehiclesByUser(@PathVariable Long userId) {
        User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!authUser.isAdmin() && !authUser.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        List<Vehicle> vehicles = vehicleService.getAllVehicles().stream()
                .filter(v -> v.getUser() != null && v.getUser().getId().equals(userId))
                .toList();
        return ResponseEntity.ok(vehicles);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Vehicle> vehicle = vehicleService.getVehicleById(id);
        if (vehicle.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (!authUser.isAdmin()
                && (vehicle.get().getUser() == null || !vehicle.get().getUser().getId().equals(authUser.getId()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }
}