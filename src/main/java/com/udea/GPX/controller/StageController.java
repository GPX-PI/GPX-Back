package com.udea.GPX.controller;

import com.udea.GPX.model.Stage;
import com.udea.GPX.model.User;
import com.udea.GPX.service.StageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/stages")
public class StageController {

    @Autowired
    private StageService stageService;

    @SuppressWarnings("unused")
    @Autowired
    private HttpServletRequest request;

    @GetMapping
    public ResponseEntity<List<Stage>> getAllStages() {
        return ResponseEntity.ok(stageService.getAllStages());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Stage> getStageById(@PathVariable Long id) {
        Optional<Stage> stage = stageService.getStageById(id);
        return stage.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/byevent/{eventId}")
    public ResponseEntity<List<Stage>> getStagesByEventId(@PathVariable Long eventId) {
        return ResponseEntity.ok(stageService.getStagesByEventId(eventId));
    }

    @PostMapping
    public ResponseEntity<Stage> createStage(@RequestBody Stage stage) {
        User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!authUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(stageService.createStage(stage));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Stage> updateStage(@PathVariable Long id, @RequestBody Stage stage) {
        User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!authUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            Stage updatedStage = stageService.updateStage(id, stage);
            return ResponseEntity.ok(updatedStage);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStage(@PathVariable Long id) {
        User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!authUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        stageService.deleteStage(id);
        return ResponseEntity.noContent().build();
    }
}