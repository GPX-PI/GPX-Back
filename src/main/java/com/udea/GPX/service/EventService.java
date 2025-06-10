package com.udea.GPX.service;

import com.udea.GPX.model.Event;
import com.udea.GPX.model.EventCategory;
import com.udea.GPX.repository.IEventCategoryRepository;
import com.udea.GPX.repository.IEventRepository;
import com.udea.GPX.util.BusinessRuleValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
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
import java.util.stream.Collectors;

@Service
public class EventService {

    private static final Logger logger = LoggerFactory.getLogger(EventService.class);

    @Autowired
    private IEventRepository eventRepository;

    @Autowired
    private IEventCategoryRepository eventCategoryRepository;

    @Autowired
    private BusinessRuleValidator businessRuleValidator;

    // Directorio para almacenar imágenes de eventos
    private final String EVENT_UPLOAD_DIR = "uploads/events/";

    @Cacheable("events")
    public List<Event> getAllEvents() {
        return eventRepository.findAll()
                .stream()
                .sorted((e1, e2) -> e1.getStartDate().compareTo(e2.getStartDate()))
                .collect(Collectors.toList());
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
                .collect(Collectors.toList());
    }

    @Cacheable("pastEvents")
    public List<Event> getPastEvents() {
        LocalDate today = LocalDate.now();
        return eventRepository.findByEndDateBefore(today)
                .stream()
                .sorted((e1, e2) -> e2.getStartDate().compareTo(e1.getStartDate())) // Orden descendente para eventos
                                                                                    // pasados
                .collect(Collectors.toList());
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
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

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
                .collect(Collectors.toList());
    }

    // Métodos para manejo de imágenes

    public Event updateEventPicture(Long id, MultipartFile eventPhoto) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        try {
            // Eliminar imagen anterior si existe
            deleteOldFile(event.getPicture());

            // Guardar nueva imagen
            String newPicturePath = saveFile(eventPhoto, "event");
            event.setPicture(newPicturePath);

            return eventRepository.save(event);
        } catch (Exception e) {
            throw new RuntimeException("Error al subir la imagen del evento: " + e.getMessage());
        }
    }

    public Event updateEventPictureUrl(Long id, String pictureUrl) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        // Eliminar archivo anterior si no es una URL externa
        deleteOldFile(event.getPicture());

        // Actualizar con la nueva URL (puede ser externa o null para eliminar)
        event.setPicture(pictureUrl != null && !pictureUrl.trim().isEmpty() ? pictureUrl : null);

        return eventRepository.save(event);
    }

    public Event removeEventPicture(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

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

    private String saveFile(MultipartFile file, String fileType) throws Exception {
        if (file.isEmpty()) {
            throw new Exception("El archivo está vacío");
        }

        // Verificar tipo de archivo
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new Exception("Solo se permiten archivos de imagen");
        }

        // Verificar tamaño (máximo 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new Exception("El archivo no puede ser mayor a 5MB");
        }

        // Crear directorio si no existe
        File uploadDir = new File(EVENT_UPLOAD_DIR);
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
        Path filePath = Paths.get(EVENT_UPLOAD_DIR + uniqueFilename);
        Files.write(filePath, file.getBytes());

        return filePath.toString();
    }
}
