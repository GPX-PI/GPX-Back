package com.udea.gpx.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.udea.gpx.model.Event;
import com.udea.gpx.model.EventCategory;
import com.udea.gpx.repository.IEventCategoryRepository;
import com.udea.gpx.repository.IEventRepository;
import com.udea.gpx.util.BusinessRuleValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    private static final String IMAGE_UPLOAD_ERROR_MSG = "Error al subir la imagen del evento: ";
    private static final String ONLY_IMAGE_FILES_MSG = "Solo se permiten archivos de imagen";
    private static final String FILE_SIZE_LIMIT_MSG = "El archivo no puede ser mayor a 5MB";
    private static final String UPLOADS_EVENTS_DIR = "uploads/events/";

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
        // Antes de eliminar, eliminar la imagen si existe
        Optional<Event> eventOpt = eventRepository.findById(id);
        if (eventOpt.isPresent()) {
            Event event = eventOpt.get();
            deleteOldFile(event.getPicture());
        }
        eventRepository.deleteById(id);
    }

    @Cacheable(value = "eventCategories", key = "#eventId")
    public List<EventCategory> getCategoriesByEventId(Long eventId) {
        return eventCategoryRepository.findAll()
                .stream()
                .filter(ec -> ec.getEvent().getId().equals(eventId))
                .toList();
    }

    // Métodos para manejo de imágenes

    public Event updateEventPicture(Long id, MultipartFile eventPhoto) throws ImageUploadException {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(EVENT_NOT_FOUND_MSG));

        try {
            // Eliminar imagen anterior si existe
            deleteOldFile(event.getPicture());

            // Guardar nueva imagen
            String newPicturePath = saveFile(eventPhoto, "event");
            event.setPicture(newPicturePath);

            return eventRepository.save(event);
        } catch (Exception e) {
            logger.error("Error uploading event image for event ID {}: {}", id, e.getMessage(), e);
            throw new ImageUploadException(IMAGE_UPLOAD_ERROR_MSG + e.getMessage(), e);
        }
    }

    public Event updateEventPictureUrl(Long id, String pictureUrl) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(EVENT_NOT_FOUND_MSG));

        // Eliminar archivo anterior si no es una URL externa
        deleteOldFile(event.getPicture());

        // Actualizar con la nueva URL (puede ser externa o null para eliminar)
        event.setPicture(pictureUrl != null && !pictureUrl.trim().isEmpty() ? pictureUrl : null);

        return eventRepository.save(event);
    }

    public Event removeEventPicture(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(EVENT_NOT_FOUND_MSG));

        // Eliminar archivo actual si existe
        deleteOldFile(event.getPicture());
        event.setPicture(null);

        return eventRepository.save(event);
    }

    // Métodos auxiliares para manejo de archivos

    private void deleteOldFile(String oldFilePath) {
        if (oldFilePath != null && !oldFilePath.trim().isEmpty() && !oldFilePath.startsWith("http")) {
            try {
                Path path = Paths.get(oldFilePath);
                Files.deleteIfExists(path);
                logger.debug("Archivo anterior eliminado: {}", oldFilePath);
            } catch (Exception e) {
                logger.error("Error al eliminar archivo anterior: {}", oldFilePath, e);
                // No lanzar excepción para no interrumpir el flujo principal
            }
        }
    }

    private String saveFile(MultipartFile file, String fileType) throws EventServiceException {
        if (file.isEmpty()) {
            throw new EventServiceException("El archivo está vacío");
        }

        // Verificar tipo de archivo
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new InvalidFileTypeException(ONLY_IMAGE_FILES_MSG);
        }

        // Verificar tamaño (máximo 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new FileSizeExceededException(FILE_SIZE_LIMIT_MSG);
        }

        // Crear directorio si no existe
        File uploadDir = new File(UPLOADS_EVENTS_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // Generar nombre único para el archivo
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFilename = fileType + "_" + UUID.randomUUID().toString() + fileExtension;

        // Guardar archivo
        Path filePath = Paths.get(UPLOADS_EVENTS_DIR + uniqueFilename);
        try {
            Files.write(filePath, file.getBytes());
        } catch (Exception e) {
            logger.error("Error writing file to disk: {}", filePath, e);
            throw new EventServiceException("Error saving file to disk: " + e.getMessage(), e);
        }

        return filePath.toString();
    }
}
