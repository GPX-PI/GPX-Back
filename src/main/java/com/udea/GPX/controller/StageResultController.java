package com.udea.GPX.controller;

import com.udea.GPX.dto.ClasificacionCompletaDTO;
import com.udea.GPX.model.StageResult;
import com.udea.GPX.service.EventService;
import com.udea.GPX.service.StageResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/api/stageresults")
public class StageResultController {

    @Autowired
    private StageResultService stageResultService;

    @Autowired
    private EventService eventService;

    @PostMapping
    public ResponseEntity<StageResult> saveResult(@RequestBody StageResult result) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(stageResultService.saveResult(result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StageResult> updateResult(@PathVariable Long id, @RequestBody StageResult result) {
        try {
            return ResponseEntity.ok(stageResultService.updateResult(id, result));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResult(@PathVariable Long id) {
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

    @GetMapping("/bystagerange")
    public ResponseEntity<List<StageResult>> getResultsByStageRange(
            @RequestParam Long eventId,
            @RequestParam int stageStart,
            @RequestParam int stageEnd) {

        return eventService.getEventById(eventId)
                .map(event -> ResponseEntity.ok(stageResultService.getResultsByStageRange(event, stageStart, stageEnd)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/update-elapsed-times/{eventId}")
    public ResponseEntity<Void> updateElapsedTimesForEvent(@PathVariable Long eventId) {
        stageResultService.updateElapsedTimesForEvent(eventId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/penalizacion/{id}")
    public ResponseEntity<StageResult> aplicarPenalizacion(
            @PathVariable Long id,
            @RequestParam(required = false) Duration penaltyWaypoint,
            @RequestParam(required = false) Duration penaltySpeed,
            @RequestParam(required = false) Duration discountClaim) {
        return ResponseEntity
                .ok(stageResultService.aplicarPenalizacion(id, penaltyWaypoint, penaltySpeed, discountClaim));
    }
}