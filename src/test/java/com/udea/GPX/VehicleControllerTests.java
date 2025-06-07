//package com.udea.GPX;
//
//import com.udea.GPX.controller.VehicleController;
//import com.udea.GPX.model.Category;
//import com.udea.GPX.model.User;
//import com.udea.GPX.model.Vehicle;
//import com.udea.GPX.service.VehicleService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.test.util.ReflectionTestUtils;
//import jakarta.servlet.http.HttpServletRequest;
//import org.junit.jupiter.api.Disabled;
//
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Objects;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNull;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest
//@Disabled("No se ejecuta en la CI/CD")
//
//public class VehicleControllerTests {
//
//    @Mock
//    private VehicleService vehicleService;
//
//    @Mock
//    private HttpServletRequest request;
//
//    @Mock
//    private SecurityContext securityContext;
//
//    @Mock
//    private Authentication authentication;
//
//    @InjectMocks
//    private VehicleController vehicleController;
//
//    private User buildUser(Long id, boolean admin) {
//        User user = new User();
//        user.setId(id);
//        user.setAdmin(admin);
//        return user;
//    }
//
//    private Vehicle buildVehicle(Long id, Long userId, boolean admin) {
//        Vehicle vehicle = new Vehicle();
//        vehicle.setId(id);
//        User user = buildUser(userId, admin);
//        vehicle.setUser(user);
//        return vehicle;
//    }
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        SecurityContextHolder.setContext(securityContext);
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        ReflectionTestUtils.setField(vehicleController, "request", request);
//    }
//
//    @Test
//    void getAllVehicles_whenAdmin_shouldReturnOK() {
//        // Arrange
//        User adminUser = buildUser(1L, true);
//        when(authentication.getPrincipal()).thenReturn(adminUser);
//        List<Vehicle> vehicles = Arrays.asList(
//                buildVehicle(1L, 10L, false),
//                buildVehicle(2L, 20L, false));
//        when(vehicleService.getAllVehicles()).thenReturn(vehicles);
//
//        // Act
//        ResponseEntity<List<Vehicle>> response = vehicleController.getAllVehicles();
//
//        // Assert
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
//    }
//
//    @Test
//    void getAllVehicles_whenNotAdmin_shouldReturnForbidden() {
//        // Arrange
//        User nonAdminUser = buildUser(1L, false);
//        when(authentication.getPrincipal()).thenReturn(nonAdminUser);
//
//        // Act
//        ResponseEntity<List<Vehicle>> response = vehicleController.getAllVehicles();
//
//        // Assert
//        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
//        assertNull(response.getBody());
//    }
//
//    @Test
//    void getVehicleById_whenAdmin_shouldReturnOK() {
//        // Arrange
//        Long vehicleId = 1L;
//        User adminUser = buildUser(99L, true);
//        Vehicle vehicle = buildVehicle(vehicleId, 10L, false);
//        when(authentication.getPrincipal()).thenReturn(adminUser);
//        when(vehicleService.getVehicleById(vehicleId)).thenReturn(Optional.of(vehicle));
//
//        // Act
//        ResponseEntity<Vehicle> response = vehicleController.getVehicleById(vehicleId);
//
//        // Assert
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(vehicleId, Objects.requireNonNull(response.getBody()).getId());
//    }
//
//    @Test
//    void getVehicleById_whenOwner_shouldReturnOK() {
//        // Arrange
//        Long vehicleId = 1L;
//        User ownerUser = buildUser(10L, false);
//        Vehicle vehicle = buildVehicle(vehicleId, 10L, false);
//        when(authentication.getPrincipal()).thenReturn(ownerUser);
//        when(vehicleService.getVehicleById(vehicleId)).thenReturn(Optional.of(vehicle));
//
//        // Act
//        ResponseEntity<Vehicle> response = vehicleController.getVehicleById(vehicleId);
//
//        // Assert
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(vehicleId, Objects.requireNonNull(response.getBody()).getId());
//    }
//
//    @Test
//    void getVehicleById_whenNotOwnerNorAdmin_shouldReturnForbidden() {
//        // Arrange
//        Long vehicleId = 1L;
//        User otherUser = buildUser(20L, false);
//        Vehicle vehicle = buildVehicle(vehicleId, 10L, false);
//        when(authentication.getPrincipal()).thenReturn(otherUser);
//        when(vehicleService.getVehicleById(vehicleId)).thenReturn(Optional.of(vehicle));
//
//        // Act
//        ResponseEntity<Vehicle> response = vehicleController.getVehicleById(vehicleId);
//
//        // Assert
//        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
//    }
//
//    @Test
//    void getVehicleById_whenNotFound_shouldReturnNotFound() {
//        // Arrange
//        Long vehicleId = 1L;
//        User adminUser = buildUser(99L, true);
//        when(authentication.getPrincipal()).thenReturn(adminUser);
//        when(vehicleService.getVehicleById(vehicleId)).thenReturn(Optional.empty());
//
//        // Act
//        ResponseEntity<Vehicle> response = vehicleController.getVehicleById(vehicleId);
//
//        // Assert
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//        assertNull(response.getBody());
//    }
//
//    @Test
//    void createVehicle_shouldReturnCreated() {
//        // Arrange
//        User ownerUser = buildUser(10L, false);
//        Vehicle vehicle = buildVehicle(1L, 10L, false);
//        when(authentication.getPrincipal()).thenReturn(ownerUser);
//        when(vehicleService.createVehicle(any(Vehicle.class))).thenReturn(vehicle);
//
//        // Act
//        ResponseEntity<Vehicle> response = vehicleController.createVehicle(vehicle);
//
//        // Assert
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertEquals(vehicle.getId(), Objects.requireNonNull(response.getBody()).getId());
//    }
//
//    @Test
//    void updateVehicle_whenAdmin_shouldReturnOK() {
//        // Arrange
//        Long vehicleId = 1L;
//        User adminUser = buildUser(99L, true);
//        Vehicle existingVehicle = buildVehicle(vehicleId, 10L, false);
//        Vehicle updatedVehicle = buildVehicle(vehicleId, 10L, false);
//        when(authentication.getPrincipal()).thenReturn(adminUser);
//        when(vehicleService.getVehicleById(vehicleId)).thenReturn(Optional.of(existingVehicle));
//        when(vehicleService.updateVehicle(vehicleId, updatedVehicle)).thenReturn(updatedVehicle);
//
//        // Act
//        ResponseEntity<Vehicle> response = vehicleController.updateVehicle(vehicleId, updatedVehicle);
//
//        // Assert
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(vehicleId, Objects.requireNonNull(response.getBody()).getId());
//    }
//
//    @Test
//    void updateVehicle_whenOwner_shouldReturnOK() {
//        // Arrange
//        Long vehicleId = 1L;
//        User ownerUser = buildUser(10L, false);
//        Vehicle existingVehicle = buildVehicle(vehicleId, 10L, false);
//        Vehicle updatedVehicle = buildVehicle(vehicleId, 10L, false);
//        when(authentication.getPrincipal()).thenReturn(ownerUser);
//        when(vehicleService.getVehicleById(vehicleId)).thenReturn(Optional.of(existingVehicle));
//        when(vehicleService.updateVehicle(vehicleId, updatedVehicle)).thenReturn(updatedVehicle);
//
//        // Act
//        ResponseEntity<Vehicle> response = vehicleController.updateVehicle(vehicleId, updatedVehicle);
//
//        // Assert
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(vehicleId, Objects.requireNonNull(response.getBody()).getId());
//    }
//
//    @Test
//    void updateVehicle_whenNotOwnerNorAdmin_shouldReturnForbidden() {
//        // Arrange
//        Long vehicleId = 1L;
//        User otherUser = buildUser(20L, false);
//        Vehicle existingVehicle = buildVehicle(vehicleId, 10L, false);
//        Vehicle updatedVehicle = buildVehicle(vehicleId, 10L, false);
//        when(authentication.getPrincipal()).thenReturn(otherUser);
//        when(vehicleService.getVehicleById(vehicleId)).thenReturn(Optional.of(existingVehicle));
//
//        // Act
//        ResponseEntity<Vehicle> response = vehicleController.updateVehicle(vehicleId, updatedVehicle);
//
//        // Assert
//        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
//    }
//
//    @Test
//    void updateVehicle_whenNotFound_shouldReturnNotFound() {
//        // Arrange
//        Long vehicleId = 1L;
//        User adminUser = buildUser(99L, true);
//        Vehicle updatedVehicle = buildVehicle(vehicleId, 10L, false);
//        when(authentication.getPrincipal()).thenReturn(adminUser);
//        when(vehicleService.getVehicleById(vehicleId)).thenReturn(Optional.empty());
//
//        // Act
//        ResponseEntity<Vehicle> response = vehicleController.updateVehicle(vehicleId, updatedVehicle);
//
//        // Assert
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//        assertNull(response.getBody());
//    }
//
//    @Test
//    void getVehiclesByCategory_whenAdmin_shouldReturnOK() {
//        // Arrange
//        Long categoryId = 1L;
//        User adminUser = buildUser(1L, true);
//        when(authentication.getPrincipal()).thenReturn(adminUser);
//        Category category = new Category();
//        category.setId(categoryId);
//        List<Vehicle> vehicles = Arrays.asList(
//                buildVehicle(1L, 10L, false),
//                buildVehicle(2L, 20L, false));
//        vehicles.get(0).setCategory(category);
//        vehicles.get(1).setCategory(category);
//        when(vehicleService.getAllVehicles()).thenReturn(vehicles);
//
//        // Act
//        ResponseEntity<List<Vehicle>> response = vehicleController.getVehiclesByCategory(categoryId);
//
//        // Assert
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
//    }
//
//    @Test
//    void getVehiclesByCategory_whenNotAdmin_shouldReturnForbidden() {
//        // Arrange
//        Long categoryId = 1L;
//        User nonAdminUser = buildUser(1L, false);
//        when(authentication.getPrincipal()).thenReturn(nonAdminUser);
//
//        // Act
//        ResponseEntity<List<Vehicle>> response = vehicleController.getVehiclesByCategory(categoryId);
//
//        // Assert
//        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
//        assertNull(response.getBody());
//    }
//
//    @Test
//    void getVehiclesByUser_whenAdmin_shouldReturnOK() {
//        // Arrange
//        Long userId = 10L;
//        User adminUser = buildUser(99L, true);
//        List<Vehicle> vehicles = Arrays.asList(
//                buildVehicle(1L, userId, false),
//                buildVehicle(2L, userId, false));
//        when(authentication.getPrincipal()).thenReturn(adminUser);
//        when(vehicleService.getAllVehicles()).thenReturn(vehicles);
//
//        // Act
//        ResponseEntity<List<Vehicle>> response = vehicleController.getVehiclesByUser(userId);
//
//        // Assert
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
//    }
//
//    @Test
//    void getVehiclesByUser_whenOwner_shouldReturnOK() {
//        // Arrange
//        Long userId = 10L;
//        User ownerUser = buildUser(userId, false);
//        List<Vehicle> vehicles = Arrays.asList(
//                buildVehicle(1L, userId, false),
//                buildVehicle(2L, userId, false));
//        when(authentication.getPrincipal()).thenReturn(ownerUser);
//        when(vehicleService.getAllVehicles()).thenReturn(vehicles);
//
//        // Act
//        ResponseEntity<List<Vehicle>> response = vehicleController.getVehiclesByUser(userId);
//
//        // Assert
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
//    }
//
//    @Test
//    void getVehiclesByUser_whenNotOwnerNorAdmin_shouldReturnForbidden() {
//        // Arrange
//        Long userId = 10L;
//        User otherUser = buildUser(20L, false);
//        when(authentication.getPrincipal()).thenReturn(otherUser);
//
//        // Act
//        ResponseEntity<List<Vehicle>> response = vehicleController.getVehiclesByUser(userId);
//
//        // Assert
//        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
//        assertNull(response.getBody());
//    }
//
//    @Test
//    void deleteVehicle_whenAdmin_shouldReturnNoContent() {
//        // Arrange
//        Long vehicleId = 1L;
//        User adminUser = buildUser(99L, true);
//        Vehicle vehicle = buildVehicle(vehicleId, 10L, false);
//        when(authentication.getPrincipal()).thenReturn(adminUser);
//        when(vehicleService.getVehicleById(vehicleId)).thenReturn(Optional.of(vehicle));
//
//        // Act
//        ResponseEntity<Void> response = vehicleController.deleteVehicle(vehicleId);
//
//        // Assert
//        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
//        verify(vehicleService, times(1)).deleteVehicle(vehicleId);
//    }
//
//    @Test
//    void deleteVehicle_whenOwner_shouldReturnNoContent() {
//        // Arrange
//        Long vehicleId = 1L;
//        User ownerUser = buildUser(10L, false);
//        Vehicle vehicle = buildVehicle(vehicleId, 10L, false);
//        when(authentication.getPrincipal()).thenReturn(ownerUser);
//        when(vehicleService.getVehicleById(vehicleId)).thenReturn(Optional.of(vehicle));
//
//        // Act
//        ResponseEntity<Void> response = vehicleController.deleteVehicle(vehicleId);
//
//        // Assert
//        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
//        verify(vehicleService, times(1)).deleteVehicle(vehicleId);
//    }
//
//    @Test
//    void deleteVehicle_whenNotOwnerNorAdmin_shouldReturnForbidden() {
//        // Arrange
//        Long vehicleId = 1L;
//        User otherUser = buildUser(20L, false);
//        Vehicle vehicle = buildVehicle(vehicleId, 10L, false);
//        when(authentication.getPrincipal()).thenReturn(otherUser);
//        when(vehicleService.getVehicleById(vehicleId)).thenReturn(Optional.of(vehicle));
//
//        // Act
//        ResponseEntity<Void> response = vehicleController.deleteVehicle(vehicleId);
//
//        // Assert
//        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
//        verify(vehicleService, never()).deleteVehicle(vehicleId);
//    }
//
//    @Test
//    void deleteVehicle_whenNotFound_shouldReturnNotFound() {
//        // Arrange
//        Long vehicleId = 1L;
//        User adminUser = buildUser(99L, true);
//        when(authentication.getPrincipal()).thenReturn(adminUser);
//        when(vehicleService.getVehicleById(vehicleId)).thenReturn(Optional.empty());
//
//        // Act
//        ResponseEntity<Void> response = vehicleController.deleteVehicle(vehicleId);
//
//        // Assert
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//        verify(vehicleService, never()).deleteVehicle(vehicleId);
//    }
//}