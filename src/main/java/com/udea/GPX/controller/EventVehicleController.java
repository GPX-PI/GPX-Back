package com.udea.GPX.controller;

import com.udea.GPX.model.EventVehicle;
import com.udea.GPX.model.User;
import com.udea.GPX.service.EventVehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@RestController
@RequestMapping("/api/event-vehicles")
public class EventVehicleController {

    @Autowired
    private EventVehicleService eventVehicleService;

    @SuppressWarnings("unused")
    @Autowired
    private HttpServletRequest request;

    @GetMapping
    public ResponseEntity<List<EventVehicle>> getAllEventVehicles() {
        User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!authUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        return ResponseEntity.ok(eventVehicleService.getAllEventVehicles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventVehicle> getEventVehicleById(@PathVariable Long id) {
        User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        EventVehicle ev = eventVehicleService.getEventVehicleById(id).orElse(null);
        if (ev == null) {
            return ResponseEntity.notFound().build();
        }
        if (!authUser.isAdmin() && (ev.getVehicleId() == null || ev.getVehicleId().getUser() == null
                || !ev.getVehicleId().getUser().getId().equals(authUser.getId()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(ev);
    }

    @GetMapping("/byevent/{eventId}")
    public ResponseEntity<List<EventVehicle>> getVehiclesByEventId(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventVehicleService.getVehiclesByEventId(eventId));
    }

    @PostMapping
    public ResponseEntity<EventVehicle> createEventVehicle(@RequestBody EventVehicle eventVehicle) {
        User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!authUser.isAdmin() && (eventVehicle.getVehicleId() == null || eventVehicle.getVehicleId().getUser() == null
                || !eventVehicle.getVehicleId().getUser().getId().equals(authUser.getId()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventVehicleService.createEventVehicle(eventVehicle));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEventVehicle(@PathVariable Long id) {
        User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        EventVehicle ev = eventVehicleService.getEventVehicleById(id).orElse(null);
        if (ev == null) {
            return ResponseEntity.notFound().build();
        }
        if (!authUser.isAdmin() && (ev.getVehicleId() == null || ev.getVehicleId().getUser() == null
                || !ev.getVehicleId().getUser().getId().equals(authUser.getId()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        eventVehicleService.deleteEventVehicle(id);
        return ResponseEntity.noContent().build();
    }
}