package com.udea.GPX.controller;

import com.udea.GPX.model.EventVehicle;
import com.udea.GPX.service.EventVehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/event-vehicles")
public class EventVehicleController {

    @Autowired
    private EventVehicleService eventVehicleService;

    @GetMapping
    public ResponseEntity<List<EventVehicle>> getAllEventVehicles() {
        return ResponseEntity.ok(eventVehicleService.getAllEventVehicles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventVehicle> getEventVehicleById(@PathVariable Long id) {
        return eventVehicleService.getEventVehicleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/byevent/{eventId}")
    public ResponseEntity<List<EventVehicle>> getVehiclesByEventId(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventVehicleService.getVehiclesByEventId(eventId));
    }

    @PostMapping
    public ResponseEntity<EventVehicle> createEventVehicle(@RequestBody EventVehicle eventVehicle) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventVehicleService.createEventVehicle(eventVehicle));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEventVehicle(@PathVariable Long id) {
        eventVehicleService.deleteEventVehicle(id);
        return ResponseEntity.noContent().build();
    }
}