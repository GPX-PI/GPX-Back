package com.udea.gpx.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.udea.gpx.model.Event;
import com.udea.gpx.model.EventVehicle;
import com.udea.gpx.repository.IEventRepository;
import com.udea.gpx.repository.IEventVehicleRepository;
import com.udea.gpx.util.BusinessRuleValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class EventVehicleService {

    private static final Logger logger = LoggerFactory.getLogger(EventVehicleService.class);

    private final IEventVehicleRepository eventVehicleRepository;
    private final IEventRepository eventRepository;
    private final BusinessRuleValidator businessRuleValidator;

    // Constructor injection (no @Autowired needed)
    public EventVehicleService(
            IEventVehicleRepository eventVehicleRepository,
            IEventRepository eventRepository,
            BusinessRuleValidator businessRuleValidator) {
        this.eventVehicleRepository = eventVehicleRepository;
        this.eventRepository = eventRepository;
        this.businessRuleValidator = businessRuleValidator;
    }

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
