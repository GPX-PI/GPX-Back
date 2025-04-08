package com.udea.GPX.controller;

import com.udea.GPX.model.EventCategory;
import com.udea.GPX.service.EventCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/event-categories")
public class EventCategoryController {

    @Autowired
    private EventCategoryService eventCategoryService;

    @GetMapping
    public ResponseEntity<List<EventCategory>> getAllEventCategories() {
        return ResponseEntity.ok(eventCategoryService.getAll());
    }

    @PostMapping
    public ResponseEntity<EventCategory> createEventCategory(@RequestBody EventCategory eventCategory) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventCategoryService.save(eventCategory));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEventCategory(@PathVariable Long id) {
        eventCategoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<EventCategory> getEventCategoryById(Long categoryId) {
        EventCategory eventCategory = eventCategoryService.getById(categoryId);
        return eventCategory != null ?
                ResponseEntity.ok(eventCategory) :
                ResponseEntity.notFound().build();
    }
}