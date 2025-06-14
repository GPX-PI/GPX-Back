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

import com.udea.gpx.controller.VehicleController;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class VehicleControllerTests {

    @Mock
    private VehicleService vehicleService;

    @Mock
    private UserService userService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private IEventVehicleRepository eventVehicleRepository;

    @Mock
    private IStageResultRepository stageResultRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private AuthUtils authUtils;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private VehicleController vehicleController;

    private User buildUser(Long id, boolean admin) {
        User user = new User();
        user.setId(id);
        user.setAdmin(admin);
        user.setEmail("user" + id + "@test.com");
        user.setFirstName("User " + id);
        return user;
    }

    private Vehicle buildVehicle(Long id, Long userId, boolean admin) {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(id);
        vehicle.setName("Vehicle " + id);
        vehicle.setSoat("SOAT" + id);
        vehicle.setPlates("ABC" + id);

        User user = buildUser(userId, admin);
        vehicle.setUser(user);
        return vehicle;
    }

    private Category buildCategory(Long id) {
        Category category = new Category();
        category.setId(id);
        category.setName("Category " + id);
        return category;
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
        // Configurar SecurityContext
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Mock repository responses para evitar NullPointerException
        when(eventVehicleRepository.findAll()).thenReturn(Collections.emptyList());
        when(stageResultRepository.findAll()).thenReturn(Collections.emptyList());
    }

    @Test
    void createVehicle_shouldReturnCreated() {
        // Arrange
        User ownerUser = buildUser(10L, false);
        Vehicle createdVehicle = buildVehicle(1L, 10L, false);
        VehicleRequestDTO vehicleDTO = buildVehicleRequestDTO(1L);
        Category category = buildCategory(1L);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(ownerUser);
        when(categoryService.getCategoryById(1L)).thenReturn(category);
        when(vehicleService.createVehicle(any(Vehicle.class))).thenReturn(createdVehicle);

        // Act
        ResponseEntity<Object> response = vehicleController.createVehicle(vehicleDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody() instanceof Vehicle);
        Vehicle responseBody = (Vehicle) response.getBody();
        assertNotNull(responseBody);
        assertEquals(createdVehicle.getId(), responseBody.getId());
    }

    @Test
    void createVehicle_whenCategoryNotFound_shouldReturnBadRequest() {
        // Arrange
        User ownerUser = buildUser(10L, false);
        VehicleRequestDTO vehicleDTO = buildVehicleRequestDTO(1L);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(ownerUser);
        when(categoryService.getCategoryById(1L)).thenReturn(null);

        // Act
        ResponseEntity<Object> response = vehicleController.createVehicle(vehicleDTO);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof java.util.Map);
        @SuppressWarnings("unchecked")
        java.util.Map<String, String> body = (java.util.Map<String, String>) response.getBody();
        assertEquals("Categoría no válida", body.get("error"));
    }

    @Test
    void updateVehicle_whenAdmin_shouldReturnOK() {
        // Arrange
        Long vehicleId = 1L;
        User adminUser = buildUser(99L, true);
        Vehicle existingVehicle = buildVehicle(vehicleId, 10L, false);
        Vehicle updatedVehicle = buildVehicle(vehicleId, 10L, false);
        VehicleRequestDTO vehicleDTO = buildVehicleRequestDTO(1L);
        Category category = buildCategory(1L);

        when(authentication.getPrincipal()).thenReturn(adminUser);
        when(vehicleService.getVehicleById(vehicleId)).thenReturn(Optional.of(existingVehicle));
        when(authUtils.isCurrentUserOrAdmin(10L)).thenReturn(true);
        when(categoryService.getCategoryById(1L)).thenReturn(category);
        when(vehicleService.updateVehicle(eq(vehicleId), any(Vehicle.class))).thenReturn(updatedVehicle);

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
        Category category = buildCategory(1L);

        when(authentication.getPrincipal()).thenReturn(ownerUser);
        when(vehicleService.getVehicleById(vehicleId)).thenReturn(Optional.of(existingVehicle));
        when(authUtils.isCurrentUserOrAdmin(10L)).thenReturn(true);
        when(categoryService.getCategoryById(1L)).thenReturn(category);
        when(vehicleService.updateVehicle(eq(vehicleId), any(Vehicle.class))).thenReturn(updatedVehicle);

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
        when(authUtils.isCurrentUserOrAdmin(10L)).thenReturn(false);

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
        Category category = buildCategory(categoryId);

        when(authentication.getPrincipal()).thenReturn(adminUser);
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);

        List<Vehicle> allVehicles = Arrays.asList(
                buildVehicle(1L, 10L, false),
                buildVehicle(2L, 20L, false),
                buildVehicle(3L, 30L, false));

        // Asignar categorías
        allVehicles.get(0).setCategory(category);
        allVehicles.get(1).setCategory(category);
        allVehicles.get(2).setCategory(buildCategory(2L)); // Diferente categoría

        when(vehicleService.getAllVehicles()).thenReturn(allVehicles);

        // Act
        ResponseEntity<List<Vehicle>> response = vehicleController.getVehiclesByCategory(categoryId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Vehicle> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(2, responseBody.size()); // Solo 2 vehículos de la categoría 1
    }

    @Test
    void getVehiclesByCategory_whenNotAdmin_shouldReturnForbidden() {
        // Arrange
        Long categoryId = 1L;
        User nonAdminUser = buildUser(1L, false);

        when(authentication.getPrincipal()).thenReturn(nonAdminUser);
        when(authUtils.isCurrentUserAdmin()).thenReturn(false);

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
        when(authentication.getPrincipal()).thenReturn(adminUser);

        List<Vehicle> allVehicles = Arrays.asList(
                buildVehicle(1L, userId, false),
                buildVehicle(2L, userId, false),
                buildVehicle(3L, 20L, false)); // Diferente usuario

        when(vehicleService.getAllVehicles()).thenReturn(allVehicles);

        // Act
        ResponseEntity<List<Vehicle>> response = vehicleController.getVehiclesByUser(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Vehicle> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(2, responseBody.size()); // Solo 2 vehículos del usuario 10
    }

    @Test
    void getVehiclesByUser_whenOwner_shouldReturnOK() {
        // Arrange
        Long userId = 10L;
        User ownerUser = buildUser(userId, false);

        when(authUtils.isCurrentUserOrAdmin(userId)).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(ownerUser);

        List<Vehicle> allVehicles = Arrays.asList(
                buildVehicle(1L, userId, false),
                buildVehicle(2L, userId, false),
                buildVehicle(3L, 20L, false)); // Diferente usuario

        when(vehicleService.getAllVehicles()).thenReturn(allVehicles);

        // Act
        ResponseEntity<List<Vehicle>> response = vehicleController.getVehiclesByUser(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Vehicle> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(2, responseBody.size()); // Solo 2 vehículos del usuario 10
    }

    @Test
    void getVehiclesByUser_whenNotOwnerNorAdmin_shouldReturnForbidden() {
        // Arrange
        Long userId = 10L;
        User otherUser = buildUser(20L, false);

        when(authentication.getPrincipal()).thenReturn(otherUser);
        when(authUtils.isCurrentUserOrAdmin(userId)).thenReturn(false);

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
        when(authUtils.isCurrentUserOrAdmin(10L)).thenReturn(true);

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
        when(authUtils.isCurrentUserOrAdmin(10L)).thenReturn(true);

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
        when(authUtils.isCurrentUserOrAdmin(10L)).thenReturn(false);

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
