package com.udea.gpx;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.udea.gpx.controller.StageResultController;
import com.udea.gpx.dto.ClasificacionCompletaDTO;
import com.udea.gpx.dto.CreateStageResultDTO;
import com.udea.gpx.dto.UpdateStageResultDTO;
import com.udea.gpx.model.StageResult;
import com.udea.gpx.model.User;
import com.udea.gpx.service.EventService;
import com.udea.gpx.service.StageResultService;
import com.udea.gpx.util.AuthUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class StageResultControllerTests {

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
    @Mock
    private AuthUtils authUtils;

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

        // Create the controller manually with mocked dependencies
        stageResultController = new StageResultController(stageResultService, authUtils);
    }

    @Test
    void createResult_whenAdmin_shouldReturnCreated() {
        // Arrange
        User adminUser = buildUser(1L, true);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        CreateStageResultDTO createDTO = new CreateStageResultDTO();
        StageResult sr = buildStageResult(1L);
        when(stageResultService.createResult(createDTO)).thenReturn(sr);

        // Act
        ResponseEntity<?> response = stageResultController.createResult(createDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody() instanceof StageResult);
        StageResult responseBody = (StageResult) response.getBody();
        assertNotNull(responseBody);
        assertEquals(sr, responseBody);
    }

    @Test
    void createResult_whenNotAdmin_shouldReturnForbidden() {
        // Arrange
        User nonAdminUser = buildUser(1L, false);
        when(authentication.getPrincipal()).thenReturn(nonAdminUser);
        when(authUtils.isCurrentUserAdmin()).thenReturn(false);
        CreateStageResultDTO createDTO = new CreateStageResultDTO();

        // Act
        ResponseEntity<?> response = stageResultController.createResult(createDTO);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void updateResult_whenAdmin_shouldReturnOK() {
        // Arrange
        User adminUser = buildUser(1L, true);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        UpdateStageResultDTO updateDTO = new UpdateStageResultDTO();
        StageResult sr = buildStageResult(1L);
        when(stageResultService.updateResultFromDTO(1L, updateDTO)).thenReturn(sr);

        // Act
        ResponseEntity<?> response = stageResultController.updateResult(1L, updateDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof StageResult);
        StageResult responseBody = (StageResult) response.getBody();
        assertNotNull(responseBody);
        assertEquals(sr, responseBody);
    }

    @Test
    void updateResult_whenNotAdmin_shouldReturnForbidden() {
        // Arrange
        User nonAdminUser = buildUser(1L, false);
        when(authentication.getPrincipal()).thenReturn(nonAdminUser);
        when(authUtils.isCurrentUserAdmin()).thenReturn(false);
        UpdateStageResultDTO updateDTO = new UpdateStageResultDTO();

        // Act
        ResponseEntity<?> response = stageResultController.updateResult(1L, updateDTO);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void aplicarPenalizacion_whenAdmin_shouldReturnOK() {
        // Arrange
        User adminUser = buildUser(1L, true);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        StageResult sr = buildStageResult(1L);
        when(stageResultService.aplicarPenalizacion(eq(1L), any(), any(), any())).thenReturn(sr);

        // Act
        ResponseEntity<?> response = stageResultController.aplicarPenalizacion(1L, "PT60S", "PT30S", null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(StageResult.class, response.getBody());
        StageResult responseBody = (StageResult) response.getBody();
        assertNotNull(responseBody);
        assertEquals(sr, responseBody);
    }

    @Test
    void aplicarPenalizacion_whenNotAdmin_shouldReturnForbidden() {
        // Arrange
        User nonAdminUser = buildUser(1L, false);
        when(authentication.getPrincipal()).thenReturn(nonAdminUser);
        when(authUtils.isCurrentUserAdmin()).thenReturn(false);

        // Act
        ResponseEntity<?> response = stageResultController.aplicarPenalizacion(1L, "PT60S", "PT30S", null);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void updateResult_whenAdminAndError_shouldReturnBadRequest() {
        // Arrange
        User adminUser = buildUser(1L, true);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        UpdateStageResultDTO updateDTO = new UpdateStageResultDTO();
        when(stageResultService.updateResultFromDTO(1L, updateDTO)).thenThrow(new IllegalArgumentException());

        // Act
        ResponseEntity<?> response = stageResultController.updateResult(1L, updateDTO);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void deleteResult_whenAdmin_shouldReturnNoContent() {
        // Arrange
        User adminUser = buildUser(1L, true);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
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
        when(authUtils.isCurrentUserAdmin()).thenReturn(false);

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
        List<ClasificacionCompletaDTO> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(1, responseBody.size());
        assertEquals(dto, responseBody.get(0));
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
        List<ClasificacionCompletaDTO> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(1, responseBody.size());
        assertEquals(dto, responseBody.get(0));
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
        List<ClasificacionCompletaDTO> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(1, responseBody.size());
        assertEquals(dto, responseBody.get(0));
    }
}
