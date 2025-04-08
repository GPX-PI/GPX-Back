package com.udea.GPX;

import com.udea.GPX.controller.StageResultController;
import com.udea.GPX.dto.ClasificacionDTO;
import com.udea.GPX.model.Category;
import com.udea.GPX.model.Event;
import com.udea.GPX.model.StageResult;
import com.udea.GPX.model.Vehicle;
import com.udea.GPX.service.CategoryService;
import com.udea.GPX.service.EventService;
import com.udea.GPX.service.StageResultService;
import com.udea.GPX.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@SpringBootTest
public class StageResultControllerTests {

    @Mock
    private StageResultService stageResultService;

    @Mock
    private EventService eventService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private VehicleService vehicleService;

    @InjectMocks
    private StageResultController stageResultController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllResults_shouldReturnOK() {
        // Arrange
        List<StageResult> stageResults = Arrays.asList(
                new StageResult(),
                new StageResult()
        );
        when(stageResultService.getAllResults()).thenReturn(stageResults);

        // Act
        ResponseEntity<List<StageResult>> response = stageResultController.getAllResults();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    void getResultById_whenResultExists_shouldReturnOK() {
        // Arrange
        Long resultId = 1L;
        StageResult stageResult = new StageResult();
        stageResult.setId(resultId);
        when(stageResultService.getResultById(resultId)).thenReturn(Optional.of(stageResult));

        // Act
        ResponseEntity<StageResult> response = stageResultController.getResultById(resultId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(resultId, Objects.requireNonNull(response.getBody()).getId());
    }

    @Test
    void getResultById_whenResultNotExists_shouldReturnNotFound() {
        // Arrange
        Long resultId = 1L;
        when(stageResultService.getResultById(resultId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<StageResult> response = stageResultController.getResultById(resultId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void saveResult_shouldReturnCreated() {
        // Arrange
        StageResult stageResult = new StageResult();
        when(stageResultService.saveResult(stageResult)).thenReturn(stageResult);

        // Act
        ResponseEntity<StageResult> response = stageResultController.saveResult(stageResult);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(stageResult, response.getBody());
    }

    @Test
    void updateResult_whenResultExists_shouldReturnOK() {
        // Arrange
        Long resultId = 1L;
        StageResult existingResult = new StageResult();
        existingResult.setId(resultId);
        StageResult updatedResult = new StageResult();
        updatedResult.setId(resultId);

        when(stageResultService.updateResult(resultId, updatedResult)).thenReturn(updatedResult);

        // Act
        ResponseEntity<StageResult> response = stageResultController.updateResult(resultId, updatedResult);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedResult, response.getBody());
    }

    @Test
    void updateResult_whenResultNotExists_shouldReturnNotFound() {
        // Arrange
        Long resultId = 1L;
        StageResult updatedResult = new StageResult();
        when(stageResultService.updateResult(resultId, updatedResult)).thenThrow(new RuntimeException("Resultado no encontrado"));

        // Act
        ResponseEntity<StageResult> response = stageResultController.updateResult(resultId, updatedResult);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void deleteResult_shouldReturnNoContent() {
        // Arrange
        Long resultId = 1L;

        // Act
        ResponseEntity<Void> response = stageResultController.deleteResult(resultId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(stageResultService, times(1)).deleteResult(resultId);
    }

    @Test
    void getResultsByCategoryAndStages_shouldReturnOK() {
        // Arrange
        Long categoryId = 1L;
        int stageStart = 1;
        int stageEnd = 2;
        List<StageResult> stageResults = Arrays.asList(
                new StageResult(),
                new StageResult()
        );
        when(stageResultService.getResultsByCategoryAndStages(categoryId, stageStart, stageEnd)).thenReturn(stageResults);

        // Act
        ResponseEntity<List<StageResult>> response = stageResultController.getResultsByCategoryAndStages(categoryId, stageStart, stageEnd);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    void calcularTiempoTotal_whenVehicleAndEventExist_shouldReturnOK() {
        // Arrange
        Long vehicleId = 1L;
        Long eventId = 1L;
        Vehicle vehicle = new Vehicle();
        Event event = new Event();
        Duration totalTime = Duration.ofHours(2);

        when(vehicleService.getVehicleById(vehicleId)).thenReturn(Optional.of(vehicle));
        when(eventService.getEventById(eventId)).thenReturn(Optional.
                of(event));
        when(stageResultService.calcularTiempoTotal(vehicle, event)).thenReturn(totalTime);

        // Act
        ResponseEntity<Duration> response = stageResultController.calcularTiempoTotal(vehicleId, eventId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(totalTime, response.getBody());
    }

    @Test
    void calcularTiempoTotal_whenVehicleNotExists_shouldReturnNotFound() {
        // Arrange
        Long vehicleId = 1L;
        Long eventId = 1L;

        when(vehicleService.getVehicleById(vehicleId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Duration> response = stageResultController.calcularTiempoTotal(vehicleId, eventId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void calcularTiempoTotal_whenEventNotExists_shouldReturnNotFound() {
        // Arrange
        Long vehicleId = 1L;
        Long eventId = 1L;
        Vehicle vehicle = new Vehicle();

        when(vehicleService.getVehicleById(vehicleId)).thenReturn(Optional.of(vehicle));
        when(eventService.getEventById(eventId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Duration> response = stageResultController.calcularTiempoTotal(vehicleId, eventId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getClasificacionPorCategoria_whenEventAndCategoryExist_shouldReturnOK() {
        // Arrange
        Long eventId = 1L;
        Long categoryId = 1L;
        Event event = new Event();
        Category category = new Category();
        List<ClasificacionDTO> clasificacion = Arrays.asList(
                new ClasificacionDTO(new Vehicle(), Duration.ofHours(1)),
                new ClasificacionDTO(new Vehicle(), Duration.ofHours(2))
        );

        when(eventService.getEventById(eventId)).thenReturn(Optional.of(event));
        when(categoryService.getCategoryById(categoryId)).thenReturn(category);
        when(stageResultService.getClasificacionPorCategoria(event, category)).thenReturn(clasificacion);

        // Act
        ResponseEntity<List<ClasificacionDTO>> response = stageResultController.getClasificacionPorCategoria(eventId, categoryId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    void getClasificacionPorCategoria_whenEventNotExists_shouldReturnNotFound() {
        // Arrange
        Long eventId = 1L;
        Long categoryId = 1L;

        when(eventService.getEventById(eventId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<List<ClasificacionDTO>> response = stageResultController.getClasificacionPorCategoria(eventId, categoryId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getClasificacionPorCategoria_whenCategoryNotExists_shouldReturnNotFound() {
        // Arrange
        Long eventId = 1L;
        Long categoryId = 1L;
        Event event = new Event();

        when(eventService.getEventById(eventId)).thenReturn(Optional.of(event));
        when(categoryService.getCategoryById(categoryId)).thenReturn(null);

        // Act
        ResponseEntity<List<ClasificacionDTO>> response = stageResultController.getClasificacionPorCategoria(eventId, categoryId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getResultsByStageRange_whenEventExists_shouldReturnOK() {
        // Arrange
        Long eventId = 1L;
        int stageStart = 1;
        int stageEnd = 2;
        Event event = new Event();
        List<StageResult> stageResults = Arrays.asList(
                new StageResult(),
                new StageResult()
        );

        when(eventService.getEventById(eventId)).thenReturn(Optional.of(event));
        when(stageResultService.getResultsByStageRange(event, stageStart, stageEnd)).thenReturn(stageResults);

        // Act
        ResponseEntity<List<StageResult>> response = stageResultController.getResultsByStageRange(eventId, stageStart, stageEnd);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    void getResultsByStageRange_whenEventNotExists_shouldReturnNotFound() {
        // Arrange
        Long eventId = 1L;
        int stageStart = 1;
        int stageEnd = 2;

        when(eventService.getEventById(eventId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<List<StageResult>> response = stageResultController.getResultsByStageRange(eventId, stageStart, stageEnd);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}