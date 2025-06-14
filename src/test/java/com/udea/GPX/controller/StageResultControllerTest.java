package com.udea.gpx.controller;

import com.udea.gpx.dto.ClasificacionCompletaDTO;
import com.udea.gpx.dto.CreateStageResultDTO;
import com.udea.gpx.dto.UpdateStageResultDTO;
import com.udea.gpx.model.StageResult;
import com.udea.gpx.service.StageResultService;
import com.udea.gpx.util.AuthUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StageResultController Tests")
class StageResultControllerTest {
    @Mock
    private StageResultService stageResultService;
    @Mock
    private AuthUtils authUtils;
    @InjectMocks
    private StageResultController controller;

    @Test
    @DisplayName("Controller should be instantiated")
    void shouldInstantiateController() {
        assertThat(controller).isNotNull();
    }

    @Test
    @DisplayName("createResult - Forbidden for non-admin")
    void createResult_forbidden() {
        when(authUtils.isCurrentUserAdmin()).thenReturn(false);
        ResponseEntity<StageResult> response = controller.createResult(mock(CreateStageResultDTO.class));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("createResult - Success")
    void createResult_success() {
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        StageResult result = new StageResult();
        when(stageResultService.createResult(any())).thenReturn(result);
        ResponseEntity<StageResult> response = controller.createResult(mock(CreateStageResultDTO.class));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(result);
    }

    @Test
    @DisplayName("createResult - BadRequest on IllegalArgumentException")
    void createResult_badRequest() {
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        when(stageResultService.createResult(any())).thenThrow(new IllegalArgumentException("bad data"));
        ResponseEntity<StageResult> response = controller.createResult(mock(CreateStageResultDTO.class));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("createResult - InternalServerError on RuntimeException")
    void createResult_internalError() {
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        when(stageResultService.createResult(any())).thenThrow(new RuntimeException("fail"));
        ResponseEntity<StageResult> response = controller.createResult(mock(CreateStageResultDTO.class));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("updateResult - Forbidden for non-admin")
    void updateResult_forbidden() {
        when(authUtils.isCurrentUserAdmin()).thenReturn(false);
        UpdateStageResultDTO dto = mock(UpdateStageResultDTO.class);
        ResponseEntity<StageResult> response = controller.updateResult(1L, dto);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("updateResult - Success")
    void updateResult_success() {
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        UpdateStageResultDTO dto = mock(UpdateStageResultDTO.class);
        StageResult result = new StageResult();
        when(stageResultService.updateResultFromDTO(anyLong(), any())).thenReturn(result);
        ResponseEntity<StageResult> response = controller.updateResult(1L, dto);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(result);
    }

    @Test
    @DisplayName("updateResult - BadRequest on IllegalArgumentException")
    void updateResult_badRequest() {
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        UpdateStageResultDTO dto = mock(UpdateStageResultDTO.class);
        when(stageResultService.updateResultFromDTO(anyLong(), any()))
                .thenThrow(new IllegalArgumentException("bad data"));
        ResponseEntity<StageResult> response = controller.updateResult(1L, dto);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("updateResult - InternalServerError on RuntimeException")
    void updateResult_internalError() {
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        UpdateStageResultDTO dto = mock(UpdateStageResultDTO.class);
        when(stageResultService.updateResultFromDTO(anyLong(), any())).thenThrow(new RuntimeException("fail"));
        ResponseEntity<StageResult> response = controller.updateResult(1L, dto);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("deleteResult - Forbidden for non-admin")
    void deleteResult_forbidden() {
        when(authUtils.isCurrentUserAdmin()).thenReturn(false);
        ResponseEntity<Void> response = controller.deleteResult(1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("deleteResult - Success")
    void deleteResult_success() {
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        ResponseEntity<Void> response = controller.deleteResult(1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(stageResultService).deleteResult(1L);
    }

    @Test
    @DisplayName("deleteResult - InternalServerError on RuntimeException")
    void deleteResult_internalError() {
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        doThrow(new RuntimeException("fail")).when(stageResultService).deleteResult(anyLong());
        ResponseEntity<Void> response = controller.deleteResult(1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("getClasificacionCompleta - Success with eventId only")
    void getClasificacionCompleta_successWithEventIdOnly() {
        List<ClasificacionCompletaDTO> list = Collections.emptyList();
        when(stageResultService.getClasificacionGeneral(anyLong())).thenReturn(list);
        ResponseEntity<List<ClasificacionCompletaDTO>> response = controller.getClasificacionCompleta(1L, null, null);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(list);
        verify(stageResultService).updateElapsedTimesForEvent(1L);
    }

    @Test
    @DisplayName("getClasificacionCompleta - Success with categoryId")
    void getClasificacionCompleta_successWithCategoryId() {
        List<ClasificacionCompletaDTO> list = Collections.emptyList();
        when(stageResultService.getClasificacionPorCategoria(anyLong(), anyLong())).thenReturn(list);
        ResponseEntity<List<ClasificacionCompletaDTO>> response = controller.getClasificacionCompleta(1L, 2L, null);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(list);
        verify(stageResultService).updateElapsedTimesForEvent(1L);
    }

    @Test
    @DisplayName("getClasificacionCompleta - Success with stageNumber")
    void getClasificacionCompleta_successWithStageNumber() {
        List<ClasificacionCompletaDTO> list = Collections.emptyList();
        when(stageResultService.getClasificacionPorStage(anyLong(), anyInt())).thenReturn(list);
        ResponseEntity<List<ClasificacionCompletaDTO>> response = controller.getClasificacionCompleta(1L, null, 3);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(list);
        verify(stageResultService).updateElapsedTimesForEvent(1L);
    }

    @Test
    @DisplayName("getClasificacionByStage - Success")
    void getClasificacionByStage_success() {
        List<ClasificacionCompletaDTO> list = Collections.emptyList();
        when(stageResultService.getClasificacionPorStage(anyLong(), anyInt())).thenReturn(list);
        ResponseEntity<List<ClasificacionCompletaDTO>> response = controller.getClasificacionByStage(1L, 2);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(list);
        verify(stageResultService).updateElapsedTimesForEvent(1L);
    }

    @Test
    @DisplayName("updateElapsedTimesForEvent - Forbidden for non-admin")
    void updateElapsedTimesForEvent_forbidden() {
        when(authUtils.isCurrentUserAdmin()).thenReturn(false);
        ResponseEntity<Void> response = controller.updateElapsedTimesForEvent(1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("updateElapsedTimesForEvent - Success")
    void updateElapsedTimesForEvent_success() {
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        ResponseEntity<Void> response = controller.updateElapsedTimesForEvent(1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(stageResultService).updateElapsedTimesForEvent(1L);
    }

    @Test
    @DisplayName("updateElapsedTimesForEvent - InternalServerError on RuntimeException")
    void updateElapsedTimesForEvent_internalError() {
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        doThrow(new RuntimeException("fail")).when(stageResultService).updateElapsedTimesForEvent(anyLong());
        ResponseEntity<Void> response = controller.updateElapsedTimesForEvent(1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("aplicarPenalizacion - Forbidden for non-admin")
    void aplicarPenalizacion_forbidden() {
        when(authUtils.isCurrentUserAdmin()).thenReturn(false);
        ResponseEntity<StageResult> response = controller.aplicarPenalizacion(1L, null, null, null);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("aplicarPenalizacion - Success")
    void aplicarPenalizacion_success() {
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        StageResult result = new StageResult();
        when(stageResultService.aplicarPenalizacion(anyLong(), any(), any(), any())).thenReturn(result);
        ResponseEntity<StageResult> response = controller.aplicarPenalizacion(1L, "PT1H", "PT30M", "PT15M");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(result);
        verify(stageResultService).aplicarPenalizacion(eq(1L), eq(Duration.ofHours(1)), eq(Duration.ofMinutes(30)),
                eq(Duration.ofMinutes(15)));
    }

    @Test
    @DisplayName("aplicarPenalizacion - BadRequest on IllegalArgumentException")
    void aplicarPenalizacion_badRequest() {
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        when(stageResultService.aplicarPenalizacion(anyLong(), any(), any(), any()))
                .thenThrow(new IllegalArgumentException("bad data"));
        ResponseEntity<StageResult> response = controller.aplicarPenalizacion(1L, "PT1H", "PT30M", "PT15M");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("getResultsByEvent - Success")
    void getResultsByEvent_success() {
        List<StageResult> list = Collections.singletonList(new StageResult());
        when(stageResultService.getResultsByEvent(anyLong())).thenReturn(list);
        ResponseEntity<List<StageResult>> response = controller.getResultsByEvent(1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(list);
    }
    // Add more tests for endpoints and edge cases as needed
}
