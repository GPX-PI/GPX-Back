package com.udea.GPX.service;

import com.udea.GPX.model.Event;
import com.udea.GPX.model.EventVehicle;
import com.udea.GPX.repository.IEventRepository;
import com.udea.GPX.repository.IEventVehicleRepository;
import com.udea.GPX.util.BusinessRuleValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class EventVehicleService {

    private static final Logger logger = LoggerFactory.getLogger(EventVehicleService.class);

    @Autowired
    private IEventVehicleRepository eventVehicleRepository;

    @Autowired
    private IEventRepository eventRepository;

    @Autowired
    private BusinessRuleValidator businessRuleValidator;

    public List<EventVehicle> getAllEventVehicles() {
        return eventVehicleRepository.findAll();
    }

    public Optional<EventVehicle> getEventVehicleById(Long id) {
        return eventVehicleRepository.findById(id);
    }

    public List<EventVehicle> getVehiclesByEventId(Long eventId) {
        return eventVehicleRepository.findByEventId(eventId);
    }

    @Transactional
    public EventVehicle createEventVehicle(EventVehicle eventVehicle) {
        logger.debug("🔍 EventVehicleService.createEventVehicle - Registrando vehículo {} en evento {}",
                eventVehicle.getVehicleId().getId(), eventVehicle.getEvent().getId());

        // Obtener el evento para validaciones
        Event event = eventRepository.findById(eventVehicle.getEvent().getId())
                .orElseThrow(() -> new IllegalArgumentException("El evento especificado no existe"));

        // Validar reglas de negocio para el registro
        businessRuleValidator.validateVehicleRegistration(event, eventVehicle.getVehicleId().getId());

        // Obtener registros actuales para validar capacidad
        List<EventVehicle> currentRegistrations = eventVehicleRepository.findByEventId(event.getId());
        businessRuleValidator.validateEventCapacity(event, currentRegistrations);

        // Verificar que el vehículo no esté ya registrado en este evento
        boolean alreadyRegistered = currentRegistrations.stream()
                .anyMatch(ev -> ev.getVehicleId().getId().equals(eventVehicle.getVehicleId().getId()));

        if (alreadyRegistered) {
            throw new IllegalArgumentException("El vehículo ya está registrado en este evento");
        }

        EventVehicle savedEventVehicle = eventVehicleRepository.save(eventVehicle);
        logger.info("✅ EventVehicleService.createEventVehicle - Vehículo {} registrado exitosamente en evento {}",
                eventVehicle.getVehicleId().getId(), event.getId());

        return savedEventVehicle;
    }

    public void deleteEventVehicle(Long id) {
        eventVehicleRepository.deleteById(id);
    }
}
