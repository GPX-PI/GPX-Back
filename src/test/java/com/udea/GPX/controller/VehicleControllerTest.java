package com.udea.gpx.controller;

import com.udea.gpx.model.User;
import com.udea.gpx.model.Vehicle;
import com.udea.gpx.repository.IEventVehicleRepository;
import com.udea.gpx.repository.IStageResultRepository;
import com.udea.gpx.service.CategoryService;
import com.udea.gpx.service.UserService;
import com.udea.gpx.service.VehicleService;
import com.udea.gpx.util.AuthUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Collections;
import java.util.Optional;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VehicleController Tests")
class VehicleControllerTest {
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
    private AuthUtils authUtils;
    @InjectMocks
    private VehicleController controller;

    @Test
    @DisplayName("Controller should be instantiated")
    void shouldInstantiateController() {
        assertThat(controller).isNotNull();
    }

    @Test
    @DisplayName("getAllVehicles - Forbidden for non-admin")
    void getAllVehicles_forbidden() {
        when(authUtils.isCurrentUserAdmin()).thenReturn(false);
        ResponseEntity<?> response = controller.getAllVehicles();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("getAllVehicles - Success for admin")
    void getAllVehicles_success() {
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        List<Vehicle> list = Collections.singletonList(new Vehicle());
        when(vehicleService.getAllVehicles()).thenReturn(list);
        ResponseEntity<?> response = controller.getAllVehicles();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(list);
    }

    @Test
    @DisplayName("getVehicleById - Not found")
    void getVehicleById_notFound() {
        when(vehicleService.getVehicleById(anyLong())).thenReturn(Optional.empty());
        ResponseEntity<Vehicle> response = controller.getVehicleById(1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("getVehicleById - Forbidden for non-owner")
    void getVehicleById_forbidden() {
        Vehicle vehicle = mock(Vehicle.class);
        User user = mock(User.class);
        when(vehicle.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(2L);
        when(vehicleService.getVehicleById(anyLong())).thenReturn(Optional.of(vehicle));
        when(authUtils.isCurrentUserOrAdmin(any())).thenReturn(false);
        ResponseEntity<Vehicle> response = controller.getVehicleById(1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("getVehicleById - Success for owner or admin")
    void getVehicleById_success() {
        Vehicle vehicle = new Vehicle();
        User user = new User();
        user.setId(1L);
        vehicle.setUser(user);
        when(vehicleService.getVehicleById(anyLong())).thenReturn(Optional.of(vehicle));
        when(authUtils.isCurrentUserOrAdmin(any())).thenReturn(true);

        ResponseEntity<Vehicle> response = controller.getVehicleById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(vehicle);
    }

    @Test
    @DisplayName("getVehiclesByCategory - Forbidden for non-admin")
    void getVehiclesByCategory_forbidden() {
        when(authUtils.isCurrentUserAdmin()).thenReturn(false);

        ResponseEntity<List<Vehicle>> response = controller.getVehiclesByCategory(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("getVehiclesByCategory - Success for admin")
    void getVehiclesByCategory_success() {
        // Preparar categoría y vehículos
        Vehicle vehicle1 = new Vehicle();
        Vehicle vehicle2 = new Vehicle();
        com.udea.gpx.model.Category category = new com.udea.gpx.model.Category();
        category.setId(1L);
        vehicle1.setCategory(category);

        List<Vehicle> allVehicles = List.of(vehicle1, vehicle2);

        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        when(vehicleService.getAllVehicles()).thenReturn(allVehicles);

        ResponseEntity<List<Vehicle>> response = controller.getVehiclesByCategory(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0)).isEqualTo(vehicle1);
    }

    @Test
    @DisplayName("getVehiclesByUser - Forbidden when not owner or admin")
    void getVehiclesByUser_forbidden() {
        when(authUtils.isCurrentUserOrAdmin(anyLong())).thenReturn(false);

        ResponseEntity<List<Vehicle>> response = controller.getVehiclesByUser(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("getVehiclesByUser - Success for owner or admin")
    void getVehiclesByUser_success() {
        // Preparar usuario y vehículos
        User user = new User();
        user.setId(1L);

        Vehicle vehicle1 = new Vehicle();
        vehicle1.setUser(user);

        Vehicle vehicle2 = new Vehicle();
        User otherUser = new User();
        otherUser.setId(2L);
        vehicle2.setUser(otherUser);

        List<Vehicle> allVehicles = List.of(vehicle1, vehicle2);

        when(authUtils.isCurrentUserOrAdmin(1L)).thenReturn(true);
        when(vehicleService.getAllVehicles()).thenReturn(allVehicles);

        ResponseEntity<List<Vehicle>> response = controller.getVehiclesByUser(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0)).isEqualTo(vehicle1);
    }

    @Test
    @DisplayName("deleteVehicle - Not found")
    void deleteVehicle_notFound() {
        when(vehicleService.getVehicleById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<Object> response = controller.deleteVehicle(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("deleteVehicle - Forbidden when not owner or admin")
    void deleteVehicle_forbidden() {
        Vehicle vehicle = new Vehicle();
        User user = new User();
        user.setId(1L);
        vehicle.setUser(user);

        when(vehicleService.getVehicleById(anyLong())).thenReturn(Optional.of(vehicle));
        when(authUtils.isCurrentUserOrAdmin(anyLong())).thenReturn(false);

        ResponseEntity<Object> response = controller.deleteVehicle(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("deleteVehicle - Conflict when vehicle is used in events or results")
    void deleteVehicle_conflict() { // Preparar vehículo
        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L); // Establecer el ID del vehículo
        vehicle.setName("Test Vehicle");
        User user = new User();
        user.setId(1L);
        vehicle.setUser(user);

        when(vehicleService.getVehicleById(anyLong())).thenReturn(Optional.of(vehicle));
        when(authUtils.isCurrentUserOrAdmin(anyLong())).thenReturn(true); // Crear un mock de EventVehicle configurado
                                                                          // correctamente
        com.udea.gpx.model.EventVehicle mockEventVehicle = mock(com.udea.gpx.model.EventVehicle.class);
        when(mockEventVehicle.getVehicleId()).thenReturn(vehicle);

        // Simular que el vehículo está en uso en eventos
        when(eventVehicleRepository.findAll()).thenReturn(Collections.singletonList(mockEventVehicle));

        ResponseEntity<Object> response = controller.deleteVehicle(1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isInstanceOf(Map.class);

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertThat(body).containsKeys("error", "message", "vehicleName")
                .containsEntry("vehicleName", "Test Vehicle");
    }
}
