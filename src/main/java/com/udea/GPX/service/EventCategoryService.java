package com.udea.GPX.service;

import com.udea.GPX.model.EventCategory;
import com.udea.GPX.repository.IEventCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventCategoryService {

    @Autowired
    private IEventCategoryRepository eventCategoryRepository;

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
}
