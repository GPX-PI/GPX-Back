package com.udea.GPX.service;

import com.udea.GPX.dto.CreateStageResultDTO;
import com.udea.GPX.dto.UpdateStageResultDTO;
import com.udea.GPX.dto.ClasificacionCompletaDTO;
import com.udea.GPX.model.*;
import com.udea.GPX.repository.IStageResultRepository;
import com.udea.GPX.repository.IStageRepository;
import com.udea.GPX.repository.IVehicleRepository;
import com.udea.GPX.util.BusinessRuleValidator;
import com.udea.GPX.util.TestDataBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("StageResultService Tests")
class StageResultServiceTest {

    @Mock
    private IStageResultRepository stageResultRepository;

    @Mock
    private IStageRepository stageRepository;

    @Mock
    private IVehicleRepository vehicleRepository;

    @Mock
    private BusinessRuleValidator businessRuleValidator;

    @InjectMocks
    private StageResultService stageResultService;

    private StageResult testStageResult;
    private Stage testStage;
    private Vehicle testVehicle;
    private Event testEvent;
    private User testUser;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = TestDataBuilder.buildUser(1L, "TestUser", false);
        testCategory = TestDataBuilder.buildCategory(1L, "Test Category");
        testEvent = TestDataBuilder.buildEvent(1L, "Test Event");
        testStage = TestDataBuilder.buildStage(1L, "Test Stage", testEvent, 1);
        testVehicle = TestDataBuilder.buildVehicle(1L, testUser, testCategory);

        testStageResult = new StageResult();
        testStageResult.setId(1L);
        testStageResult.setStage(testStage);
        testStageResult.setVehicle(testVehicle);
        testStageResult.setTimestamp(LocalDateTime.now());
        testStageResult.setLatitude(4.6097);
        testStageResult.setLongitude(-74.0817);
        testStageResult.setElapsedTimeSeconds(3600);
    }

    // ========== SAVE RESULT TESTS ==========

    @Test
    @DisplayName("saveResult - Debe guardar resultado exitosamente")
    void saveResult_shouldSaveResultSuccessfully() {
        // Given
        when(stageResultRepository.save(testStageResult)).thenReturn(testStageResult);

        // When
        StageResult result = stageResultService.saveResult(testStageResult);

        // Then
        assertThat(result).isEqualTo(testStageResult);
        verify(stageResultRepository).save(testStageResult);
    }

    // ========== CREATE RESULT TESTS ==========

    @Test
    @DisplayName("createResult - Debe crear resultado desde DTO exitosamente")
    void createResult_shouldCreateResultFromDTOSuccessfully() {
        // Given
        CreateStageResultDTO createDTO = new CreateStageResultDTO();
        createDTO.setStageId(1L);
        createDTO.setVehicleId(1L);
        createDTO.setTimestamp(LocalDateTime.now());
        createDTO.setLatitude(4.6097);
        createDTO.setLongitude(-74.0817);

        when(stageRepository.findById(1L)).thenReturn(Optional.of(testStage));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));
        when(stageResultRepository.save(any(StageResult.class))).thenReturn(testStageResult);

        doNothing().when(businessRuleValidator).validateStageResultTimestamp(any(), any());
        doNothing().when(businessRuleValidator).validateGpsCoordinates(any(), any());

        // When
        StageResult result = stageResultService.createResult(createDTO);

        // Then
        assertThat(result).isEqualTo(testStageResult);
        verify(stageRepository).findById(1L);
        verify(vehicleRepository).findById(1L);
        verify(businessRuleValidator).validateStageResultTimestamp(any(), any());
        verify(businessRuleValidator).validateGpsCoordinates(any(), any());
        verify(stageResultRepository).save(any(StageResult.class));
    }

    @Test
    @DisplayName("createResult - Debe lanzar excepción si stage no existe")
    void createResult_shouldThrowExceptionIfStageNotExists() {
        // Given
        CreateStageResultDTO createDTO = new CreateStageResultDTO();
        createDTO.setStageId(999L);
        createDTO.setVehicleId(1L);

        when(stageRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> stageResultService.createResult(createDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Etapa no encontrada con ID: 999");

        verify(stageRepository).findById(999L);
        verify(vehicleRepository, never()).findById(any());
        verify(stageResultRepository, never()).save(any());
    }

    @Test
    @DisplayName("createResult - Debe lanzar excepción si vehículo no existe")
    void createResult_shouldThrowExceptionIfVehicleNotExists() {
        // Given
        CreateStageResultDTO createDTO = new CreateStageResultDTO();
        createDTO.setStageId(1L);
        createDTO.setVehicleId(999L);

        when(stageRepository.findById(1L)).thenReturn(Optional.of(testStage));
        when(vehicleRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> stageResultService.createResult(createDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Vehículo no encontrado con ID: 999");

        verify(stageRepository).findById(1L);
        verify(vehicleRepository).findById(999L);
        verify(stageResultRepository, never()).save(any());
    }

    // ========== UPDATE RESULT TESTS ==========

    @Test
    @DisplayName("updateResult - Debe actualizar resultado existente")
    void updateResult_shouldUpdateExistingResult() {
        // Given
        StageResult updatedResult = new StageResult();
        updatedResult.setTimestamp(LocalDateTime.now().plusHours(1));
        updatedResult.setLatitude(4.7000);
        updatedResult.setLongitude(-74.1000);
        updatedResult.setElapsedTimeSeconds(7200);

        StageResult expectedResult = new StageResult();
        expectedResult.setId(1L);
        expectedResult.setTimestamp(updatedResult.getTimestamp());
        expectedResult.setLatitude(updatedResult.getLatitude());
        expectedResult.setLongitude(updatedResult.getLongitude());
        expectedResult.setElapsedTimeSeconds(updatedResult.getElapsedTimeSeconds());

        when(stageResultRepository.findById(1L)).thenReturn(Optional.of(testStageResult));
        when(stageResultRepository.save(any(StageResult.class))).thenReturn(expectedResult);

        // When
        StageResult result = stageResultService.updateResult(1L, updatedResult);

        // Then
        assertThat(result).isEqualTo(expectedResult);
        verify(stageResultRepository).findById(1L);
        verify(stageResultRepository).save(any(StageResult.class));
    }

    @Test
    @DisplayName("updateResult - Debe lanzar excepción si resultado no existe")
    void updateResult_shouldThrowExceptionIfResultNotExists() {
        // Given
        StageResult updatedResult = new StageResult();
        when(stageResultRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> stageResultService.updateResult(999L, updatedResult))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Resultado no encontrado");

        verify(stageResultRepository).findById(999L);
        verify(stageResultRepository, never()).save(any());
    }

    // ========== UPDATE RESULT FROM DTO TESTS ==========

    @Test
    @DisplayName("updateResultFromDTO - Debe actualizar resultado desde DTO")
    void updateResultFromDTO_shouldUpdateResultFromDTO() {
        // Given
        UpdateStageResultDTO updateDTO = new UpdateStageResultDTO();
        updateDTO.setTimestamp(LocalDateTime.now().plusHours(2));
        updateDTO.setLatitude(4.8000);
        updateDTO.setLongitude(-74.2000);

        when(stageResultRepository.findById(1L)).thenReturn(Optional.of(testStageResult));
        when(stageResultRepository.save(any(StageResult.class))).thenReturn(testStageResult);

        doNothing().when(businessRuleValidator).validateStageResultTimestamp(any(), any());
        doNothing().when(businessRuleValidator).validateGpsCoordinates(any(), any());

        // When
        StageResult result = stageResultService.updateResultFromDTO(1L, updateDTO);

        // Then
        assertThat(result).isEqualTo(testStageResult);
        verify(stageResultRepository).findById(1L);
        verify(businessRuleValidator).validateStageResultTimestamp(any(), any());
        verify(businessRuleValidator).validateGpsCoordinates(any(), any());
        verify(stageResultRepository).save(any(StageResult.class));
    }

    @Test
    @DisplayName("updateResultFromDTO - Debe actualizar stage si es diferente")
    void updateResultFromDTO_shouldUpdateStageIfDifferent() {
        // Given
        Stage newStage = TestDataBuilder.buildStage(2L, "New Stage", testEvent, 2);
        UpdateStageResultDTO updateDTO = new UpdateStageResultDTO();
        updateDTO.setStageId(2L);

        when(stageResultRepository.findById(1L)).thenReturn(Optional.of(testStageResult));
        when(stageRepository.findById(2L)).thenReturn(Optional.of(newStage));
        when(stageResultRepository.save(any(StageResult.class))).thenReturn(testStageResult);

        // When
        StageResult result = stageResultService.updateResultFromDTO(1L, updateDTO);

        // Then
        assertThat(result).isEqualTo(testStageResult);
        verify(stageRepository).findById(2L);
        verify(stageResultRepository).save(any(StageResult.class));
    }

    @Test
    @DisplayName("updateResultFromDTO - Debe actualizar vehículo si es diferente")
    void updateResultFromDTO_shouldUpdateVehicleIfDifferent() {
        // Given
        Vehicle newVehicle = TestDataBuilder.buildVehicle(2L, testUser, testCategory);
        UpdateStageResultDTO updateDTO = new UpdateStageResultDTO();
        updateDTO.setVehicleId(2L);

        when(stageResultRepository.findById(1L)).thenReturn(Optional.of(testStageResult));
        when(vehicleRepository.findById(2L)).thenReturn(Optional.of(newVehicle));
        when(stageResultRepository.save(any(StageResult.class))).thenReturn(testStageResult);

        // When
        StageResult result = stageResultService.updateResultFromDTO(1L, updateDTO);

        // Then
        assertThat(result).isEqualTo(testStageResult);
        verify(vehicleRepository).findById(2L);
        verify(stageResultRepository).save(any(StageResult.class));
    }

    @Test
    @DisplayName("updateResultFromDTO - Debe lanzar excepción si stage no existe")
    void updateResultFromDTO_shouldThrowExceptionIfStageNotExists() {
        // Given
        UpdateStageResultDTO updateDTO = new UpdateStageResultDTO();
        updateDTO.setStageId(999L);

        when(stageResultRepository.findById(1L)).thenReturn(Optional.of(testStageResult));
        when(stageRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> stageResultService.updateResultFromDTO(1L, updateDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Etapa no encontrada con ID: 999");

        verify(stageRepository).findById(999L);
    }

    @Test
    @DisplayName("updateResultFromDTO - Debe lanzar excepción si vehículo no existe")
    void updateResultFromDTO_shouldThrowExceptionIfVehicleNotExists() {
        // Given
        UpdateStageResultDTO updateDTO = new UpdateStageResultDTO();
        updateDTO.setVehicleId(999L);

        when(stageResultRepository.findById(1L)).thenReturn(Optional.of(testStageResult));
        when(vehicleRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> stageResultService.updateResultFromDTO(1L, updateDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Vehículo no encontrado con ID: 999");

        verify(vehicleRepository).findById(999L);
    }

    // ========== DELETE RESULT TESTS ==========

    @Test
    @DisplayName("deleteResult - Debe eliminar resultado exitosamente")
    void deleteResult_shouldDeleteResultSuccessfully() {
        // Given
        doNothing().when(stageResultRepository).deleteById(1L);

        // When
        stageResultService.deleteResult(1L);

        // Then
        verify(stageResultRepository).deleteById(1L);
    }

    // ========== UPDATE ELAPSED TIMES FOR EVENT TESTS ==========

    @Test
    @DisplayName("updateElapsedTimesForEvent - Debe actualizar tiempos transcurridos")
    void updateElapsedTimesForEvent_shouldUpdateElapsedTimes() {
        // Given
        LocalDateTime baseTime = LocalDateTime.now();

        StageResult result1 = new StageResult();
        result1.setId(1L);
        result1.setStage(testStage);
        result1.setVehicle(testVehicle);
        result1.setTimestamp(baseTime);

        Stage stage2 = TestDataBuilder.buildStage(2L, "Stage 2", testEvent, 2);
        StageResult result2 = new StageResult();
        result2.setId(2L);
        result2.setStage(stage2);
        result2.setVehicle(testVehicle);
        result2.setTimestamp(baseTime.plusMinutes(30));

        List<StageResult> stageResults = Arrays.asList(result1, result2);

        when(stageResultRepository.findByEventIdWithTimestampOrderedByVehicleAndStage(1L))
                .thenReturn(stageResults);
        when(stageResultRepository.save(any(StageResult.class))).thenReturn(result1);

        // When
        stageResultService.updateElapsedTimesForEvent(1L);

        // Then
        verify(stageResultRepository).findByEventIdWithTimestampOrderedByVehicleAndStage(1L);
        verify(stageResultRepository, atLeastOnce()).save(any(StageResult.class));
    }

    // ========== GET CLASIFICACION POR CATEGORIA TESTS ==========

    @Test
    @DisplayName("getClasificacionPorCategoria - Debe retornar clasificación por categoría")
    void getClasificacionPorCategoria_shouldReturnClassificationByCategory() {
        // Given
        List<StageResult> results = Arrays.asList(testStageResult);
        when(stageResultRepository.findByEventIdAndCategoryId(1L, 1L)).thenReturn(results);

        // When
        List<ClasificacionCompletaDTO> classification = stageResultService.getClasificacionPorCategoria(1L, 1L);

        // Then
        assertThat(classification).isNotNull();
        verify(stageResultRepository).findByEventIdAndCategoryId(1L, 1L);
    }

    // ========== GET CLASIFICACION POR STAGE TESTS ==========

    @Test
    @DisplayName("getClasificacionPorStage - Debe retornar clasificación por etapa")
    void getClasificacionPorStage_shouldReturnClassificationByStage() {
        // Given
        List<StageResult> results = Arrays.asList(testStageResult);
        when(stageResultRepository.findByEventIdAndStageNumber(1L, 1)).thenReturn(results);

        // When
        List<ClasificacionCompletaDTO> classification = stageResultService.getClasificacionPorStage(1L, 1);

        // Then
        assertThat(classification).isNotNull();
        verify(stageResultRepository).findByEventIdAndStageNumber(1L, 1);
    }

    // ========== EDGE CASES ==========

    @Test
    @DisplayName("createResult - Debe validar reglas de negocio antes de crear")
    void createResult_shouldValidateBusinessRulesBeforeCreating() {
        // Given
        CreateStageResultDTO createDTO = new CreateStageResultDTO();
        createDTO.setStageId(1L);
        createDTO.setVehicleId(1L);
        createDTO.setTimestamp(LocalDateTime.now());
        createDTO.setLatitude(4.6097);
        createDTO.setLongitude(-74.0817);

        when(stageRepository.findById(1L)).thenReturn(Optional.of(testStage));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));

        doThrow(new IllegalArgumentException("Invalid timestamp"))
                .when(businessRuleValidator).validateStageResultTimestamp(any(), any());

        // When & Then
        assertThatThrownBy(() -> stageResultService.createResult(createDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid timestamp");

        verify(businessRuleValidator).validateStageResultTimestamp(any(), any());
        verify(stageResultRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateResultFromDTO - No debe actualizar si stage ID es igual")
    void updateResultFromDTO_shouldNotUpdateIfStageIdIsEqual() {
        // Given
        UpdateStageResultDTO updateDTO = new UpdateStageResultDTO();
        updateDTO.setStageId(1L); // Mismo ID que el stage actual

        when(stageResultRepository.findById(1L)).thenReturn(Optional.of(testStageResult));
        when(stageResultRepository.save(any(StageResult.class))).thenReturn(testStageResult);

        // When
        StageResult result = stageResultService.updateResultFromDTO(1L, updateDTO);

        // Then
        assertThat(result).isEqualTo(testStageResult);
        verify(stageRepository, never()).findById(any());
        verify(stageResultRepository).save(any(StageResult.class));
    }

    @Test
    @DisplayName("updateResultFromDTO - No debe actualizar si vehicle ID es igual")
    void updateResultFromDTO_shouldNotUpdateIfVehicleIdIsEqual() {
        // Given
        UpdateStageResultDTO updateDTO = new UpdateStageResultDTO();
        updateDTO.setVehicleId(1L); // Mismo ID que el vehículo actual

        when(stageResultRepository.findById(1L)).thenReturn(Optional.of(testStageResult));
        when(stageResultRepository.save(any(StageResult.class))).thenReturn(testStageResult);

        // When
        StageResult result = stageResultService.updateResultFromDTO(1L, updateDTO);

        // Then
        assertThat(result).isEqualTo(testStageResult);
        verify(vehicleRepository, never()).findById(any());
        verify(stageResultRepository).save(any(StageResult.class));
    }

    // ========== INTEGRATION TESTS ==========

    @Test
    @DisplayName("Flow completo - Crear, actualizar y eliminar resultado")
    void fullFlow_createUpdateDeleteResult() {
        // Given
        CreateStageResultDTO createDTO = new CreateStageResultDTO();
        createDTO.setStageId(1L);
        createDTO.setVehicleId(1L);
        createDTO.setTimestamp(LocalDateTime.now());
        createDTO.setLatitude(4.6097);
        createDTO.setLongitude(-74.0817);

        UpdateStageResultDTO updateDTO = new UpdateStageResultDTO();
        updateDTO.setLatitude(4.7000);
        updateDTO.setLongitude(-74.1000);

        when(stageRepository.findById(1L)).thenReturn(Optional.of(testStage));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));
        when(stageResultRepository.save(any(StageResult.class))).thenReturn(testStageResult);
        when(stageResultRepository.findById(1L)).thenReturn(Optional.of(testStageResult));
        doNothing().when(businessRuleValidator).validateStageResultTimestamp(any(), any());
        doNothing().when(businessRuleValidator).validateGpsCoordinates(any(), any());
        doNothing().when(stageResultRepository).deleteById(1L);

        // When - Create
        StageResult created = stageResultService.createResult(createDTO);

        // Then - Verify creation
        assertThat(created).isEqualTo(testStageResult);

        // When - Update
        StageResult updated = stageResultService.updateResultFromDTO(1L, updateDTO);

        // Then - Verify update
        assertThat(updated).isEqualTo(testStageResult);

        // When - Delete
        stageResultService.deleteResult(1L);

        // Then - Verify all operations
        verify(stageResultRepository, times(2)).save(any(StageResult.class));
        verify(stageResultRepository).findById(1L);
        verify(stageResultRepository).deleteById(1L);
    }
}
