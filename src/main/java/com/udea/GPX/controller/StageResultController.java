package com.udea.GPX.controller;

import com.udea.GPX.dto.ClasificacionDTO;
import com.udea.GPX.model.Category;
import com.udea.GPX.model.Event;
import com.udea.GPX.model.StageResult;
import com.udea.GPX.service.CategoryService;
import com.udea.GPX.service.EventService;
import com.udea.GPX.service.StageResultService;
import com.udea.GPX.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/stageresults")
public class StageResultController {

    @Autowired
    private StageResultService stageResultService;

    @Autowired
    private EventService eventService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private VehicleService vehicleService;

    @GetMapping
    public ResponseEntity<List<StageResult>> getAllResults() {
        return ResponseEntity.ok(stageResultService.getAllResults());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StageResult> getResultById(@PathVariable Long id) {
        return stageResultService.getResultById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

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

    @GetMapping("/bycategory")
    public ResponseEntity<List<StageResult>> getResultsByCategoryAndStages(
            @RequestParam Long categoryId,
            @RequestParam int stageStart,
            @RequestParam int stageEnd) {
        return ResponseEntity.ok(stageResultService.getResultsByCategoryAndStages(categoryId, stageStart, stageEnd));
    }

    @GetMapping("/tiempo-total")
    public ResponseEntity<Duration> calcularTiempoTotal(
            @RequestParam Long vehicleId,
            @RequestParam Long eventId) {

        return vehicleService.getVehicleById(vehicleId)
                .flatMap(vehicle ->
                        eventService.getEventById(eventId)
                                .map(event -> ResponseEntity.ok(stageResultService.calcularTiempoTotal(vehicle, event)))
                )
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/clasificacion")
    public ResponseEntity<List<ClasificacionDTO>> getClasificacionPorCategoria(
            @RequestParam Long eventId,
            @RequestParam Long categoryId) {

        Optional<Event> eventOptional = eventService.getEventById(eventId);
        if (eventOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Category category = categoryService.getCategoryById(categoryId);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(stageResultService.getClasificacionPorCategoria(eventOptional.get(), category));
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
}