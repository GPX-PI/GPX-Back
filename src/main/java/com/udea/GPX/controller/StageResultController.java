package com.udea.GPX.controller;

import com.udea.GPX.dto.ClasificacionCompletaDTO;
import com.udea.GPX.dto.CreateStageResultDTO;
import com.udea.GPX.dto.UpdateStageResultDTO;
import com.udea.GPX.model.StageResult;
import com.udea.GPX.service.StageResultService;
import com.udea.GPX.util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.time.Duration;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/stageresults")
public class StageResultController {

    @Autowired
    private StageResultService stageResultService;

    @Autowired
    private AuthUtils authUtils;

    @SuppressWarnings("unused")
    @Autowired
    private HttpServletRequest request;

    @PostMapping
    public ResponseEntity<StageResult> createResult(@Valid @RequestBody CreateStageResultDTO createDTO) {
        if (!authUtils.isCurrentUserAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            StageResult result = stageResultService.createResult(createDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<StageResult> updateResult(@PathVariable Long id,
            @Valid @RequestBody UpdateStageResultDTO updateDTO) {
        if (!authUtils.isCurrentUserAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            StageResult result = stageResultService.updateResultFromDTO(id, updateDTO);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResult(@PathVariable Long id) {
        if (!authUtils.isCurrentUserAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        stageResultService.deleteResult(id);
        return ResponseEntity.noContent().build();
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
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        stageResultService.updateElapsedTimesForEvent(eventId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/penalizacion/{id}")
    public ResponseEntity<StageResult> aplicarPenalizacion(
            @PathVariable Long id,
            @RequestParam(required = false) String penaltyWaypoint,
            @RequestParam(required = false) String penaltySpeed,
            @RequestParam(required = false) String discountClaim) {
        if (!authUtils.isCurrentUserAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Convertir strings a Duration, usando Duration.ZERO para valores vacíos o
        // "PT0S"
        Duration penaltyWaypointDuration = parseDuration(penaltyWaypoint);
        Duration penaltySpeedDuration = parseDuration(penaltySpeed);
        Duration discountClaimDuration = parseDuration(discountClaim);

        return ResponseEntity
                .ok(stageResultService.aplicarPenalizacion(id, penaltyWaypointDuration, penaltySpeedDuration,
                        discountClaimDuration));
    }

    private Duration parseDuration(String durationString) {
        if (durationString == null || durationString.trim().isEmpty() || "PT0S".equals(durationString)) {
            return Duration.ZERO;
        }
        try {
            return Duration.parse(durationString);
        } catch (Exception e) {
            return Duration.ZERO;
        }
    }

    @GetMapping("/by-event/{eventId}")
    public ResponseEntity<List<StageResult>> getResultsByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(stageResultService.getResultsByEvent(eventId));
    }
}