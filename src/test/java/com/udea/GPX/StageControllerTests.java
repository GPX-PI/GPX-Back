package com.udea.GPX;

import com.udea.GPX.controller.StageController;
import com.udea.GPX.model.Event;
import com.udea.GPX.model.Stage;
import com.udea.GPX.service.StageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@SpringBootTest
public class StageControllerTests {

    @Mock
    private StageService stageService;

    @InjectMocks
    private StageController stageController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllStages_shouldReturnOK() {
        // Arrange
        List<Stage> stages = Arrays.asList(
                new Stage(1L, "Stage 1", 1, false, new Event()),
                new Stage(2L, "Stage 2", 2, true, new Event())
        );
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
                new Stage(2L, "Stage 2", 2, true, new Event())
        );
        when(stageService.getStagesByEventId(eventId)).thenReturn(stages);

        // Act
        ResponseEntity<List<Stage>> response = stageController.getStagesByEventId(eventId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
    }

}