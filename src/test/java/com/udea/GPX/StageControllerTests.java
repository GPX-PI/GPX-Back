package com.udea.gpx;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
import org.springframework.test.util.ReflectionTestUtils;

import com.udea.gpx.controller.StageController;
import com.udea.gpx.model.Event;
import com.udea.gpx.model.Stage;
import com.udea.gpx.model.User;
import com.udea.gpx.service.StageService;
import com.udea.gpx.util.AuthUtils;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class StageControllerTests {

    @Mock
    private StageService stageService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Authentication authentication;

    @Mock
    private AuthUtils authUtils;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private StageController stageController;

    private User buildUser(boolean admin) {
        User user = new User();
        user.setId(1L);
        user.setAdmin(admin);
        return user;
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        ReflectionTestUtils.setField(stageController, "request", request);
    }

    @Test
    void getAllStages_shouldReturnOK() {
        // Arrange
        List<Stage> stages = Arrays.asList(
                new Stage(1L, "Stage 1", 1, false, new Event()),
                new Stage(2L, "Stage 2", 2, true, new Event()));
        when(stageService.getAllStages()).thenReturn(stages);

        // Act
        ResponseEntity<List<Stage>> response = stageController.getAllStages();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    void getStageById_whenStageExists_shouldReturnOK() {
        // Arrange
        Long stageId = 1L;
        Stage stage = new Stage(stageId, "Stage 1", 1, false, new Event());
        when(stageService.getStageById(stageId)).thenReturn(Optional.of(stage));

        // Act
        ResponseEntity<Stage> response = stageController.getStageById(stageId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Stage 1", Objects.requireNonNull(response.getBody()).getName());
    }

    @Test
    void getStageById_whenStageNotExists_shouldReturnNotFound() {
        // Arrange
        Long stageId = 1L;
        when(stageService.getStageById(stageId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Stage> response = stageController.getStageById(stageId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getStagesByEventId_shouldReturnOK() {
        // Arrange
        Long eventId = 1L;
        List<Stage> stages = Arrays.asList(
                new Stage(1L, "Stage 1", 1, false, new Event()),
                new Stage(2L, "Stage 2", 2, true, new Event()));
        when(stageService.getStagesByEventId(eventId)).thenReturn(stages);

        // Act
        ResponseEntity<List<Stage>> response = stageController.getStagesByEventId(eventId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    void createStage_whenAdmin_shouldReturnCreated() {
        User adminUser = buildUser(true);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        Stage stage = new Stage(null, "Stage X", 1, false, new Event());
        Stage savedStage = new Stage(10L, "Stage X", 1, false, new Event());
        when(stageService.createStage(stage)).thenReturn(savedStage);
        ResponseEntity<Stage> response = stageController.createStage(stage);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(savedStage, response.getBody());
    }

    @Test
    void createStage_whenNotAdmin_shouldReturnForbidden() {
        User nonAdminUser = buildUser(false);
        when(authentication.getPrincipal()).thenReturn(nonAdminUser);
        Stage stage = new Stage(null, "Stage X", 1, false, new Event());
        ResponseEntity<Stage> response = stageController.createStage(stage);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void updateStage_whenAdmin_shouldReturnOK() {
        User adminUser = buildUser(true);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        Stage stage = new Stage(null, "Stage Y", 2, true, new Event());
        Stage updatedStage = new Stage(2L, "Stage Y", 2, true, new Event());
        when(stageService.updateStage(2L, stage)).thenReturn(updatedStage);
        ResponseEntity<Stage> response = stageController.updateStage(2L, stage);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedStage, response.getBody());
    }

    @Test
    void updateStage_whenNotAdmin_shouldReturnForbidden() {
        User nonAdminUser = buildUser(false);
        when(authentication.getPrincipal()).thenReturn(nonAdminUser);
        Stage stage = new Stage(null, "Stage Y", 2, true, new Event());
        ResponseEntity<Stage> response = stageController.updateStage(2L, stage);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void updateStage_whenAdminAndNotFound_shouldReturnNotFound() {
        User adminUser = buildUser(true);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        Stage stage = new Stage(null, "Stage Y", 2, true, new Event());
        doThrow(new RuntimeException()).when(stageService).updateStage(99L, stage);
        ResponseEntity<Stage> response = stageController.updateStage(99L, stage);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteStage_whenAdmin_shouldReturnNoContent() {
        User adminUser = buildUser(true);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        ResponseEntity<Void> response = stageController.deleteStage(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(stageService).deleteStage(1L);
    }

    @Test
    void deleteStage_whenNotAdmin_shouldReturnForbidden() {
        User nonAdminUser = buildUser(false);
        when(authentication.getPrincipal()).thenReturn(nonAdminUser);
        ResponseEntity<Void> response = stageController.deleteStage(1L);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

}