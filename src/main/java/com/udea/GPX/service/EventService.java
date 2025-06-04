package com.udea.GPX.service;

import com.udea.GPX.model.Event;
import com.udea.GPX.model.EventCategory;
import com.udea.GPX.repository.IEventCategoryRepository;
import com.udea.GPX.repository.IEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private IEventRepository eventRepository;

    @Autowired
    private IEventCategoryRepository eventCategoryRepository;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public List<Event> getCurrentEvents() {
        LocalDate today = LocalDate.now();
        return eventRepository.findByEndDateAfterOrEndDateEquals(today, today);
    }

    public List<Event> getPastEvents() {
        LocalDate today = LocalDate.now();
        return eventRepository.findByEndDateBefore(today);
    }

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    public Event updateEvent(Long id, Event eventDetails) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        event.setName(eventDetails.getName());
        event.setLocation(eventDetails.getLocation());
        event.setDetails(eventDetails.getDetails());
        event.setStartDate(eventDetails.getStartDate());
        event.setEndDate(eventDetails.getEndDate());
        event.setPicture(eventDetails.getPicture());

        return eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public List<EventCategory> getCategoriesByEventId(Long eventId) {
        return eventCategoryRepository.findAll()
                .stream()
                .filter(ec -> ec.getEvent().getId().equals(eventId))
                .collect(Collectors.toList());
    }

}
