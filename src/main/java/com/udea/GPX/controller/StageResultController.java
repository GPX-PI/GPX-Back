package com.udea.gpx.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.udea.gpx.dto.ClasificacionCompletaDTO;
import com.udea.gpx.dto.CreateStageResultDTO;
import com.udea.gpx.dto.UpdateStageResultDTO;
import com.udea.gpx.model.StageResult;
import com.udea.gpx.service.StageResultService;
import com.udea.gpx.util.AuthUtils;

import jakarta.validation.Valid;
import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/api/stageresults")
public class StageResultController {

    private static final Logger logger = LoggerFactory.getLogger(StageResultController.class);

    private final StageResultService stageResultService;
    private final AuthUtils authUtils;

    public StageResultController(StageResultService stageResultService, AuthUtils authUtils) {
        this.stageResultService = stageResultService;
        this.authUtils = authUtils;
    }

    @PostMapping
    public ResponseEntity<StageResult> createResult(@Valid @RequestBody CreateStageResultDTO createDTO) {
        if (!authUtils.isCurrentUserAdmin()) {
            logger.warn("Unauthorized attempt to create stage result");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            StageResult result = stageResultService.createResult(createDTO);
            logger.info("Stage result created successfully with ID: {}", result.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid data provided for stage result creation: {}", e.getMessage());
            return ResponseEntity.badRequest().header("Error-Message", e.getMessage()).build();
        } catch (RuntimeException e) {
            logger.error("Unexpected error creating stage result: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<StageResult> updateResult(@PathVariable Long id,
            @Valid @RequestBody UpdateStageResultDTO updateDTO) {
        if (!authUtils.isCurrentUserAdmin()) {
            logger.warn("Unauthorized attempt to update stage result with ID: {}", id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            StageResult result = stageResultService.updateResultFromDTO(id, updateDTO);
            logger.info("Stage result updated successfully with ID: {}", id);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid data provided for stage result update: {}", e.getMessage());
            return ResponseEntity.badRequest().header("Error-Message", e.getMessage()).build();
        } catch (RuntimeException e) {
            logger.error("Unexpected error updating stage result with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResult(@PathVariable Long id) {
        if (!authUtils.isCurrentUserAdmin()) {
            logger.warn("Unauthorized attempt to delete stage result with ID: {}", id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            stageResultService.deleteResult(id);
            logger.info("Stage result deleted successfully with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.error("Error deleting stage result with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/clasificacion")
    public ResponseEntity<List<ClasificacionCompletaDTO>> getClasificacionCompleta(
            @RequestParam Long eventId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer stageNumber) {

        stageResultService.updateElapsedTimesForEvent(eventId);

        if (categoryId != null) {
            return ResponseEntity.ok(stageResultService.getClasificacionPorCategoria(eventId, categoryId));
        }
        if (stageNumber != null) {
            return ResponseEntity.ok(stageResultService.getClasificacionPorStage(eventId, stageNumber));
        }
        return ResponseEntity.ok(stageResultService.getClasificacionGeneral(eventId));
    }

    @GetMapping("/clasificacionbystage")
    public ResponseEntity<List<ClasificacionCompletaDTO>> getClasificacionByStage(
            @RequestParam Long eventId,
            @RequestParam Integer stageNumber) {
        stageResultService.updateElapsedTimesForEvent(eventId);
        return ResponseEntity.ok(stageResultService.getClasificacionPorStage(eventId, stageNumber));
    }

    @PostMapping("/update-elapsed-times/{eventId}")
    public ResponseEntity<Void> updateElapsedTimesForEvent(@PathVariable Long eventId) {
        if (!authUtils.isCurrentUserAdmin()) {
            logger.warn("Unauthorized attempt to update elapsed times for event ID: {}", eventId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            stageResultService.updateElapsedTimesForEvent(eventId);
            logger.info("Elapsed times updated successfully for event ID: {}", eventId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            logger.error("Error updating elapsed times for event ID {}: {}", eventId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/penalizacion/{id}")
    public ResponseEntity<StageResult> aplicarPenalizacion(
            @PathVariable Long id,
            @RequestParam(required = false) String penaltyWaypoint,
            @RequestParam(required = false) String penaltySpeed,
            @RequestParam(required = false) String discountClaim) {

        if (!authUtils.isCurrentUserAdmin()) {
            logger.warn("Unauthorized attempt to apply penalty to stage result ID: {}", id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            Duration penaltyWaypointDuration = parseDuration(penaltyWaypoint);
            Duration penaltySpeedDuration = parseDuration(penaltySpeed);
            Duration discountClaimDuration = parseDuration(discountClaim);

            StageResult result = stageResultService.aplicarPenalizacion(id, penaltyWaypointDuration,
                    penaltySpeedDuration, discountClaimDuration);
            logger.info("Penalty applied successfully to stage result ID: {}", id);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid penalty data for stage result ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (RuntimeException e) {
            logger.error("Error applying penalty to stage result ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/by-event/{eventId}")
    public ResponseEntity<List<StageResult>> getResultsByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(stageResultService.getResultsByEvent(eventId));
    }

    private Duration parseDuration(String durationString) {
        if (durationString == null || durationString.trim().isEmpty() || "PT0S".equals(durationString)) {
            return Duration.ZERO;
        }
        try {
            return Duration.parse(durationString);
        } catch (Exception e) {
            logger.warn("Failed to parse duration '{}', using Duration.ZERO", durationString);
            return Duration.ZERO;
        }
    }
}