package com.udea.gpx;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.mockito.MockitoAnnotations;
import com.udea.gpx.controller.VehicleController;
import com.udea.gpx.dto.VehicleRequestDTO;
import com.udea.gpx.model.Category;
import com.udea.gpx.model.User;
import com.udea.gpx.model.Vehicle;
import com.udea.gpx.service.VehicleService;
import com.udea.gpx.util.AuthUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class VehicleControllerTests {

    @Mock
    private VehicleService vehicleService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private AuthUtils authUtils;

    @Mock
    private com.udea.gpx.repository.IEventVehicleRepository eventVehicleRepository;

    @Mock
    private com.udea.gpx.repository.IStageResultRepository stageResultRepository;

    @Mock
    private com.udea.gpx.service.UserService userService;

    @Mock
    private com.udea.gpx.service.CategoryService categoryService;

    @InjectMocks
    private VehicleController vehicleController;

    private User buildUser(Long id, boolean admin) {
        User user = new User();
        user.setId(id);
        user.setAdmin(admin);
        return user;
    }

    private Vehicle buildVehicle(Long id, Long userId, boolean admin) {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(id);
        User user = buildUser(userId, admin);
        vehicle.setUser(user);
        return vehicle;
    }

    private VehicleRequestDTO buildVehicleRequestDTO(Long categoryId) {
        VehicleRequestDTO dto = new VehicleRequestDTO();
        dto.setName("Vehículo Test");
        dto.setSoat("SOAT123");
        dto.setPlates("ABC123");
        dto.setCategoryId(categoryId);
        return dto;
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        ReflectionTestUtils.setField(vehicleController, "request", request);
        ReflectionTestUtils.setField(vehicleController, "authUtils", authUtils);

        // Mock repository responses to avoid NullPointerException
        when(eventVehicleRepository.findAll()).thenReturn(java.util.Collections.emptyList());
        when(stageResultRepository.findAll()).thenReturn(java.util.Collections.emptyList());
    }

    @Test
    void getAllVehicles_whenAdmin_shouldReturnOK() {
        // Arrange
        User adminUser = buildUser(1L, true);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        List<Vehicle> vehicles = Arrays.asList(
                buildVehicle(1L, 10L, false),
                buildVehicle(2L, 20L, false));
        when(vehicleService.getAllVehicles()).thenReturn(vehicles);

        // Act
        ResponseEntity<List<Vehicle>> response = vehicleController.getAllVehicles();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Vehicle> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(2, responseBody.size());
    }

    @Test
    void getAllVehicles_whenNotAdmin_shouldReturnForbidden() {
        // Arrange
        User nonAdminUser = buildUser(1L, false);
        when(authentication.getPrincipal()).thenReturn(nonAdminUser);

        // Act
        ResponseEntity<List<Vehicle>> response = vehicleController.getAllVehicles();

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getVehicleById_whenAdmin_shouldReturnOK() {
        // Arrange
        Long vehicleId = 1L;
        User adminUser = buildUser(99L, true);
        Vehicle vehicle = buildVehicle(vehicleId, 10L, false);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        when(vehicleService.getVehicleById(vehicleId)).thenReturn(Optional.of(vehicle));
        when(authUtils.isCurrentUserOrAdmin(anyLong())).thenReturn(true);

        // Act
        ResponseEntity<Vehicle> response = vehicleController.getVehicleById(vehicleId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Vehicle responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(vehicleId, responseBody.getId());
    }

    @Test
    void getVehicleById_whenOwner_shouldReturnOK() {
        // Arrange
        Long vehicleId = 1L;
        User ownerUser = buildUser(10L, false);
        Vehicle vehicle = buildVehicle(vehicleId, 10L, false);
        when(authentication.getPrincipal()).thenReturn(ownerUser);
        when(vehicleService.getVehicleById(vehicleId)).thenReturn(Optional.of(vehicle));
        when(authUtils.isCurrentUserOrAdmin(anyLong())).thenReturn(true);

        // Act
        ResponseEntity<Vehicle> response = vehicleController.getVehicleById(vehicleId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Vehicle responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(vehicleId, responseBody.getId());
    }

    @Test
    void getVehicleById_whenNotOwnerNorAdmin_shouldReturnForbidden() {
        // Arrange
        Long vehicleId = 1L;
        User otherUser = buildUser(20L, false);
        Vehicle vehicle = buildVehicle(vehicleId, 10L, false);
        when(authentication.getPrincipal()).thenReturn(otherUser);
        when(vehicleService.getVehicleById(vehicleId)).thenReturn(Optional.of(vehicle));

        // Act
        ResponseEntity<Vehicle> response = vehicleController.getVehicleById(vehicleId);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void getVehicleById_whenNotFound_shouldReturnNotFound() {
        // Arrange
        Long vehicleId = 1L;
        User adminUser = buildUser(99L, true);
        when(authentication.getPrincipal()).thenReturn(adminUser);
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
        User ownerUser = buildUser(10L, false);
        Vehicle vehicle = buildVehicle(1L, 10L, false);
        VehicleRequestDTO vehicleDTO = buildVehicleRequestDTO(1L);
        Category category = new Category();
        category.setId(1L);
        when(authentication.getPrincipal()).thenReturn(ownerUser);
        when(categoryService.getCategoryById(1L)).thenReturn(category);
        when(vehicleService.createVehicle(any(Vehicle.class))).thenReturn(vehicle);

        // Act
        ResponseEntity<Vehicle> response = vehicleController.createVehicle(vehicleDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Vehicle responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(vehicle.getId(), responseBody.getId());
    }

    @Test
    void updateVehicle_whenAdmin_shouldReturnOK() {
        // Arrange
        Long vehicleId = 1L;
        User adminUser = buildUser(99L, true);
        Vehicle existingVehicle = buildVehicle(vehicleId, 10L, false);
        Vehicle updatedVehicle = buildVehicle(vehicleId, 10L, false);
        VehicleRequestDTO vehicleDTO = buildVehicleRequestDTO(1L);
        Category category = new Category();
        category.setId(1L);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        when(vehicleService.getVehicleById(vehicleId)).thenReturn(Optional.of(existingVehicle));
        when(categoryService.getCategoryById(1L)).thenReturn(category);
        when(vehicleService.updateVehicle(vehicleId, existingVehicle)).thenReturn(updatedVehicle);
        when(authUtils.isCurrentUserOrAdmin(anyLong())).thenReturn(true);

        // Act
        ResponseEntity<Vehicle> response = vehicleController.updateVehicle(vehicleId, vehicleDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Vehicle responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(vehicleId, responseBody.getId());
    }

    @Test
    void updateVehicle_whenOwner_shouldReturnOK() {
        // Arrange
        Long vehicleId = 1L;
        User ownerUser = buildUser(10L, false);
        Vehicle existingVehicle = buildVehicle(vehicleId, 10L, false);
        Vehicle updatedVehicle = buildVehicle(vehicleId, 10L, false);
        VehicleRequestDTO vehicleDTO = buildVehicleRequestDTO(1L);
        Category category = new Category();
        category.setId(1L);
        when(authentication.getPrincipal()).thenReturn(ownerUser);
        when(vehicleService.getVehicleById(vehicleId)).thenReturn(Optional.of(existingVehicle));
        when(categoryService.getCategoryById(1L)).thenReturn(category);
        when(vehicleService.updateVehicle(vehicleId, existingVehicle)).thenReturn(updatedVehicle);
        when(authUtils.isCurrentUserOrAdmin(anyLong())).thenReturn(true);

        // Act
        ResponseEntity<Vehicle> response = vehicleController.updateVehicle(vehicleId, vehicleDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Vehicle responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(vehicleId, responseBody.getId());
    }

    @Test
    void updateVehicle_whenNotOwnerNorAdmin_shouldReturnForbidden() {
        // Arrange
        Long vehicleId = 1L;
        User otherUser = buildUser(20L, false);
        Vehicle existingVehicle = buildVehicle(vehicleId, 10L, false);
        VehicleRequestDTO vehicleDTO = buildVehicleRequestDTO(1L);
        when(authentication.getPrincipal()).thenReturn(otherUser);
        when(vehicleService.getVehicleById(vehicleId)).thenReturn(Optional.of(existingVehicle));

        // Act
        ResponseEntity<Vehicle> response = vehicleController.updateVehicle(vehicleId, vehicleDTO);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void updateVehicle_whenNotFound_shouldReturnNotFound() {
        // Arrange
        Long vehicleId = 1L;
        User adminUser = buildUser(99L, true);
        VehicleRequestDTO vehicleDTO = buildVehicleRequestDTO(1L);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        when(vehicleService.getVehicleById(vehicleId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Vehicle> response = vehicleController.updateVehicle(vehicleId, vehicleDTO);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getVehiclesByCategory_whenAdmin_shouldReturnOK() {
        // Arrange
        Long categoryId = 1L;
        User adminUser = buildUser(1L, true);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        Category category = new Category();
        category.setId(categoryId);
        List<Vehicle> vehicles = Arrays.asList(
                buildVehicle(1L, 10L, false),
                buildVehicle(2L, 20L, false));
        vehicles.get(0).setCategory(category);
        vehicles.get(1).setCategory(category);
        when(vehicleService.getAllVehicles()).thenReturn(vehicles);

        // Act
        ResponseEntity<List<Vehicle>> response = vehicleController.getVehiclesByCategory(categoryId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Vehicle> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(2, responseBody.size());
    }

    @Test
    void getVehiclesByCategory_whenNotAdmin_shouldReturnForbidden() {
        // Arrange
        Long categoryId = 1L;
        User nonAdminUser = buildUser(1L, false);
        when(authentication.getPrincipal()).thenReturn(nonAdminUser);

        // Act
        ResponseEntity<List<Vehicle>> response = vehicleController.getVehiclesByCategory(categoryId);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getVehiclesByUser_whenAdmin_shouldReturnOK() {
        // Arrange
        Long userId = 10L;
        User adminUser = buildUser(99L, true);
        when(authUtils.isCurrentUserOrAdmin(userId)).thenReturn(true);
        List<Vehicle> vehicles = Arrays.asList(
                buildVehicle(1L, userId, false),
                buildVehicle(2L, userId, false));
        when(authentication.getPrincipal()).thenReturn(adminUser);
        when(vehicleService.getAllVehicles()).thenReturn(vehicles);

        // Act
        ResponseEntity<List<Vehicle>> response = vehicleController.getVehiclesByUser(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Vehicle> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(2, responseBody.size());
    }

    @Test
    void getVehiclesByUser_whenOwner_shouldReturnOK() {
        // Arrange
        Long userId = 10L;
        User ownerUser = buildUser(userId, false);
        when(authUtils.isCurrentUserOrAdmin(userId)).thenReturn(true);
        List<Vehicle> vehicles = Arrays.asList(
                buildVehicle(1L, userId, false),
                buildVehicle(2L, userId, false));
        when(authentication.getPrincipal()).thenReturn(ownerUser);
        when(vehicleService.getAllVehicles()).thenReturn(vehicles);

        // Act
        ResponseEntity<List<Vehicle>> response = vehicleController.getVehiclesByUser(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Vehicle> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(2, responseBody.size());
    }

    @Test
    void getVehiclesByUser_whenNotOwnerNorAdmin_shouldReturnForbidden() {
        // Arrange
        Long userId = 10L;
        User otherUser = buildUser(20L, false);
        when(authentication.getPrincipal()).thenReturn(otherUser);

        // Act
        ResponseEntity<List<Vehicle>> response = vehicleController.getVehiclesByUser(userId);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void deleteVehicle_whenAdmin_shouldReturnNoContent() {
        // Arrange
        Long vehicleId = 1L;
        User adminUser = buildUser(99L, true);
        Vehicle vehicle = buildVehicle(vehicleId, 10L, false);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        when(vehicleService.getVehicleById(vehicleId)).thenReturn(Optional.of(vehicle));
        when(authUtils.isCurrentUserOrAdmin(anyLong())).thenReturn(true);

        // Act
        ResponseEntity<?> response = vehicleController.deleteVehicle(vehicleId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(vehicleService, times(1)).deleteVehicle(vehicleId);
    }

    @Test
    void deleteVehicle_whenOwner_shouldReturnNoContent() {
        // Arrange
        Long vehicleId = 1L;
        User ownerUser = buildUser(10L, false);
        Vehicle vehicle = buildVehicle(vehicleId, 10L, false);
        when(authentication.getPrincipal()).thenReturn(ownerUser);
        when(vehicleService.getVehicleById(vehicleId)).thenReturn(Optional.of(vehicle));
        when(authUtils.isCurrentUserOrAdmin(anyLong())).thenReturn(true);

        // Act
        ResponseEntity<?> response = vehicleController.deleteVehicle(vehicleId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(vehicleService, times(1)).deleteVehicle(vehicleId);
    }

    @Test
    void deleteVehicle_whenNotOwnerNorAdmin_shouldReturnForbidden() {
        // Arrange
        Long vehicleId = 1L;
        User otherUser = buildUser(20L, false);
        Vehicle vehicle = buildVehicle(vehicleId, 10L, false);
        when(authentication.getPrincipal()).thenReturn(otherUser);
        when(vehicleService.getVehicleById(vehicleId)).thenReturn(Optional.of(vehicle));

        // Act
        ResponseEntity<?> response = vehicleController.deleteVehicle(vehicleId);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(vehicleService, never()).deleteVehicle(vehicleId);
    }

    @Test
    void deleteVehicle_whenNotFound_shouldReturnNotFound() {
        // Arrange
        Long vehicleId = 1L;
        User adminUser = buildUser(99L, true);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        when(vehicleService.getVehicleById(vehicleId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = vehicleController.deleteVehicle(vehicleId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(vehicleService, never()).deleteVehicle(vehicleId);
    }
}
