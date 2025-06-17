package com.udea.gpx.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.udea.gpx.model.Event;
import com.udea.gpx.model.EventCategory;
import com.udea.gpx.repository.IEventCategoryRepository;
import com.udea.gpx.repository.IEventRepository;
import com.udea.gpx.util.BusinessRuleValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Dedicated exceptions for EventService
 */
class EventServiceException extends Exception {
    public EventServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public EventServiceException(String message) {
        super(message);
    }
}

class InvalidFileTypeException extends EventServiceException {
    public InvalidFileTypeException(String message) {
        super(message);
    }
}

class FileSizeExceededException extends EventServiceException {
    public FileSizeExceededException(String message) {
        super(message);
    }
}

class ImageUploadException extends EventServiceException {
    public ImageUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}

@Service
public class EventService {

    private static final Logger logger = LoggerFactory.getLogger(EventService.class);

    // String constants to eliminate duplicated literals
    private static final String EVENT_NOT_FOUND_MSG = "Evento no encontrado";

    private final IEventRepository eventRepository;
    private final IEventCategoryRepository eventCategoryRepository;
    private final BusinessRuleValidator businessRuleValidator;

    // Constructor injection (no @Autowired needed)
    public EventService(
            IEventRepository eventRepository,
            IEventCategoryRepository eventCategoryRepository,
            BusinessRuleValidator businessRuleValidator) {
        this.eventRepository = eventRepository;
        this.eventCategoryRepository = eventCategoryRepository;
        this.businessRuleValidator = businessRuleValidator;
    }

    @Cacheable("events")
    public List<Event> getAllEvents() {
        return eventRepository.findAll()
                .stream()
                .sorted((e1, e2) -> e1.getStartDate().compareTo(e2.getStartDate()))
                .toList();
    }

    public Page<Event> getAllEvents(Pageable pageable) {
        return eventRepository.findAll(pageable);
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    @Cacheable("currentEvents")
    public List<Event> getCurrentEvents() {
        LocalDate today = LocalDate.now();
        return eventRepository.findByEndDateAfterOrEndDateEquals(today, today)
                .stream()
                .sorted((e1, e2) -> e1.getStartDate().compareTo(e2.getStartDate()))
                .toList();
    }

    @Cacheable("pastEvents")
    public List<Event> getPastEvents() {
        LocalDate today = LocalDate.now();
        return eventRepository.findByEndDateBefore(today)
                .stream()
                .sorted((e1, e2) -> e2.getStartDate().compareTo(e1.getStartDate())) // Orden descendente para eventos
                                                                                    // pasados
                .toList();
    }

    @CacheEvict(value = { "events", "currentEvents", "pastEvents" }, allEntries = true)
    public Event createEvent(Event event) {
        // Validar reglas de negocio completas
        businessRuleValidator.validateCompleteEvent(event);
        return eventRepository.save(event);
    }

    @CacheEvict(value = { "events", "currentEvents", "pastEvents" }, allEntries = true)
    public Event updateEvent(Long id, Event eventDetails) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(EVENT_NOT_FOUND_MSG));

        event.setName(eventDetails.getName());
        event.setLocation(eventDetails.getLocation());
        event.setDetails(eventDetails.getDetails());
        event.setStartDate(eventDetails.getStartDate());
        event.setEndDate(eventDetails.getEndDate());
        if (eventDetails.getPicture() != null) {
            event.setPicture(eventDetails.getPicture());
        }

        // Validar reglas de negocio completas antes de guardar
        businessRuleValidator.validateCompleteEvent(event);

        return eventRepository.save(event);
    }

    @CacheEvict(value = { "events", "currentEvents", "pastEvents", "eventCategories" }, allEntries = true)
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    @Cacheable(value = "eventCategories", key = "#eventId")
    public List<EventCategory> getCategoriesByEventId(Long eventId) {
        return eventCategoryRepository.findAll()
                .stream()
                .filter(ec -> ec.getEvent().getId().equals(eventId))
                .toList();
    }

    // ========== MÉTODOS PARA GESTIÓN DE URLs DE IMÁGENES ==========

    public Event updateEventPictureUrl(Long id, String pictureUrl) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(EVENT_NOT_FOUND_MSG)); // Actualizar con la nueva URL (puede ser
                                                                               // externa o null para eliminar)
        event.setPicture(pictureUrl != null && !pictureUrl.trim().isEmpty() ? pictureUrl : null);

        // NOSONAR - S5145: Log seguro - solo muestra longitud, no la URL completa
        // Log seguro - no exponer la URL completa
        logger.debug("Actualizando URL de imagen del evento {} (longitud: {} caracteres)",
                id, pictureUrl != null ? pictureUrl.length() : 0);
        return eventRepository.save(event);
    }

    public Event removeEventPicture(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(EVENT_NOT_FOUND_MSG));

        event.setPicture(null);
        logger.debug("Removiendo imagen del evento {}", id);
        return eventRepository.save(event);
    }
}
