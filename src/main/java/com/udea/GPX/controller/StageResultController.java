package com.udea.GPX.controller;

import com.udea.GPX.dto.ClasificacionCompletaDTO;
import com.udea.GPX.model.StageResult;
import com.udea.GPX.model.User;
import com.udea.GPX.service.StageResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.time.Duration;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/stageresults")
public class StageResultController {

    @Autowired
    private StageResultService stageResultService;

    @SuppressWarnings("unused")
    @Autowired
    private HttpServletRequest request;

    @PostMapping
    public ResponseEntity<StageResult> saveResult(@RequestBody StageResult result) {
        User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!authUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(stageResultService.saveResult(result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StageResult> updateResult(@PathVariable Long id, @RequestBody StageResult result) {
        User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!authUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            return ResponseEntity.ok(stageResultService.updateResult(id, result));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResult(@PathVariable Long id) {
        User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!authUser.isAdmin()) {
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
        User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!authUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        stageResultService.updateElapsedTimesForEvent(eventId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/penalizacion/{id}")
    public ResponseEntity<StageResult> aplicarPenalizacion(
            @PathVariable Long id,
            @RequestParam(required = false) Duration penaltyWaypoint,
            @RequestParam(required = false) Duration penaltySpeed,
            @RequestParam(required = false) Duration discountClaim) {
        User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!authUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity
                .ok(stageResultService.aplicarPenalizacion(id, penaltyWaypoint, penaltySpeed, discountClaim));
    }
}