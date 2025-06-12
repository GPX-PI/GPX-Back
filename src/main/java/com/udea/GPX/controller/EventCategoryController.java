package com.udea.gpx.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.udea.gpx.model.EventCategory;
import com.udea.gpx.model.User;
import com.udea.gpx.service.EventCategoryService;
import com.udea.gpx.util.AuthUtils;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/event-categories")
public class EventCategoryController {

    private final EventCategoryService eventCategoryService;
    private final AuthUtils authUtils;

    public EventCategoryController(EventCategoryService eventCategoryService, AuthUtils authUtils) {
        this.eventCategoryService = eventCategoryService;
        this.authUtils = authUtils;
    }

    @GetMapping
    public ResponseEntity<List<EventCategory>> getAllEventCategories() {
        User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!authUser.isAdmin()) {
            return ResponseEntity.status(403).body(null);
        }
        return ResponseEntity.ok(eventCategoryService.getAll());
    }

    @PostMapping
    public ResponseEntity<EventCategory> createEventCategory(@Valid @RequestBody EventCategory eventCategory) {
        if (!authUtils.isCurrentUserAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        EventCategory savedEventCategory = eventCategoryService.save(eventCategory);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEventCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEventCategory(@PathVariable Long id) {
        User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!authUser.isAdmin()) {
            return ResponseEntity.status(403).build();
        }
        eventCategoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventCategory> getEventCategoryById(@PathVariable("id") Long id) {
        User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!authUser.isAdmin()) {
            return ResponseEntity.status(403).body(null);
        }
        EventCategory eventCategory = eventCategoryService.getById(id);
        return eventCategory != null ? ResponseEntity.ok(eventCategory) : ResponseEntity.notFound().build();
    }

    @GetMapping("/byevent/{eventId}")
    public ResponseEntity<List<EventCategory>> getCategoriesByEventId(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventCategoryService.getByEventId(eventId));
    }
}