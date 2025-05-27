package com.udea.GPX;

import com.udea.GPX.controller.VehicleController;
import com.udea.GPX.model.Category;
import com.udea.GPX.model.User;
import com.udea.GPX.model.Vehicle;
import com.udea.GPX.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@SpringBootTest
public class VehicleControllerTests {

    @Mock
    private VehicleService vehicleService;

    @InjectMocks
    private VehicleController vehicleController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllVehicles_shouldReturnOK() {
        // Arrange
        List<Vehicle> vehicles = Arrays.asList(
                new Vehicle(1L, "Carro 1", "SOAT1", "ABC123", new Category(), new User()),
                new Vehicle(2L, "Carro 2", "SOAT2", "DEF456", new Category(), new User())
        );
        when(vehicleService.getAllVehicles()).thenReturn(vehicles);

        // Act
        ResponseEntity<List<Vehicle>> response = vehicleController.getAllVehicles();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    void getVehicleById_whenVehicleExists_shouldReturnOK() {
        // Arrange
        Long vehicleId = 1L;
        Vehicle vehicle = new Vehicle(vehicleId, "Carro 1", "SOAT1", "ABC123", new Category(), new User());
        when(vehicleService.getVehicleById(vehicleId)).thenReturn(Optional.of(vehicle));

        // Act
        ResponseEntity<Vehicle> response = vehicleController.getVehicleById(vehicleId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Carro 1", Objects.requireNonNull(response.getBody()).getName());
    }

    @Test
    void getVehicleById_whenVehicleNotExists_shouldReturnNotFound() {
        // Arrange
        Long vehicleId = 1L;
        when(vehicleService.getVehicleById(vehicleId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Vehicle> response = vehicleController.getVehicleById(vehicleId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void createVehicle_shouldReturnCreated() {
        // Arrange
        Vehicle vehicle = new Vehicle(1L, "Carro 1", "SOAT1", "ABC123", new Category(), new User());
        when(vehicleService.createVehicle(vehicle)).thenReturn(vehicle);

        // Act
        ResponseEntity<Vehicle> response = vehicleController.createVehicle(vehicle);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Carro 1", Objects.requireNonNull(response.getBody()).getName());
    }

    @Test
    void updateVehicle_whenVehicleExists_shouldReturnOK() {
        // Arrange
        Long vehicleId = 1L;
        Vehicle existingVehicle = new Vehicle(vehicleId, "Carro 1", "SOAT1", "ABC123", new Category(), new User());
        Vehicle updatedVehicle = new Vehicle(vehicleId, "Carro Actualizado", "SOAT2", "DEF456", new Category(), new User());
        when(vehicleService.updateVehicle(vehicleId, updatedVehicle)).thenReturn(updatedVehicle);
        when(vehicleService.getVehicleById(vehicleId)).thenReturn(Optional.of(existingVehicle));

        // Act
        ResponseEntity<Vehicle> response = vehicleController.updateVehicle(vehicleId, updatedVehicle);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Carro Actualizado", Objects.requireNonNull(response.getBody()).getName());
    }

    @Test
    void updateVehicle_whenVehicleNotExists_shouldReturnNotFound() {
        // Arrange
        Long vehicleId = 1L;
        Vehicle updatedVehicle = new Vehicle(vehicleId, "Carro Actualizado", "SOAT2", "DEF456", new Category(), new User());
        when(vehicleService.updateVehicle(vehicleId, updatedVehicle)).thenThrow(new RuntimeException("Vehículo no encontrado"));

        // Act
        ResponseEntity<Vehicle> response = vehicleController.updateVehicle(vehicleId, updatedVehicle);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getVehiclesByCategory_shouldReturnOK() {
        // Arrange
        Long categoryId = 1L;
        Category category = new Category();
        category.setId(categoryId);
        List<Vehicle> vehicles = Arrays.asList(
                new Vehicle(1L, "Carro 1", "SOAT1", "ABC123", category, new User()),
                new Vehicle(2L, "Carro 2", "SOAT2", "DEF456", category, new User())
        );
        when(vehicleService.getAllVehicles()).thenReturn(vehicles);

        // Act
        ResponseEntity<List<Vehicle>> response = vehicleController.getVehiclesByCategory(categoryId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    void getVehiclesByUser_shouldReturnOK() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        List<Vehicle> vehicles = Arrays.asList(
                new Vehicle(1L, "Carro 1", "SOAT1", "ABC123", new Category(), user),
                new Vehicle(2L, "Carro 2", "SOAT2", "DEF456", new Category(), user)
        );
        when(vehicleService.getAllVehicles()).thenReturn(vehicles);

        // Act
        ResponseEntity<List<Vehicle>> response = vehicleController.getVehiclesByUser(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    void deleteVehicle_shouldReturnNoContent() {
        // Arrange
        Long vehicleId = 1L;

        // Act
        ResponseEntity<Void> response = vehicleController.deleteVehicle(vehicleId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(vehicleService, times(1)).deleteVehicle(vehicleId);
    }
}