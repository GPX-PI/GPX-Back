package com.udea.gpx.service;

import com.udea.gpx.util.TestDataBuilder;
import com.udea.gpx.model.Event;
import com.udea.gpx.model.Stage;
import com.udea.gpx.repository.IStageRepository;
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

@DisplayName("StageService Tests")
class StageServiceTest {

    @Mock
    private IStageRepository stageRepository;

    @InjectMocks
    private StageService stageService;

    private Stage testStage;
    private Event testEvent;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testEvent = TestDataBuilder.buildEvent(1L, "Test Event");
        testStage = TestDataBuilder.buildStage(1L, "Test Stage", testEvent, 1);
    }

    // ========== GET ALL STAGES TESTS ==========

    @Test
    @DisplayName("getAllStages - Debe retornar todas las etapas")
    void getAllStages_shouldReturnAllStages() {
        // Given
        Stage stage2 = TestDataBuilder.buildStage(2L, "Stage 2", testEvent, 2);
        List<Stage> stages = Arrays.asList(testStage, stage2);
        when(stageRepository.findAll()).thenReturn(stages);

        // When
        List<Stage> result = stageService.getAllStages(); // Then
        assertThat(result)
                .hasSize(2)
                .containsExactly(testStage, stage2);
        verify(stageRepository).findAll();
    }

    @Test
    @DisplayName("getAllStages - Debe retornar lista vacía cuando no hay etapas")
    void getAllStages_shouldReturnEmptyListWhenNoStages() {
        // Given
        when(stageRepository.findAll()).thenReturn(List.of());

        // When
        List<Stage> result = stageService.getAllStages();

        // Then
        assertThat(result).isEmpty();
        verify(stageRepository).findAll();
    }

    // ========== GET STAGES BY EVENT ID TESTS ==========

    @Test
    @DisplayName("getStagesByEventId - Debe retornar etapas del evento especificado")
    void getStagesByEventId_shouldReturnStagesForSpecifiedEvent() {
        // Given
        Event otherEvent = TestDataBuilder.buildEvent(2L, "Other Event");
        Stage stageFromOtherEvent = TestDataBuilder.buildStage(3L, "Other Stage", otherEvent, 1);

        List<Stage> allStages = Arrays.asList(testStage, stageFromOtherEvent);
        when(stageRepository.findAll()).thenReturn(allStages);

        // When
        List<Stage> result = stageService.getStagesByEventId(1L); // Then
        assertThat(result)
                .hasSize(1)
                .containsExactly(testStage);
        assertThat(result.get(0).getEvent().getId()).isEqualTo(1L);
        verify(stageRepository).findAll();
    }

    @Test
    @DisplayName("getStagesByEventId - Debe retornar lista vacía si evento no tiene etapas")
    void getStagesByEventId_shouldReturnEmptyListIfEventHasNoStages() {
        // Given
        Event otherEvent = TestDataBuilder.buildEvent(2L, "Other Event");
        Stage stageFromOtherEvent = TestDataBuilder.buildStage(3L, "Other Stage", otherEvent, 1);

        List<Stage> allStages = Arrays.asList(stageFromOtherEvent);
        when(stageRepository.findAll()).thenReturn(allStages);

        // When
        List<Stage> result = stageService.getStagesByEventId(999L);

        // Then
        assertThat(result).isEmpty();
        verify(stageRepository).findAll();
    }

    // ========== GET STAGE BY ID TESTS ==========

    @Test
    @DisplayName("getStageById - Debe retornar etapa existente")
    void getStageById_shouldReturnExistingStage() {
        // Given
        when(stageRepository.findById(1L)).thenReturn(Optional.of(testStage));

        // When
        Optional<Stage> result = stageService.getStageById(1L); // Then
        assertThat(result).isPresent()
                .contains(testStage);
        verify(stageRepository).findById(1L);
    }

    @Test
    @DisplayName("getStageById - Debe retornar Optional vacío para etapa inexistente")
    void getStageById_shouldReturnEmptyOptionalForNonExistentStage() {
        // Given
        when(stageRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Stage> result = stageService.getStageById(999L);

        // Then
        assertThat(result).isEmpty();
        verify(stageRepository).findById(999L);
    }

    // ========== CREATE STAGE TESTS ==========

    @Test
    @DisplayName("createStage - Debe crear etapa exitosamente")
    void createStage_shouldCreateStageSuccessfully() {
        // Given
        Stage newStage = TestDataBuilder.buildStage(null, "New Stage", testEvent, 3);
        Stage savedStage = TestDataBuilder.buildStage(5L, "New Stage", testEvent, 3);

        when(stageRepository.save(newStage)).thenReturn(savedStage);

        // When
        Stage result = stageService.createStage(newStage);

        // Then
        assertThat(result).isEqualTo(savedStage);
        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getName()).isEqualTo("New Stage");
        verify(stageRepository).save(newStage);
    }

    // ========== UPDATE STAGE TESTS ==========

    @Test
    @DisplayName("updateStage - Debe actualizar etapa existente exitosamente")
    void updateStage_shouldUpdateExistingStageSuccessfully() {
        // Given
        Stage updatedStage = TestDataBuilder.buildStage(null, "Updated Stage", testEvent, 5);
        Stage expectedStage = TestDataBuilder.buildStage(1L, "Updated Stage", testEvent, 5);

        when(stageRepository.findById(1L)).thenReturn(Optional.of(testStage));
        when(stageRepository.save(any(Stage.class))).thenReturn(expectedStage);

        // When
        Stage result = stageService.updateStage(1L, updatedStage);

        // Then
        assertThat(result).isEqualTo(expectedStage);
        assertThat(result.getName()).isEqualTo("Updated Stage");
        assertThat(result.getOrderNumber()).isEqualTo(5);
        verify(stageRepository).findById(1L);
        verify(stageRepository).save(any(Stage.class));
    }

    @Test
    @DisplayName("updateStage - Debe lanzar excepción para etapa inexistente")
    void updateStage_shouldThrowExceptionForNonExistentStage() {
        // Given
        Stage updatedStage = TestDataBuilder.buildStage(null, "Updated Stage", testEvent, 5);

        when(stageRepository.findById(999L)).thenReturn(Optional.empty()); // When & Then
        assertThatThrownBy(() -> stageService.updateStage(999L, updatedStage))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Etapa no encontrada");

        verify(stageRepository).findById(999L);
        verify(stageRepository, never()).save(any(Stage.class));
    }

    // ========== DELETE STAGE TESTS ==========

    @Test
    @DisplayName("deleteStage - Debe eliminar etapa exitosamente")
    void deleteStage_shouldDeleteStageSuccessfully() {
        // Given
        doNothing().when(stageRepository).deleteById(1L);

        // When
        stageService.deleteStage(1L);

        // Then
        verify(stageRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteStage - Debe manejar eliminación de etapa inexistente")
    void deleteStage_shouldHandleDeletionOfNonExistentStage() {
        // Given
        doNothing().when(stageRepository).deleteById(999L);

        // When
        stageService.deleteStage(999L);

        // Then
        verify(stageRepository).deleteById(999L);
    }

    // ========== EDGE CASES ==========

    @Test
    @DisplayName("createStage - Debe manejar etapa con campos nulos")
    void createStage_shouldHandleStageWithNullFields() {
        // Given
        Stage stageWithNulls = new Stage();
        stageWithNulls.setName(null);
        stageWithNulls.setOrderNumber(0);
        stageWithNulls.setEvent(null);

        when(stageRepository.save(stageWithNulls)).thenReturn(stageWithNulls);

        // When
        Stage result = stageService.createStage(stageWithNulls);

        // Then
        assertThat(result).isEqualTo(stageWithNulls);
        assertThat(result.getName()).isNull();
        assertThat(result.getOrderNumber()).isZero();
        assertThat(result.getEvent()).isNull();
        verify(stageRepository).save(stageWithNulls);
    }

    @Test
    @DisplayName("getStagesByEventId - Debe manejar múltiples etapas del mismo evento")
    void getStagesByEventId_shouldHandleMultipleStagesFromSameEvent() {
        // Given
        Stage stage2 = TestDataBuilder.buildStage(2L, "Stage 2", testEvent, 2);
        Stage stage3 = TestDataBuilder.buildStage(3L, "Stage 3", testEvent, 3);

        Event otherEvent = TestDataBuilder.buildEvent(2L, "Other Event");
        Stage stageFromOtherEvent = TestDataBuilder.buildStage(4L, "Other Stage", otherEvent, 1);

        List<Stage> allStages = Arrays.asList(testStage, stage2, stage3, stageFromOtherEvent);
        when(stageRepository.findAll()).thenReturn(allStages);

        // When
        List<Stage> result = stageService.getStagesByEventId(1L); // Then
        assertThat(result)
                .hasSize(3)
                .containsExactlyInAnyOrder(testStage, stage2, stage3)
                .allMatch(stage -> stage.getEvent().getId().equals(1L));
        verify(stageRepository).findAll();
    }

    @Test
    @DisplayName("updateStage - Debe actualizar todos los campos de la etapa")
    void updateStage_shouldUpdateAllStageFields() {
        // Given
        Event newEvent = TestDataBuilder.buildEvent(2L, "New Event");
        Stage updatedStage = TestDataBuilder.buildStage(null, "Completely Updated Stage", newEvent, 10);

        Stage originalStage = TestDataBuilder.buildStage(1L, "Original Stage", testEvent, 1);
        when(stageRepository.findById(1L)).thenReturn(Optional.of(originalStage));
        when(stageRepository.save(any(Stage.class))).thenAnswer(invocation -> {
            Stage saved = invocation.getArgument(0);
            saved.setId(1L); // Simular que mantiene el ID original
            return saved;
        });

        // When
        Stage result = stageService.updateStage(1L, updatedStage);

        // Then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Completely Updated Stage");
        assertThat(result.getOrderNumber()).isEqualTo(10);
        assertThat(result.getEvent()).isEqualTo(newEvent);
        verify(stageRepository).findById(1L);
        verify(stageRepository).save(any(Stage.class));
    }

    // ========== INTEGRATION TESTS ==========

    @Test
    @DisplayName("Flow completo - Crear, buscar, actualizar y eliminar etapa")
    void fullFlow_createFindUpdateDeleteStage() {
        // Given
        Stage newStage = TestDataBuilder.buildStage(null, "Flow Stage", testEvent, 1);
        Stage savedStage = TestDataBuilder.buildStage(10L, "Flow Stage", testEvent, 1);
        Stage updatedStage = TestDataBuilder.buildStage(null, "Updated Flow Stage", testEvent, 2);
        Stage finalStage = TestDataBuilder.buildStage(10L, "Updated Flow Stage", testEvent, 2);
        when(stageRepository.save(newStage)).thenReturn(savedStage);
        when(stageRepository.findById(10L)).thenReturn(Optional.of(savedStage));
        when(stageRepository.save(argThat(stage -> stage != newStage))).thenReturn(finalStage);
        doNothing().when(stageRepository).deleteById(10L);

        // When - Create
        Stage created = stageService.createStage(newStage);

        // Then - Verify creation
        assertThat(created).isEqualTo(savedStage);
        assertThat(created.getId()).isEqualTo(10L);

        // When - Find
        Optional<Stage> found = stageService.getStageById(10L); // Then - Verify find
        assertThat(found).isPresent()
                .contains(savedStage);

        // When - Update
        Stage updated = stageService.updateStage(10L, updatedStage);

        // Then - Verify update
        assertThat(updated.getName()).isEqualTo("Updated Flow Stage");
        assertThat(updated.getOrderNumber()).isEqualTo(2);

        // When - Delete
        stageService.deleteStage(10L); // Then - Verify all operations
        verify(stageRepository).save(newStage);
        verify(stageRepository, times(2)).findById(10L);
        verify(stageRepository, times(2)).save(any(Stage.class));
        verify(stageRepository).deleteById(10L);
    }

    @Test
    @DisplayName("Filtrado por evento - Debe filtrar correctamente por ID de evento")
    void eventFiltering_shouldFilterCorrectlyByEventId() {
        // Given
        Event event1 = TestDataBuilder.buildEvent(1L, "Event 1");
        Event event2 = TestDataBuilder.buildEvent(2L, "Event 2");
        Event event3 = TestDataBuilder.buildEvent(3L, "Event 3");

        Stage stage1 = TestDataBuilder.buildStage(1L, "Stage 1", event1, 1);
        Stage stage2 = TestDataBuilder.buildStage(2L, "Stage 2", event2, 1);
        Stage stage3 = TestDataBuilder.buildStage(3L, "Stage 3", event1, 2);
        Stage stage4 = TestDataBuilder.buildStage(4L, "Stage 4", event3, 1);

        List<Stage> allStages = Arrays.asList(stage1, stage2, stage3, stage4);
        when(stageRepository.findAll()).thenReturn(allStages);

        // When
        List<Stage> event1Stages = stageService.getStagesByEventId(1L);
        List<Stage> event2Stages = stageService.getStagesByEventId(2L);
        List<Stage> event3Stages = stageService.getStagesByEventId(3L);
        List<Stage> nonExistentEventStages = stageService.getStagesByEventId(999L);

        // Then
        assertThat(event1Stages)
                .hasSize(2)
                .containsExactlyInAnyOrder(stage1, stage3);
        assertThat(event2Stages)
                .hasSize(1)
                .containsExactly(stage2);
        assertThat(event3Stages)
                .hasSize(1)
                .containsExactly(stage4);
        assertThat(nonExistentEventStages).isEmpty();
        verify(stageRepository, times(4)).findAll();
    }
}
