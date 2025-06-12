package com.udea.gpx.service;

import org.springframework.stereotype.Service;

import com.udea.gpx.model.EventCategory;
import com.udea.gpx.repository.IEventCategoryRepository;

import java.util.List;

@Service
public class EventCategoryService {

    private final IEventCategoryRepository eventCategoryRepository;

    public EventCategoryService(IEventCategoryRepository eventCategoryRepository) {
        this.eventCategoryRepository = eventCategoryRepository;
    }

    public List<EventCategory> getAll() {
        return eventCategoryRepository.findAll();
    }

    public EventCategory save(EventCategory ec) {
        return eventCategoryRepository.save(ec);
    }

    public void delete(Long id) {
        eventCategoryRepository.deleteById(id);
    }

    public Object getEventCategoryById(Long categoryId) {
        return eventCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("EventCategory not found"));
    }

    public EventCategory getById(Long categoryId) {
        return eventCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("EventCategory not found"));
    }

    public List<EventCategory> getByEventId(Long eventId) {
        return eventCategoryRepository.findByEventId(eventId);
    }
}
