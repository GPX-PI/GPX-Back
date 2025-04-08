package com.udea.GPX.service;

import com.udea.GPX.model.EventVehicle;
import com.udea.GPX.repository.IEventVehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventVehicleService {

    @Autowired
    private IEventVehicleRepository eventVehicleRepository;

    public List<EventVehicle> getAllEventVehicles() {
        return eventVehicleRepository.findAll();
    }

    public Optional<EventVehicle> getEventVehicleById(Long id) {
        return eventVehicleRepository.findById(id);
    }

    public List<EventVehicle> getVehiclesByEventId(Long eventId) {
        return eventVehicleRepository.findByEventId(eventId);
    }

    public EventVehicle createEventVehicle(EventVehicle eventVehicle) {
        return eventVehicleRepository.save(eventVehicle);
    }

    public void deleteEventVehicle(Long id) {
        eventVehicleRepository.deleteById(id);
    }
}
