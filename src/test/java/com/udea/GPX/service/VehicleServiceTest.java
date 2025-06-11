package com.udea.gpx.service;

import com.udea.gpx.util.TestDataBuilder;
import com.udea.gpx.model.Category;
import com.udea.gpx.model.User;
import com.udea.gpx.model.Vehicle;
import com.udea.gpx.repository.IVehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("VehicleService Tests")
class VehicleServiceTest {

    @Mock
    private IVehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private Vehicle testVehicle;
    private User testUser;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = TestDataBuilder.buildUser(1L, "TestUser", false);
        testCategory = TestDataBuilder.buildCategory(1L, "Test Category");
        testVehicle = TestDataBuilder.buildVehicle(1L, testUser, testCategory);
    }

    // ========== GET ALL VEHICLES TESTS ==========

    @Test
    @DisplayName("getAllVehicles - Debe retornar todos los vehículos")
    void getAllVehicles_shouldReturnAllVehicles() {
        // Given
        Vehicle vehicle2 = TestDataBuilder.buildVehicle(2L, testUser, testCategory);
        List<Vehicle> vehicles = Arrays.asList(testVehicle, vehicle2);
        when(vehicleRepository.findAll()).thenReturn(vehicles);

        // When
        List<Vehicle> result = vehicleService.getAllVehicles();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(testVehicle, vehicle2);
        verify(vehicleRepository).findAll();
    }

    @Test
    @DisplayName("getAllVehicles - Debe retornar lista vacía cuando no hay vehículos")
    void getAllVehicles_shouldReturnEmptyListWhenNoVehicles() {
        // Given
        when(vehicleRepository.findAll()).thenReturn(List.of());

        // When
        List<Vehicle> result = vehicleService.getAllVehicles();

        // Then
        assertThat(result).isEmpty();
        verify(vehicleRepository).findAll();
    }

    // ========== GET VEHICLE BY ID TESTS ==========

    @Test
    @DisplayName("getVehicleById - Debe retornar vehículo existente")
    void getVehicleById_shouldReturnExistingVehicle() {
        // Given
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));

        // When
        Optional<Vehicle> result = vehicleService.getVehicleById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testVehicle);
        verify(vehicleRepository).findById(1L);
    }

    @Test
    @DisplayName("getVehicleById - Debe retornar Optional vacío para vehículo inexistente")
    void getVehicleById_shouldReturnEmptyOptionalForNonExistentVehicle() {
        // Given
        when(vehicleRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Vehicle> result = vehicleService.getVehicleById(999L);

        // Then
        assertThat(result).isEmpty();
        verify(vehicleRepository).findById(999L);
    }

    // ========== CREATE VEHICLE TESTS ==========

    @Test
    @DisplayName("createVehicle - Debe crear vehículo exitosamente")
    void createVehicle_shouldCreateVehicleSuccessfully() {
        // Given
        Vehicle newVehicle = new Vehicle();
        newVehicle.setName("New Vehicle");
        newVehicle.setSoat("SOA123");
        newVehicle.setPlates("ABC123");
        newVehicle.setCategory(testCategory);
        newVehicle.setUser(testUser);

        Vehicle savedVehicle = new Vehicle();
        savedVehicle.setId(3L);
        savedVehicle.setName("New Vehicle");
        savedVehicle.setSoat("SOA123");
        savedVehicle.setPlates("ABC123");
        savedVehicle.setCategory(testCategory);
        savedVehicle.setUser(testUser);

        when(vehicleRepository.save(newVehicle)).thenReturn(savedVehicle);

        // When
        Vehicle result = vehicleService.createVehicle(newVehicle);

        // Then
        assertThat(result).isEqualTo(savedVehicle);
        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getName()).isEqualTo("New Vehicle");
        verify(vehicleRepository).save(newVehicle);
    }

    // ========== UPDATE VEHICLE TESTS ==========

    @Test
    @DisplayName("updateVehicle - Debe actualizar vehículo existente exitosamente")
    void updateVehicle_shouldUpdateExistingVehicleSuccessfully() {
        // Given
        Vehicle updatedVehicle = new Vehicle();
        updatedVehicle.setName("Updated Vehicle");
        updatedVehicle.setSoat("SOA456");
        updatedVehicle.setPlates("XYZ789");
        updatedVehicle.setCategory(testCategory);
        updatedVehicle.setUser(testUser);

        Vehicle expectedVehicle = new Vehicle();
        expectedVehicle.setId(1L);
        expectedVehicle.setName("Updated Vehicle");
        expectedVehicle.setSoat("SOA456");
        expectedVehicle.setPlates("XYZ789");
        expectedVehicle.setCategory(testCategory);
        expectedVehicle.setUser(testUser);

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(expectedVehicle);

        // When
        Vehicle result = vehicleService.updateVehicle(1L, updatedVehicle);

        // Then
        assertThat(result).isEqualTo(expectedVehicle);
        assertThat(result.getName()).isEqualTo("Updated Vehicle");
        assertThat(result.getSoat()).isEqualTo("SOA456");
        assertThat(result.getPlates()).isEqualTo("XYZ789");
        verify(vehicleRepository).findById(1L);
        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("updateVehicle - Debe lanzar excepción para vehículo inexistente")
    void updateVehicle_shouldThrowExceptionForNonExistentVehicle() {
        // Given
        Vehicle updatedVehicle = new Vehicle();
        updatedVehicle.setName("Updated Vehicle");

        when(vehicleRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> vehicleService.updateVehicle(999L, updatedVehicle))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Vehículo no encontrado");

        verify(vehicleRepository).findById(999L);
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    // ========== DELETE VEHICLE TESTS ==========

    @Test
    @DisplayName("deleteVehicle - Debe eliminar vehículo exitosamente")
    void deleteVehicle_shouldDeleteVehicleSuccessfully() {
        // Given
        doNothing().when(vehicleRepository).deleteById(1L);

        // When
        vehicleService.deleteVehicle(1L);

        // Then
        verify(vehicleRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteVehicle - Debe manejar eliminación de vehículo inexistente")
    void deleteVehicle_shouldHandleDeletionOfNonExistentVehicle() {
        // Given
        doNothing().when(vehicleRepository).deleteById(999L);

        // When
        vehicleService.deleteVehicle(999L);

        // Then
        verify(vehicleRepository).deleteById(999L);
    }

    // ========== EDGE CASES ==========

    @Test
    @DisplayName("createVehicle - Debe manejar vehículo con campos nulos")
    void createVehicle_shouldHandleVehicleWithNullFields() {
        // Given
        Vehicle vehicleWithNulls = new Vehicle();
        vehicleWithNulls.setName(null);
        vehicleWithNulls.setSoat(null);
        vehicleWithNulls.setPlates(null);
        vehicleWithNulls.setCategory(null);
        vehicleWithNulls.setUser(null);

        when(vehicleRepository.save(vehicleWithNulls)).thenReturn(vehicleWithNulls);

        // When
        Vehicle result = vehicleService.createVehicle(vehicleWithNulls);

        // Then
        assertThat(result).isEqualTo(vehicleWithNulls);
        assertThat(result.getName()).isNull();
        assertThat(result.getSoat()).isNull();
        assertThat(result.getPlates()).isNull();
        assertThat(result.getCategory()).isNull();
        assertThat(result.getUser()).isNull();
        verify(vehicleRepository).save(vehicleWithNulls);
    }

    @Test
    @DisplayName("updateVehicle - Debe actualizar solo los campos proporcionados")
    void updateVehicle_shouldUpdateOnlyProvidedFields() {
        // Given
        Vehicle partialUpdate = new Vehicle();
        partialUpdate.setName("Partial Update");
        // No establecer otros campos para probar actualización parcial

        Vehicle originalVehicle = new Vehicle();
        originalVehicle.setId(1L);
        originalVehicle.setName("Original Name");
        originalVehicle.setSoat("Original SOAT");
        originalVehicle.setPlates("Original Plates");
        originalVehicle.setCategory(testCategory);
        originalVehicle.setUser(testUser);

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(originalVehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Vehicle result = vehicleService.updateVehicle(1L, partialUpdate);

        // Then
        assertThat(result.getName()).isEqualTo("Partial Update");
        // Los campos no especificados deben tomar los valores del partial update (null
        // en este caso)
        verify(vehicleRepository).findById(1L);
        verify(vehicleRepository).save(any(Vehicle.class));
    }

    // ========== INTEGRATION TESTS ==========

    @Test
    @DisplayName("Flow completo - Crear, actualizar y eliminar vehículo")
    void fullFlow_createUpdateDeleteVehicle() {
        // Given
        Vehicle newVehicle = new Vehicle();
        newVehicle.setName("Flow Vehicle");
        newVehicle.setSoat("FLW123");
        newVehicle.setPlates("FLW456");
        newVehicle.setCategory(testCategory);
        newVehicle.setUser(testUser);

        Vehicle savedVehicle = new Vehicle();
        savedVehicle.setId(5L);
        savedVehicle.setName("Flow Vehicle");
        savedVehicle.setSoat("FLW123");
        savedVehicle.setPlates("FLW456");
        savedVehicle.setCategory(testCategory);
        savedVehicle.setUser(testUser);
        Vehicle updatedVehicle = new Vehicle();
        updatedVehicle.setId(5L); // Set the ID to match expectations
        updatedVehicle.setName("Updated Flow Vehicle");
        updatedVehicle.setSoat("UPD123");
        updatedVehicle.setPlates("UPD456");
        updatedVehicle.setCategory(testCategory);
        updatedVehicle.setUser(testUser);
        when(vehicleRepository.save(any(Vehicle.class)))
                .thenReturn(savedVehicle) // First call (create)
                .thenReturn(updatedVehicle); // Second call (update)
        when(vehicleRepository.findById(5L)).thenReturn(Optional.of(savedVehicle));
        doNothing().when(vehicleRepository).deleteById(5L);

        // When - Create
        Vehicle created = vehicleService.createVehicle(newVehicle); // Then - Verify creation
        assertThat(created.getId()).isEqualTo(5L);
        assertThat(created.getName()).isEqualTo(savedVehicle.getName());
        assertThat(created.getSoat()).isEqualTo(savedVehicle.getSoat());
        assertThat(created.getPlates()).isEqualTo(savedVehicle.getPlates());

        // When - Update
        Vehicle updated = vehicleService.updateVehicle(5L, updatedVehicle);

        // Then - Verify update
        assertThat(updated.getId()).isEqualTo(5L);
        assertThat(updated.getName()).isEqualTo(updatedVehicle.getName());
        assertThat(updated.getSoat()).isEqualTo(updatedVehicle.getSoat());
        assertThat(updated.getPlates()).isEqualTo(updatedVehicle.getPlates());

        // When - Delete
        vehicleService.deleteVehicle(5L); // Then - Verify all operations
        verify(vehicleRepository, times(2)).save(any(Vehicle.class)); // Called twice: create and update
        verify(vehicleRepository).findById(5L);
        verify(vehicleRepository).deleteById(5L);
    }
}
