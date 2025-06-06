package com.udea.GPX.controller;

import com.udea.GPX.model.EventCategory;
import com.udea.GPX.model.User;
import com.udea.GPX.service.EventCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@RestController
@RequestMapping("/api/event-categories")
public class EventCategoryController {

    @Autowired
    private EventCategoryService eventCategoryService;

    @SuppressWarnings("unused")
    @Autowired
    private HttpServletRequest request;

    @GetMapping
    public ResponseEntity<List<EventCategory>> getAllEventCategories() {
        User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!authUser.isAdmin()) {
            return ResponseEntity.status(403).body(null);
        }
        return ResponseEntity.ok(eventCategoryService.getAll());
    }

    @PostMapping
    public ResponseEntity<EventCategory> createEventCategory(@RequestBody EventCategory eventCategory) {
        User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!authUser.isAdmin()) {
            return ResponseEntity.status(403).body(null);
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventCategoryService.save(eventCategory));
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
    public ResponseEntity<EventCategory> getEventCategoryById(@PathVariable Long categoryId) {
        User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!authUser.isAdmin()) {
            return ResponseEntity.status(403).body(null);
        }
        EventCategory eventCategory = eventCategoryService.getById(categoryId);
        return eventCategory != null ? ResponseEntity.ok(eventCategory) : ResponseEntity.notFound().build();
    }

    @GetMapping("/byevent/{eventId}")
    public ResponseEntity<List<EventCategory>> getCategoriesByEventId(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventCategoryService.getByEventId(eventId));
    }
}