package com.udea.GPX;

import com.udea.GPX.controller.StageResultController;
import com.udea.GPX.dto.ClasificacionCompletaDTO;
import com.udea.GPX.model.StageResult;
import com.udea.GPX.service.EventService;
import com.udea.GPX.service.StageResultService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class StageResultControllerTests {

    @Mock
    private StageResultService stageResultService;

    @Mock
    private EventService eventService;

    @InjectMocks
    private StageResultController stageResultController;

    private StageResult buildStageResult(Long id) {
        StageResult sr = new StageResult();
        sr.setId(id);
        sr.setTimestamp(LocalDateTime.now());
        sr.setLatitude(1);
        sr.setLongitude(1);
        sr.setElapsedTimeSeconds(100);
        return sr;
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveResult_shouldReturnCreated() {
        StageResult sr = buildStageResult(1L);
        when(stageResultService.saveResult(any())).thenReturn(sr);
        ResponseEntity<StageResult> response = stageResultController.saveResult(sr);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(sr, response.getBody());
    }

    @Test
    void updateResult_shouldReturnOK() {
        StageResult sr = buildStageResult(1L);
        when(stageResultService.updateResult(eq(1L), any())).thenReturn(sr);
        ResponseEntity<StageResult> response = stageResultController.updateResult(1L, sr);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sr, response.getBody());
    }

    @Test
    void updateResult_shouldReturnNotFound() {
        when(stageResultService.updateResult(eq(1L), any())).thenThrow(new RuntimeException("Resultado no encontrado"));
        ResponseEntity<StageResult> response = stageResultController.updateResult(1L, buildStageResult(1L));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteResult_shouldReturnNoContent() {
        doNothing().when(stageResultService).deleteResult(1L);
        ResponseEntity<Void> response = stageResultController.deleteResult(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void getClasificacionCompleta_general() {
        ClasificacionCompletaDTO dto = new ClasificacionCompletaDTO(1L, "Vehículo", "Piloto", 1L, "Cat",
                Collections.emptyList(), 100);
        when(stageResultService.getClasificacionGeneral(1L)).thenReturn(List.of(dto));
        doNothing().when(stageResultService).updateElapsedTimesForEvent(1L);
        ResponseEntity<List<ClasificacionCompletaDTO>> response = stageResultController.getClasificacionCompleta(1L,
                null, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(dto, response.getBody().get(0));
    }

    @Test
    void getClasificacionCompleta_porCategoria() {
        ClasificacionCompletaDTO dto = new ClasificacionCompletaDTO(1L, "Vehículo", "Piloto", 2L, "Cat2",
                Collections.emptyList(), 200);
        when(stageResultService.getClasificacionPorCategoria(1L, 2L)).thenReturn(List.of(dto));
        doNothing().when(stageResultService).updateElapsedTimesForEvent(1L);
        ResponseEntity<List<ClasificacionCompletaDTO>> response = stageResultController.getClasificacionCompleta(1L, 2L,
                null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(dto, response.getBody().get(0));
    }

    @Test
    void getClasificacionCompleta_porStage() {
        ClasificacionCompletaDTO dto = new ClasificacionCompletaDTO(1L, "Vehículo", "Piloto", 1L, "Cat",
                Collections.emptyList(), 150);
        when(stageResultService.getClasificacionPorStage(1L, 3)).thenReturn(List.of(dto));
        doNothing().when(stageResultService).updateElapsedTimesForEvent(1L);
        ResponseEntity<List<ClasificacionCompletaDTO>> response = stageResultController.getClasificacionCompleta(1L,
                null, 3);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(dto, response.getBody().get(0));
    }

    @Test
    void aplicarPenalizacion_shouldReturnOK() {
        StageResult sr = buildStageResult(1L);
        when(stageResultService.aplicarPenalizacion(eq(1L), any(), any(), any())).thenReturn(sr);
        ResponseEntity<StageResult> response = stageResultController.aplicarPenalizacion(1L, Duration.ofSeconds(10),
                Duration.ofSeconds(20), Duration.ofSeconds(5));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sr, response.getBody());
    }

    @Test
    void updateElapsedTimesForEvent_shouldReturnOK() {
        doNothing().when(stageResultService).updateElapsedTimesForEvent(1L);
        ResponseEntity<Void> response = stageResultController.updateElapsedTimesForEvent(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
