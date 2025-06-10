package com.udea.GPX.service;

import com.udea.GPX.model.Event;
import com.udea.GPX.model.EventVehicle;
import com.udea.GPX.model.Vehicle;
import com.udea.GPX.model.User;
import com.udea.GPX.model.Category;
import com.udea.GPX.repository.IEventRepository;
import com.udea.GPX.repository.IEventVehicleRepository;
import com.udea.GPX.util.BusinessRuleValidator;
import com.udea.GPX.util.TestDataBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("EventVehicleService Tests")
class EventVehicleServiceTest {

  @Mock
  private IEventVehicleRepository eventVehicleRepository;

  @Mock
  private IEventRepository eventRepository;

  @Mock
  private BusinessRuleValidator businessRuleValidator;

  @InjectMocks
  private EventVehicleService eventVehicleService;

  private Event testEvent;
  private Vehicle testVehicle;
  private EventVehicle testEventVehicle;
  private User testUser;
  private Category testCategory;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    testUser = TestDataBuilder.buildUser(1L, "TestUser", false);
    testCategory = TestDataBuilder.buildCategory(1L, "Test Category");
    testEvent = TestDataBuilder.buildEvent(1L, "Test Event");
    testVehicle = TestDataBuilder.buildVehicle(1L, testUser, testCategory);

    testEventVehicle = new EventVehicle();
    testEventVehicle.setId(1L);
    testEventVehicle.setEvent(testEvent);
    testEventVehicle.setVehicleId(testVehicle);
  }

  // ========== GET ALL EVENT VEHICLES TESTS ==========

  @Test
  @DisplayName("getAllEventVehicles - Debe retornar todos los registros evento-vehículo")
  void getAllEventVehicles_shouldReturnAllEventVehicles() {
    // Given
    EventVehicle eventVehicle2 = new EventVehicle();
    eventVehicle2.setId(2L);
    eventVehicle2.setEvent(testEvent);

    List<EventVehicle> eventVehicles = List.of(testEventVehicle, eventVehicle2);
    when(eventVehicleRepository.findAll()).thenReturn(eventVehicles);

    // When
    List<EventVehicle> result = eventVehicleService.getAllEventVehicles();

    // Then
    assertThat(result).hasSize(2);
    assertThat(result).containsExactly(testEventVehicle, eventVehicle2);
    verify(eventVehicleRepository).findAll();
  }

  @Test
  @DisplayName("getAllEventVehicles - Debe retornar lista vacía cuando no hay registros")
  void getAllEventVehicles_shouldReturnEmptyListWhenNoRegistrations() {
    // Given
    when(eventVehicleRepository.findAll()).thenReturn(List.of());

    // When
    List<EventVehicle> result = eventVehicleService.getAllEventVehicles();

    // Then
    assertThat(result).isEmpty();
    verify(eventVehicleRepository).findAll();
  }

  // ========== GET EVENT VEHICLE BY ID TESTS ==========

  @Test
  @DisplayName("getEventVehicleById - Debe retornar registro existente")
  void getEventVehicleById_shouldReturnExistingEventVehicle() {
    // Given
    when(eventVehicleRepository.findById(1L)).thenReturn(Optional.of(testEventVehicle));

    // When
    Optional<EventVehicle> result = eventVehicleService.getEventVehicleById(1L);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(testEventVehicle);
    verify(eventVehicleRepository).findById(1L);
  }

  @Test
  @DisplayName("getEventVehicleById - Debe retornar vacío para registro inexistente")
  void getEventVehicleById_shouldReturnEmptyForNonExistentEventVehicle() {
    // Given
    when(eventVehicleRepository.findById(999L)).thenReturn(Optional.empty());

    // When
    Optional<EventVehicle> result = eventVehicleService.getEventVehicleById(999L);

    // Then
    assertThat(result).isEmpty();
    verify(eventVehicleRepository).findById(999L);
  }

  // ========== GET VEHICLES BY EVENT ID TESTS ==========

  @Test
  @DisplayName("getVehiclesByEventId - Debe retornar vehículos del evento")
  void getVehiclesByEventId_shouldReturnVehiclesForEvent() {
    // Given
    EventVehicle eventVehicle2 = new EventVehicle();
    eventVehicle2.setId(2L);
    eventVehicle2.setEvent(testEvent);

    List<EventVehicle> eventVehicles = List.of(testEventVehicle, eventVehicle2);
    when(eventVehicleRepository.findByEventId(1L)).thenReturn(eventVehicles);

    // When
    List<EventVehicle> result = eventVehicleService.getVehiclesByEventId(1L);

    // Then
    assertThat(result).hasSize(2);
    assertThat(result).containsExactly(testEventVehicle, eventVehicle2);
    verify(eventVehicleRepository).findByEventId(1L);
  }

  @Test
  @DisplayName("getVehiclesByEventId - Debe retornar lista vacía si evento no tiene vehículos")
  void getVehiclesByEventId_shouldReturnEmptyListIfEventHasNoVehicles() {
    // Given
    when(eventVehicleRepository.findByEventId(1L)).thenReturn(List.of());

    // When
    List<EventVehicle> result = eventVehicleService.getVehiclesByEventId(1L);

    // Then
    assertThat(result).isEmpty();
    verify(eventVehicleRepository).findByEventId(1L);
  }

  // ========== CREATE EVENT VEHICLE TESTS ==========

  @Test
  @DisplayName("createEventVehicle - Debe crear registro exitosamente")
  void createEventVehicle_shouldCreateEventVehicleSuccessfully() {
    // Given
    when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
    when(eventVehicleRepository.findByEventId(1L)).thenReturn(List.of());
    doNothing().when(businessRuleValidator).validateVehicleRegistration(testEvent, 1L);
    doNothing().when(businessRuleValidator).validateEventCapacity(testEvent, List.of());
    when(eventVehicleRepository.save(testEventVehicle)).thenReturn(testEventVehicle);

    // When
    EventVehicle result = eventVehicleService.createEventVehicle(testEventVehicle);

    // Then
    assertThat(result).isEqualTo(testEventVehicle);
    verify(eventRepository).findById(1L);
    verify(businessRuleValidator).validateVehicleRegistration(testEvent, 1L);
    verify(businessRuleValidator).validateEventCapacity(testEvent, List.of());
    verify(eventVehicleRepository).save(testEventVehicle);
  }

  @Test
  @DisplayName("createEventVehicle - Debe lanzar excepción si evento no existe")
  void createEventVehicle_shouldThrowExceptionIfEventNotFound() {
    // Given
    when(eventRepository.findById(1L)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> eventVehicleService.createEventVehicle(testEventVehicle))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("El evento especificado no existe");

    verify(eventRepository).findById(1L);
    verify(eventVehicleRepository, never()).save(any(EventVehicle.class));
  }

  @Test
  @DisplayName("createEventVehicle - Debe lanzar excepción si validación de registro falla")
  void createEventVehicle_shouldThrowExceptionIfRegistrationValidationFails() {
    // Given
    when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
    when(eventVehicleRepository.findByEventId(1L)).thenReturn(List.of());
    doThrow(new IllegalArgumentException("Validación falló"))
        .when(businessRuleValidator).validateVehicleRegistration(testEvent, 1L);

    // When & Then
    assertThatThrownBy(() -> eventVehicleService.createEventVehicle(testEventVehicle))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Validación falló");

    verify(businessRuleValidator).validateVehicleRegistration(testEvent, 1L);
    verify(eventVehicleRepository, never()).save(any(EventVehicle.class));
  }

  @Test
  @DisplayName("createEventVehicle - Debe lanzar excepción si validación de capacidad falla")
  void createEventVehicle_shouldThrowExceptionIfCapacityValidationFails() {
    // Given
    when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
    when(eventVehicleRepository.findByEventId(1L)).thenReturn(List.of());
    doNothing().when(businessRuleValidator).validateVehicleRegistration(testEvent, 1L);
    doThrow(new IllegalArgumentException("Capacidad excedida"))
        .when(businessRuleValidator).validateEventCapacity(testEvent, List.of());

    // When & Then
    assertThatThrownBy(() -> eventVehicleService.createEventVehicle(testEventVehicle))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Capacidad excedida");

    verify(businessRuleValidator).validateEventCapacity(testEvent, List.of());
    verify(eventVehicleRepository, never()).save(any(EventVehicle.class));
  }

  @Test
  @DisplayName("createEventVehicle - Debe lanzar excepción si vehículo ya está registrado")
  void createEventVehicle_shouldThrowExceptionIfVehicleAlreadyRegistered() {
    // Given
    EventVehicle existingRegistration = new EventVehicle();
    existingRegistration.setId(2L);
    existingRegistration.setEvent(testEvent);
    existingRegistration.setVehicleId(testVehicle); // Mismo vehículo

    when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
    when(eventVehicleRepository.findByEventId(1L)).thenReturn(List.of(existingRegistration));
    doNothing().when(businessRuleValidator).validateVehicleRegistration(testEvent, 1L);
    doNothing().when(businessRuleValidator).validateEventCapacity(testEvent, List.of(existingRegistration));

    // When & Then
    assertThatThrownBy(() -> eventVehicleService.createEventVehicle(testEventVehicle))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("El vehículo ya está registrado en este evento");

    verify(eventVehicleRepository, never()).save(any(EventVehicle.class));
  }

  @Test
  @DisplayName("createEventVehicle - Debe permitir registro de vehículo diferente")
  void createEventVehicle_shouldAllowRegistrationOfDifferentVehicle() {
    // Given
    Vehicle otherVehicle = TestDataBuilder.buildVehicle(2L, testUser, testCategory);
    EventVehicle existingRegistration = new EventVehicle();
    existingRegistration.setId(2L);
    existingRegistration.setEvent(testEvent);
    existingRegistration.setVehicleId(otherVehicle); // Vehículo diferente

    when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
    when(eventVehicleRepository.findByEventId(1L)).thenReturn(List.of(existingRegistration));
    doNothing().when(businessRuleValidator).validateVehicleRegistration(testEvent, 1L);
    doNothing().when(businessRuleValidator).validateEventCapacity(testEvent, List.of(existingRegistration));
    when(eventVehicleRepository.save(testEventVehicle)).thenReturn(testEventVehicle);

    // When
    EventVehicle result = eventVehicleService.createEventVehicle(testEventVehicle);

    // Then
    assertThat(result).isEqualTo(testEventVehicle);
    verify(eventVehicleRepository).save(testEventVehicle);
  }

  // ========== DELETE EVENT VEHICLE TESTS ==========

  @Test
  @DisplayName("deleteEventVehicle - Debe eliminar registro exitosamente")
  void deleteEventVehicle_shouldDeleteEventVehicleSuccessfully() {
    // Given
    doNothing().when(eventVehicleRepository).deleteById(1L);

    // When
    eventVehicleService.deleteEventVehicle(1L);

    // Then
    verify(eventVehicleRepository).deleteById(1L);
  }

  @Test
  @DisplayName("deleteEventVehicle - Debe manejar eliminación de registro inexistente")
  void deleteEventVehicle_shouldHandleDeletionOfNonExistentEventVehicle() {
    // Given
    doNothing().when(eventVehicleRepository).deleteById(999L);

    // When
    eventVehicleService.deleteEventVehicle(999L);

    // Then
    verify(eventVehicleRepository).deleteById(999L);
  }

  // ========== INTEGRATION TESTS ==========

  @Test
  @DisplayName("Flow completo - Registro exitoso de múltiples vehículos")
  void fullFlow_successfulRegistrationOfMultipleVehicles() {
    // Given
    Vehicle vehicle2 = TestDataBuilder.buildVehicle(2L, testUser, testCategory);
    EventVehicle eventVehicle2 = new EventVehicle();
    eventVehicle2.setId(2L);
    eventVehicle2.setEvent(testEvent);
    eventVehicle2.setVehicleId(vehicle2);

    // Primer registro
    when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
    when(eventVehicleRepository.findByEventId(1L)).thenReturn(List.of()).thenReturn(List.of(testEventVehicle));
    doNothing().when(businessRuleValidator).validateVehicleRegistration(eq(testEvent), anyLong());
    doNothing().when(businessRuleValidator).validateEventCapacity(eq(testEvent), anyList());
    when(eventVehicleRepository.save(testEventVehicle)).thenReturn(testEventVehicle);
    when(eventVehicleRepository.save(eventVehicle2)).thenReturn(eventVehicle2);

    // When
    EventVehicle result1 = eventVehicleService.createEventVehicle(testEventVehicle);
    EventVehicle result2 = eventVehicleService.createEventVehicle(eventVehicle2);

    // Then
    assertThat(result1).isEqualTo(testEventVehicle);
    assertThat(result2).isEqualTo(eventVehicle2);
    verify(eventVehicleRepository, times(2)).save(any(EventVehicle.class));
  }

  @Test
  @DisplayName("Flow completo - Validación y obtención de registros por evento")
  void fullFlow_validationAndRetrievalByEvent() {
    // Given
    List<EventVehicle> eventVehicles = List.of(testEventVehicle);
    when(eventVehicleRepository.findByEventId(1L)).thenReturn(eventVehicles);

    // When
    List<EventVehicle> vehicles = eventVehicleService.getVehiclesByEventId(1L);
    Optional<EventVehicle> specificVehicle = eventVehicleService.getEventVehicleById(1L);

    // Then
    assertThat(vehicles).hasSize(1);
    assertThat(vehicles.get(0)).isEqualTo(testEventVehicle);

    // Setup para getEventVehicleById
    when(eventVehicleRepository.findById(1L)).thenReturn(Optional.of(testEventVehicle));
    specificVehicle = eventVehicleService.getEventVehicleById(1L);
    assertThat(specificVehicle).isPresent();
    assertThat(specificVehicle.get()).isEqualTo(testEventVehicle);
  }

  @Test
  @DisplayName("Edge case - Debe lanzar excepción por null vehicle en comparación")
  void edgeCase_shouldThrowExceptionForNullVehicleInComparison() {
    // Given
    EventVehicle eventVehicleWithNullVehicle = new EventVehicle();
    eventVehicleWithNullVehicle.setId(2L);
    eventVehicleWithNullVehicle.setEvent(testEvent);
    eventVehicleWithNullVehicle.setVehicleId(null);

    when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
    when(eventVehicleRepository.findByEventId(1L)).thenReturn(List.of(eventVehicleWithNullVehicle));
    doNothing().when(businessRuleValidator).validateVehicleRegistration(testEvent, 1L);
    doNothing().when(businessRuleValidator).validateEventCapacity(testEvent, List.of(eventVehicleWithNullVehicle));

    // When & Then
    assertThatThrownBy(() -> eventVehicleService.createEventVehicle(testEventVehicle))
        .isInstanceOf(NullPointerException.class);

    verify(eventVehicleRepository, never()).save(any(EventVehicle.class));
  }
}