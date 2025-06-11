package com.udea.gpx.service;

import com.udea.gpx.util.TestDataBuilder;
import com.udea.gpx.model.Event;
import com.udea.gpx.model.EventCategory;
import com.udea.gpx.repository.IEventCategoryRepository;
import com.udea.gpx.repository.IEventRepository;
import com.udea.gpx.util.BusinessRuleValidator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("EventService Tests")
class EventServiceTest {

  @Mock
  private IEventRepository eventRepository;

  @Mock
  private IEventCategoryRepository eventCategoryRepository;

  @Mock
  private BusinessRuleValidator businessRuleValidator;

  @InjectMocks
  private EventService eventService;

  private Event testEvent;
  private Event pastEvent;
  private Event futureEvent;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    testEvent = TestDataBuilder.buildEvent(1L, "Test Event");
    testEvent.setStartDate(LocalDate.now().plusDays(10));
    testEvent.setEndDate(LocalDate.now().plusDays(12));

    pastEvent = TestDataBuilder.buildEvent(2L, "Past Event");
    pastEvent.setStartDate(LocalDate.now().minusDays(10));
    pastEvent.setEndDate(LocalDate.now().minusDays(8));

    futureEvent = TestDataBuilder.buildEvent(3L, "Future Event");
    futureEvent.setStartDate(LocalDate.now().plusDays(20));
    futureEvent.setEndDate(LocalDate.now().plusDays(22));
  }

  // ========== GET ALL EVENTS TESTS ==========

  @Test
  @DisplayName("getAllEvents - Debe retornar todos los eventos ordenados por fecha")
  void getAllEvents_shouldReturnAllEventsSortedByDate() {
    // Given
    List<Event> events = List.of(futureEvent, testEvent, pastEvent);
    when(eventRepository.findAll()).thenReturn(events);

    // When
    List<Event> result = eventService.getAllEvents();

    // Then
    assertThat(result).hasSize(3);
    assertThat(result.get(0)).isEqualTo(pastEvent); // Primero el más cercano en el pasado
    assertThat(result.get(1)).isEqualTo(testEvent);
    assertThat(result.get(2)).isEqualTo(futureEvent);
    verify(eventRepository).findAll();
  }

  @Test
  @DisplayName("getAllEvents con paginación - Debe retornar página de eventos")
  void getAllEventsWithPagination_shouldReturnPageOfEvents() {
    // Given
    Pageable pageable = PageRequest.of(0, 10);
    Page<Event> eventPage = new PageImpl<>(List.of(testEvent, futureEvent), pageable, 2);
    when(eventRepository.findAll(pageable)).thenReturn(eventPage);

    // When
    Page<Event> result = eventService.getAllEvents(pageable);

    // Then
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getTotalElements()).isEqualTo(2);
    verify(eventRepository).findAll(pageable);
  }

  // ========== GET EVENT BY ID TESTS ==========

  @Test
  @DisplayName("getEventById - Debe retornar evento existente")
  void getEventById_shouldReturnExistingEvent() {
    // Given
    when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

    // When
    Optional<Event> result = eventService.getEventById(1L);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(testEvent);
    verify(eventRepository).findById(1L);
  }

  @Test
  @DisplayName("getEventById - Debe retornar vacío para evento inexistente")
  void getEventById_shouldReturnEmptyForNonExistentEvent() {
    // Given
    when(eventRepository.findById(999L)).thenReturn(Optional.empty());

    // When
    Optional<Event> result = eventService.getEventById(999L);

    // Then
    assertThat(result).isEmpty();
    verify(eventRepository).findById(999L);
  }

  // ========== CURRENT EVENTS TESTS ==========

  @Test
  @DisplayName("getCurrentEvents - Debe retornar solo eventos actuales/futuros")
  void getCurrentEvents_shouldReturnOnlyCurrentEvents() {
    // Given
    LocalDate today = LocalDate.now();
    List<Event> currentEvents = List.of(testEvent, futureEvent);
    when(eventRepository.findByEndDateAfterOrEndDateEquals(today, today))
        .thenReturn(currentEvents);

    // When
    List<Event> result = eventService.getCurrentEvents();

    // Then
    assertThat(result).hasSize(2);
    assertThat(result).containsExactly(testEvent, futureEvent);
    verify(eventRepository).findByEndDateAfterOrEndDateEquals(today, today);
  }

  // ========== PAST EVENTS TESTS ==========

  @Test
  @DisplayName("getPastEvents - Debe retornar solo eventos pasados en orden descendente")
  void getPastEvents_shouldReturnOnlyPastEventsInDescendingOrder() {
    // Given
    LocalDate today = LocalDate.now();
    Event olderEvent = TestDataBuilder.buildEvent(4L, "Older Event");
    olderEvent.setStartDate(LocalDate.now().minusDays(20));
    olderEvent.setEndDate(LocalDate.now().minusDays(18));

    List<Event> pastEvents = List.of(pastEvent, olderEvent);
    when(eventRepository.findByEndDateBefore(today)).thenReturn(pastEvents);

    // When
    List<Event> result = eventService.getPastEvents();

    // Then
    assertThat(result).hasSize(2);
    assertThat(result.get(0)).isEqualTo(pastEvent); // Más reciente primero
    assertThat(result.get(1)).isEqualTo(olderEvent);
    verify(eventRepository).findByEndDateBefore(today);
  }

  // ========== CREATE EVENT TESTS ==========

  @Test
  @DisplayName("createEvent - Debe crear evento válido")
  void createEvent_shouldCreateValidEvent() {
    // Given
    Event newEvent = TestDataBuilder.buildEvent(5L, "New Event");
    doNothing().when(businessRuleValidator).validateCompleteEvent(newEvent);
    when(eventRepository.save(newEvent)).thenReturn(newEvent);

    // When
    Event result = eventService.createEvent(newEvent);

    // Then
    assertThat(result).isEqualTo(newEvent);
    verify(businessRuleValidator).validateCompleteEvent(newEvent);
    verify(eventRepository).save(newEvent);
  }

  @Test
  @DisplayName("createEvent - Debe lanzar excepción si validación falla")
  void createEvent_shouldThrowExceptionIfValidationFails() {
    // Given
    Event invalidEvent = TestDataBuilder.buildEvent(6L, "Invalid Event");
    doThrow(new IllegalArgumentException("Evento inválido"))
        .when(businessRuleValidator).validateCompleteEvent(invalidEvent);

    // When & Then
    assertThatThrownBy(() -> eventService.createEvent(invalidEvent))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Evento inválido");

    verify(businessRuleValidator).validateCompleteEvent(invalidEvent);
    verify(eventRepository, never()).save(any(Event.class));
  }

  // ========== UPDATE EVENT TESTS ==========

  @Test
  @DisplayName("updateEvent - Debe actualizar evento existente")
  void updateEvent_shouldUpdateExistingEvent() {
    // Given
    Event updatedData = TestDataBuilder.buildEvent(7L, "Updated Event");
    updatedData.setLocation("Updated Location");
    updatedData.setDetails("Updated Details");
    updatedData.setStartDate(LocalDate.now().plusDays(15));
    updatedData.setEndDate(LocalDate.now().plusDays(17));

    when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
    doNothing().when(businessRuleValidator).validateCompleteEvent(testEvent);
    when(eventRepository.save(testEvent)).thenReturn(testEvent);

    // When
    Event result = eventService.updateEvent(1L, updatedData);

    // Then
    assertThat(result).isNotNull();
    verify(businessRuleValidator).validateCompleteEvent(testEvent);
    verify(eventRepository).save(testEvent);
  }

  @Test
  @DisplayName("updateEvent - Debe lanzar excepción si evento no existe")
  void updateEvent_shouldThrowExceptionIfEventNotFound() {
    // Given
    Event updatedData = TestDataBuilder.buildEvent(8L, "Updated Event");
    when(eventRepository.findById(999L)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> eventService.updateEvent(999L, updatedData))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Evento no encontrado");

    verify(eventRepository, never()).save(any(Event.class));
  }

  // ========== DELETE EVENT TESTS ==========

  @Test
  @DisplayName("deleteEvent - Debe eliminar evento existente")
  void deleteEvent_shouldDeleteExistingEvent() {
    // Given
    when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
    doNothing().when(eventRepository).deleteById(1L);

    // When
    eventService.deleteEvent(1L);

    // Then
    verify(eventRepository).findById(1L);
    verify(eventRepository).deleteById(1L);
  }

  @Test
  @DisplayName("deleteEvent - Debe eliminar evento aunque no exista imagen")
  void deleteEvent_shouldDeleteEventEvenIfNoImage() {
    // Given
    Event eventWithoutImage = TestDataBuilder.buildEvent(9L, "Event Without Image");
    eventWithoutImage.setPicture(null);
    when(eventRepository.findById(1L)).thenReturn(Optional.of(eventWithoutImage));
    doNothing().when(eventRepository).deleteById(1L);

    // When
    eventService.deleteEvent(1L);

    // Then
    verify(eventRepository).deleteById(1L);
  }

  // ========== GET CATEGORIES BY EVENT ID TESTS ==========

  @Test
  @DisplayName("getCategoriesByEventId - Debe retornar categorías del evento")
  void getCategoriesByEventId_shouldReturnEventCategories() {
    // Given
    EventCategory category1 = TestDataBuilder.buildEventCategory(1L, testEvent,
        TestDataBuilder.buildCategory(1L, "Category 1"));
    EventCategory category2 = TestDataBuilder.buildEventCategory(2L, testEvent,
        TestDataBuilder.buildCategory(2L, "Category 2"));

    List<EventCategory> allCategories = List.of(category1, category2);
    when(eventCategoryRepository.findAll()).thenReturn(allCategories);

    // When
    List<EventCategory> result = eventService.getCategoriesByEventId(1L);

    // Then
    assertThat(result).hasSize(2);
    assertThat(result).containsExactly(category1, category2);
    verify(eventCategoryRepository).findAll();
  }

  @Test
  @DisplayName("getCategoriesByEventId - Debe retornar lista vacía si no hay categorías")
  void getCategoriesByEventId_shouldReturnEmptyListIfNoCategories() {
    // Given
    when(eventCategoryRepository.findAll()).thenReturn(List.of());

    // When
    List<EventCategory> result = eventService.getCategoriesByEventId(1L);

    // Then
    assertThat(result).isEmpty();
    verify(eventCategoryRepository).findAll();
  }

  // ========== UPDATE EVENT PICTURE URL TESTS ==========

  @Test
  @DisplayName("updateEventPictureUrl - Debe actualizar URL de imagen")
  void updateEventPictureUrl_shouldUpdatePictureUrl() {
    // Given
    String newPictureUrl = "https://example.com/new-picture.jpg";
    when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
    when(eventRepository.save(testEvent)).thenReturn(testEvent);

    // When
    Event result = eventService.updateEventPictureUrl(1L, newPictureUrl);

    // Then
    assertThat(result).isNotNull();
    verify(eventRepository).save(testEvent);
  }

  @Test
  @DisplayName("updateEventPictureUrl - Debe lanzar excepción si evento no existe")
  void updateEventPictureUrl_shouldThrowExceptionIfEventNotFound() {
    // Given
    when(eventRepository.findById(999L)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> eventService.updateEventPictureUrl(999L, "url"))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Evento no encontrado");
  }

  @Test
  @DisplayName("updateEventPictureUrl - Debe establecer null para URL vacía")
  void updateEventPictureUrl_shouldSetNullForEmptyUrl() {
    // Given
    when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
    when(eventRepository.save(testEvent)).thenReturn(testEvent);

    // When
    Event result = eventService.updateEventPictureUrl(1L, "   ");

    // Then
    assertThat(result).isNotNull();
    verify(eventRepository).save(testEvent);
  }

  // ========== REMOVE EVENT PICTURE TESTS ==========

  @Test
  @DisplayName("removeEventPicture - Debe eliminar imagen del evento")
  void removeEventPicture_shouldRemoveEventPicture() {
    // Given
    testEvent.setPicture("some-picture.jpg");
    when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
    when(eventRepository.save(testEvent)).thenReturn(testEvent);

    // When
    Event result = eventService.removeEventPicture(1L);

    // Then
    assertThat(result).isNotNull();
    verify(eventRepository).save(testEvent);
  }

  @Test
  @DisplayName("removeEventPicture - Debe lanzar excepción si evento no existe")
  void removeEventPicture_shouldThrowExceptionIfEventNotFound() {
    // Given
    when(eventRepository.findById(999L)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> eventService.removeEventPicture(999L))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Evento no encontrado");
  }

  // ========== UPDATE EVENT PICTURE TESTS ==========

  @Test
  @DisplayName("updateEventPicture - Debe lanzar excepción si evento no existe")
  void updateEventPicture_shouldThrowExceptionIfEventNotFound() {
    // Given
    MultipartFile mockFile = mock(MultipartFile.class);
    when(eventRepository.findById(999L)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> eventService.updateEventPicture(999L, mockFile))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Evento no encontrado");
  }

  @Test
  @DisplayName("updateEventPicture - Debe lanzar excepción por error en guardado")
  void updateEventPicture_shouldThrowExceptionOnSaveError() {
    // Given
    MultipartFile mockFile = mock(MultipartFile.class);
    when(mockFile.isEmpty()).thenReturn(true);
    when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent)); // When & Then
    assertThatThrownBy(() -> eventService.updateEventPicture(1L, mockFile))
        .isInstanceOf(ImageUploadException.class)
        .hasMessageContaining("Error al subir la imagen del evento");
  }
}