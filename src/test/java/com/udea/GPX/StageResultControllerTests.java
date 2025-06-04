package com.udea.GPX;

import com.udea.GPX.controller.StageResultController;
import com.udea.GPX.dto.ClasificacionCompletaDTO;
import com.udea.GPX.model.StageResult;
import com.udea.GPX.model.User;
import com.udea.GPX.service.EventService;
import com.udea.GPX.service.StageResultService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.LocalDate;
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

    @Mock
    private HttpServletRequest request;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private StageResultController stageResultController;

    private User buildUser(Long id, boolean admin) {
        return new User(
                id,
                "TestUser",
                "TestLastName",
                "123456789",
                "3101234567",
                admin,
                "test@ejemplo.com",
                "piloto",
                LocalDate.of(1990, 1, 1),
                "CC",
                "EquipoTest",
                "EPSX",
                "O+",
                "3000000000",
                "Ninguna",
                "wikilocUser",
                "SeguroX",
                "terrapirataUser",
                "instaUser",
                "fbUser");
    }

    private StageResult buildStageResult(Long id) {
        StageResult sr = new StageResult();
        sr.setId(id);
        sr.setTimestamp(LocalDateTime.now());
        sr.setLatitude(1);
        sr.setLongitude(1);
        sr.setElapsedTimeSeconds(100);
        return sr;
    }

    private ClasificacionCompletaDTO buildClasificacionDTO(Long vehicleId, String vehicleName, String driverName,
            Long categoryId, String categoryName, List<ClasificacionCompletaDTO.StageTimeCellDTO> stageTimes,
            Integer totalTime, String userPicture, String teamName) {
        return new ClasificacionCompletaDTO(
                vehicleId,
                vehicleName,
                driverName,
                categoryId,
                categoryName,
                stageTimes,
                totalTime,
                userPicture,
                teamName);
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        ReflectionTestUtils.setField(stageResultController, "request", request);
    }

    @Test
    void saveResult_whenAdmin_shouldReturnCreated() {
        // Arrange
        User adminUser = buildUser(1L, true);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        StageResult sr = buildStageResult(1L);
        when(stageResultService.saveResult(any())).thenReturn(sr);

        // Act
        ResponseEntity<StageResult> response = stageResultController.saveResult(sr);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(sr, response.getBody());
    }

    @Test
    void saveResult_whenNotAdmin_shouldReturnForbidden() {
        // Arrange
        User nonAdminUser = buildUser(1L, false);
        when(authentication.getPrincipal()).thenReturn(nonAdminUser);
        StageResult sr = buildStageResult(1L);

        // Act
        ResponseEntity<StageResult> response = stageResultController.saveResult(sr);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void updateResult_whenAdmin_shouldReturnOK() {
        // Arrange
        User adminUser = buildUser(1L, true);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        StageResult sr = buildStageResult(1L);
        when(stageResultService.updateResult(eq(1L), any())).thenReturn(sr);

        // Act
        ResponseEntity<StageResult> response = stageResultController.updateResult(1L, sr);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sr, response.getBody());
    }

    @Test
    void updateResult_whenNotAdmin_shouldReturnForbidden() {
        // Arrange
        User nonAdminUser = buildUser(1L, false);
        when(authentication.getPrincipal()).thenReturn(nonAdminUser);
        StageResult sr = buildStageResult(1L);

        // Act
        ResponseEntity<StageResult> response = stageResultController.updateResult(1L, sr);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void updateResult_whenNotFound_shouldReturnNotFound() {
        // Arrange
        User adminUser = buildUser(1L, true);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        StageResult sr = buildStageResult(1L);
        when(stageResultService.updateResult(eq(1L), any())).thenThrow(new RuntimeException("Resultado no encontrado"));

        // Act
        ResponseEntity<StageResult> response = stageResultController.updateResult(1L, sr);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void deleteResult_whenAdmin_shouldReturnNoContent() {
        // Arrange
        User adminUser = buildUser(1L, true);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        doNothing().when(stageResultService).deleteResult(1L);

        // Act
        ResponseEntity<Void> response = stageResultController.deleteResult(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void deleteResult_whenNotAdmin_shouldReturnForbidden() {
        // Arrange
        User nonAdminUser = buildUser(1L, false);
        when(authentication.getPrincipal()).thenReturn(nonAdminUser);

        // Act
        ResponseEntity<Void> response = stageResultController.deleteResult(1L);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void getClasificacionCompleta_general_shouldReturnOK() {
        // Arrange
        ClasificacionCompletaDTO dto = buildClasificacionDTO(1L, "Vehículo", "Piloto", 1L, "Cat",
                Collections.emptyList(), 100, "foto.jpg", "EquipoX");
        when(stageResultService.getClasificacionGeneral(1L)).thenReturn(List.of(dto));
        doNothing().when(stageResultService).updateElapsedTimesForEvent(1L);

        // Act
        ResponseEntity<List<ClasificacionCompletaDTO>> response = stageResultController.getClasificacionCompleta(1L,
                null, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(dto, response.getBody().get(0));
    }

    @Test
    void getClasificacionCompleta_porCategoria_shouldReturnOK() {
        // Arrange
        ClasificacionCompletaDTO dto = buildClasificacionDTO(1L, "Vehículo", "Piloto", 2L, "Cat2",
                Collections.emptyList(), 200, "foto.jpg", "EquipoY");
        when(stageResultService.getClasificacionPorCategoria(1L, 2L)).thenReturn(List.of(dto));
        doNothing().when(stageResultService).updateElapsedTimesForEvent(1L);

        // Act
        ResponseEntity<List<ClasificacionCompletaDTO>> response = stageResultController.getClasificacionCompleta(1L, 2L,
                null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(dto, response.getBody().get(0));
    }

    @Test
    void getClasificacionCompleta_porStage_shouldReturnOK() {
        // Arrange
        ClasificacionCompletaDTO dto = buildClasificacionDTO(1L, "Vehículo", "Piloto", 1L, "Cat",
                Collections.emptyList(), 150, "foto.jpg", "EquipoZ");
        when(stageResultService.getClasificacionPorStage(1L, 3)).thenReturn(List.of(dto));
        doNothing().when(stageResultService).updateElapsedTimesForEvent(1L);

        // Act
        ResponseEntity<List<ClasificacionCompletaDTO>> response = stageResultController.getClasificacionCompleta(1L,
                null,
                3);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(dto, response.getBody().get(0));
    }

    @Test
    void aplicarPenalizacion_whenAdmin_shouldReturnOK() {
        // Arrange
        User adminUser = buildUser(1L, true);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        StageResult sr = buildStageResult(1L);
        when(stageResultService.aplicarPenalizacion(eq(1L), eq(Duration.ofSeconds(60)), eq(Duration.ofSeconds(30)),
                eq(null))).thenReturn(sr);

        // Act
        ResponseEntity<StageResult> response = stageResultController.aplicarPenalizacion(1L, Duration.ofSeconds(60),
                Duration.ofSeconds(30), null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sr, response.getBody());
    }

    @Test
    void aplicarPenalizacion_whenNotAdmin_shouldReturnForbidden() {
        // Arrange
        User nonAdminUser = buildUser(1L, false);
        when(authentication.getPrincipal()).thenReturn(nonAdminUser);

        // Act
        ResponseEntity<StageResult> response = stageResultController.aplicarPenalizacion(1L, Duration.ofSeconds(60),
                Duration.ofSeconds(30), null);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void updateElapsedTimesForEvent_whenAdmin_shouldReturnOK() {
        // Arrange
        User adminUser = buildUser(1L, true);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        doNothing().when(stageResultService).updateElapsedTimesForEvent(1L);

        // Act
        ResponseEntity<Void> response = stageResultController.updateElapsedTimesForEvent(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateElapsedTimesForEvent_whenNotAdmin_shouldReturnForbidden() {
        // Arrange
        User nonAdminUser = buildUser(1L, false);
        when(authentication.getPrincipal()).thenReturn(nonAdminUser);

        // Act
        ResponseEntity<Void> response = stageResultController.updateElapsedTimesForEvent(1L);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}
