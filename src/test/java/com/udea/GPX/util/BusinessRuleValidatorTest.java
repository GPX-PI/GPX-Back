package com.udea.gpx.util;

import org.junit.jupiter.api.Test;

import com.udea.gpx.model.Event;
import com.udea.gpx.model.EventVehicle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("BusinessRuleValidator Tests")
class BusinessRuleValidatorTest {

  private BusinessRuleValidator validator;
  private Event validEvent;

  @BeforeEach
  void setUp() {
    validator = new BusinessRuleValidator();

    validEvent = TestDataBuilder.buildEvent(1L, "Test Event");
    validEvent.setLocation("Test Location");
    validEvent.setDetails("Test Details");
    validEvent.setStartDate(LocalDate.now().plusDays(30));
    validEvent.setEndDate(LocalDate.now().plusDays(32));
  }

  // ========== EVENT DATES VALIDATION ==========

  @Test
  @DisplayName("validateEventDates - Debe validar evento con fechas correctas")
  void validateEventDates_shouldValidateValidDates() {
    // When & Then
    assertThatCode(() -> validator.validateEventDates(validEvent))
        .doesNotThrowAnyException();
  }

  @Test
  @DisplayName("validateEventDates - Debe lanzar excepción si fecha inicio es nula")
  void validateEventDates_shouldThrowExceptionIfStartDateIsNull() {
    // Given
    Event eventWithNullStart = TestDataBuilder.buildEvent(1L, "Test Event");
    eventWithNullStart.setStartDate(null);
    eventWithNullStart.setEndDate(LocalDate.now().plusDays(10));

    // When & Then
    assertThatThrownBy(() -> validator.validateEventDates(eventWithNullStart))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Las fechas de inicio y fin del evento son obligatorias");
  }

  @Test
  @DisplayName("validateEventDates - Debe lanzar excepción si fecha fin es nula")
  void validateEventDates_shouldThrowExceptionIfEndDateIsNull() {
    // Given
    Event eventWithNullEnd = TestDataBuilder.buildEvent(2L, "Test Event");
    eventWithNullEnd.setStartDate(LocalDate.now().plusDays(10));
    eventWithNullEnd.setEndDate(null);

    // When & Then
    assertThatThrownBy(() -> validator.validateEventDates(eventWithNullEnd))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Las fechas de inicio y fin del evento son obligatorias");
  }

  @Test
  @DisplayName("validateEventDates - Debe lanzar excepción si inicio es posterior al fin")
  void validateEventDates_shouldThrowExceptionIfStartIsAfterEnd() {
    // Given
    Event eventWithInvalidDates = TestDataBuilder.buildEvent(3L, "Test Event");
    eventWithInvalidDates.setStartDate(LocalDate.now().plusDays(10));
    eventWithInvalidDates.setEndDate(LocalDate.now().plusDays(5));

    // When & Then
    assertThatThrownBy(() -> validator.validateEventDates(eventWithInvalidDates))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("La fecha de inicio no puede ser posterior a la fecha de fin");
  }

  @Test
  @DisplayName("validateEventDates - Debe lanzar excepción si evento terminó hace más de un año")
  void validateEventDates_shouldThrowExceptionIfEventEndedMoreThanOneYearAgo() {
    // Given
    Event oldEvent = TestDataBuilder.buildEvent(4L, "Old Event");
    oldEvent.setStartDate(LocalDate.now().minusYears(2));
    oldEvent.setEndDate(LocalDate.now().minusYears(1).minusDays(1));

    // When & Then
    assertThatThrownBy(() -> validator.validateEventDates(oldEvent))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("No se pueden crear eventos que terminaron hace más de un año");
  }

  @Test
  @DisplayName("validateEventDates - Debe lanzar excepción si evento es muy lejano")
  void validateEventDates_shouldThrowExceptionIfEventIsTooFarInFuture() {
    // Given
    Event futureEvent = TestDataBuilder.buildEvent(5L, "Future Event");
    futureEvent.setStartDate(LocalDate.now().plusYears(3));
    futureEvent.setEndDate(LocalDate.now().plusYears(3).plusDays(1));

    // When & Then
    assertThatThrownBy(() -> validator.validateEventDates(futureEvent))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("No se pueden crear eventos con más de 2 años de anticipación");
  }

  @Test
  @DisplayName("validateEventDates - Debe lanzar excepción si duración excede 30 días")
  void validateEventDates_shouldThrowExceptionIfDurationExceeds30Days() {
    // Given
    Event longEvent = TestDataBuilder.buildEvent(6L, "Long Event");
    longEvent.setStartDate(LocalDate.now().plusDays(30));
    longEvent.setEndDate(LocalDate.now().plusDays(65));

    // When & Then
    assertThatThrownBy(() -> validator.validateEventDates(longEvent))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("La duración del evento no puede exceder 30 días");
  }

  // ========== VEHICLE REGISTRATION VALIDATION ==========

  @Test
  @DisplayName("validateVehicleRegistration - Debe validar registro válido")
  void validateVehicleRegistration_shouldValidateValidRegistration() {
    // When & Then
    assertThatCode(() -> validator.validateVehicleRegistration(validEvent, 1L))
        .doesNotThrowAnyException();
  }

  @Test
  @DisplayName("validateVehicleRegistration - Debe lanzar excepción si evento es nulo")
  void validateVehicleRegistration_shouldThrowExceptionIfEventIsNull() {
    // When & Then
    assertThatThrownBy(() -> validator.validateVehicleRegistration(null, 1L))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("El evento no existe");
  }

  @Test
  @DisplayName("validateVehicleRegistration - Debe lanzar excepción si evento ya terminó")
  void validateVehicleRegistration_shouldThrowExceptionIfEventAlreadyEnded() {
    // Given
    Event pastEvent = TestDataBuilder.buildEvent(7L, "Past Event");
    pastEvent.setStartDate(LocalDate.now().minusDays(10));
    pastEvent.setEndDate(LocalDate.now().minusDays(5));

    // When & Then
    assertThatThrownBy(() -> validator.validateVehicleRegistration(pastEvent, 1L))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("No se puede registrar vehículos en eventos que ya han terminado");
  }

  @Test
  @DisplayName("validateVehicleRegistration - Debe lanzar excepción si registro es muy temprano")
  void validateVehicleRegistration_shouldThrowExceptionIfRegistrationIsTooEarly() {
    // Given
    Event farFutureEvent = TestDataBuilder.buildEvent(8L, "Far Future Event");
    farFutureEvent.setStartDate(LocalDate.now().plusDays(400));
    farFutureEvent.setEndDate(LocalDate.now().plusDays(402));

    // When & Then
    assertThatThrownBy(() -> validator.validateVehicleRegistration(farFutureEvent, 1L))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("No se puede registrar vehículos con más de 365 días de anticipación");
  }

  @Test
  @DisplayName("validateVehicleRegistration - Debe lanzar excepción si registro es muy tarde")
  void validateVehicleRegistration_shouldThrowExceptionIfRegistrationIsTooLate() {
    // Given
    Event soonEvent = TestDataBuilder.buildEvent(9L, "Soon Event");
    soonEvent.setStartDate(LocalDate.now()); // Hoy
    soonEvent.setEndDate(LocalDate.now().plusDays(1));

    // When & Then
    assertThatThrownBy(() -> validator.validateVehicleRegistration(soonEvent, 1L))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Los registros se cierran 1 día(s) antes del inicio del evento");
  }

  // ========== EVENT CAPACITY VALIDATION ==========

  @Test
  @DisplayName("validateEventCapacity - Debe validar capacidad disponible")
  void validateEventCapacity_shouldValidateAvailableCapacity() {
    // Given
    List<EventVehicle> registrations = Collections.nCopies(50, new EventVehicle());

    // When & Then
    assertThatCode(() -> validator.validateEventCapacity(validEvent, registrations))
        .doesNotThrowAnyException();
  }

  @Test
  @DisplayName("validateEventCapacity - Debe lanzar excepción si capacidad está llena")
  void validateEventCapacity_shouldThrowExceptionIfCapacityIsFull() {
    // Given
    List<EventVehicle> registrations = Collections.nCopies(100, new EventVehicle());

    // When & Then
    assertThatThrownBy(() -> validator.validateEventCapacity(validEvent, registrations))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("El evento ha alcanzado su capacidad máxima de 100 vehículos");
  }

  // ========== STAGE RESULT TIMESTAMP VALIDATION ==========

  @Test
  @DisplayName("validateStageResultTimestamp - Debe validar timestamp válido")
  void validateStageResultTimestamp_shouldValidateValidTimestamp() {
    // Given - Crear un timestamp en el pasado pero dentro del rango del evento
    Event pastEvent = TestDataBuilder.buildEvent(11L, "Past Event");
    pastEvent.setStartDate(LocalDate.now().minusDays(5));
    pastEvent.setEndDate(LocalDate.now().minusDays(3));
    LocalDateTime validTimestamp = pastEvent.getStartDate().plusDays(1).atTime(10, 0);

    // When & Then
    assertThatCode(() -> validator.validateStageResultTimestamp(validTimestamp, pastEvent))
        .doesNotThrowAnyException();
  }

  @Test
  @DisplayName("validateStageResultTimestamp - Debe lanzar excepción si timestamp es nulo")
  void validateStageResultTimestamp_shouldThrowExceptionIfTimestampIsNull() {
    // When & Then
    assertThatThrownBy(() -> validator.validateStageResultTimestamp(null, validEvent))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("La fecha y hora del resultado es obligatoria");
  }

  @Test
  @DisplayName("validateStageResultTimestamp - Debe lanzar excepción si timestamp es futuro")
  void validateStageResultTimestamp_shouldThrowExceptionIfTimestampIsFuture() {
    // Given
    LocalDateTime futureTimestamp = LocalDateTime.now().plusHours(1);

    // When & Then
    assertThatThrownBy(() -> validator.validateStageResultTimestamp(futureTimestamp, validEvent))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("No se pueden registrar resultados con fecha futura");
  }

  @Test
  @DisplayName("validateStageResultTimestamp - Debe lanzar excepción si timestamp es muy antiguo")
  void validateStageResultTimestamp_shouldThrowExceptionIfTimestampIsVeryOld() {
    // Given
    LocalDateTime veryOldTimestamp = LocalDateTime.now().minusYears(3);

    // When & Then
    assertThatThrownBy(() -> validator.validateStageResultTimestamp(veryOldTimestamp, validEvent))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("No se pueden registrar resultados con fecha anterior a 2 años");
  }

  @Test
  @DisplayName("validateStageResultTimestamp - Debe lanzar excepción si timestamp está fuera del evento")
  void validateStageResultTimestamp_shouldThrowExceptionIfTimestampIsOutsideEvent() {
    // Given - Usar un timestamp que esté claramente fuera del rango del evento
    // (antes del inicio)
    Event pastEvent = TestDataBuilder.buildEvent(12L, "Past Event");
    pastEvent.setStartDate(LocalDate.now().minusDays(10));
    pastEvent.setEndDate(LocalDate.now().minusDays(8));
    LocalDateTime timestampBeforeEvent = pastEvent.getStartDate().minusDays(2).atStartOfDay();

    // When & Then
    assertThatThrownBy(() -> validator.validateStageResultTimestamp(timestampBeforeEvent, pastEvent))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("La fecha del resultado debe estar dentro del período del evento");
  }

  // ========== USER EVENT REGISTRATION VALIDATION ==========

  @Test
  @DisplayName("validateUserEventRegistration - Debe validar registro de usuario válido")
  void validateUserEventRegistration_shouldValidateValidUserRegistration() {
    // When & Then
    assertThatCode(() -> validator.validateUserEventRegistration(1L, validEvent))
        .doesNotThrowAnyException();
  }

  @Test
  @DisplayName("validateUserEventRegistration - Debe lanzar excepción si userId es nulo")
  void validateUserEventRegistration_shouldThrowExceptionIfUserIdIsNull() {
    // When & Then
    assertThatThrownBy(() -> validator.validateUserEventRegistration(null, validEvent))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("ID de usuario inválido");
  }

  @Test
  @DisplayName("validateUserEventRegistration - Debe lanzar excepción si userId es inválido")
  void validateUserEventRegistration_shouldThrowExceptionIfUserIdIsInvalid() {
    // When & Then
    assertThatThrownBy(() -> validator.validateUserEventRegistration(0L, validEvent))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("ID de usuario inválido");
  }

  // ========== GPS COORDINATES VALIDATION ==========

  @Test
  @DisplayName("validateGpsCoordinates - Debe validar coordenadas válidas")
  void validateGpsCoordinates_shouldValidateValidCoordinates() {
    // When & Then
    assertThatCode(() -> validator.validateGpsCoordinates(6.2442, -75.5812))
        .doesNotThrowAnyException();
  }

  @Test
  @DisplayName("validateGpsCoordinates - Debe validar coordenadas nulas")
  void validateGpsCoordinates_shouldValidateNullCoordinates() {
    // When & Then
    assertThatCode(() -> validator.validateGpsCoordinates(null, null))
        .doesNotThrowAnyException();
  }

  @Test
  @DisplayName("validateGpsCoordinates - Debe lanzar excepción si latitud es inválida")
  void validateGpsCoordinates_shouldThrowExceptionIfLatitudeIsInvalid() {
    // When & Then
    assertThatThrownBy(() -> validator.validateGpsCoordinates(91.0, -75.5812))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("La latitud debe estar entre -90 y 90 grados");

    assertThatThrownBy(() -> validator.validateGpsCoordinates(-91.0, -75.5812))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("La latitud debe estar entre -90 y 90 grados");
  }

  @Test
  @DisplayName("validateGpsCoordinates - Debe lanzar excepción si longitud es inválida")
  void validateGpsCoordinates_shouldThrowExceptionIfLongitudeIsInvalid() {
    // When & Then
    assertThatThrownBy(() -> validator.validateGpsCoordinates(6.2442, 181.0))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("La longitud debe estar entre -180 y 180 grados");

    assertThatThrownBy(() -> validator.validateGpsCoordinates(6.2442, -181.0))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("La longitud debe estar entre -180 y 180 grados");
  }

  // ========== EVENT NAME VALIDATION ==========

  @Test
  @DisplayName("validateEventName - Debe validar nombre válido")
  void validateEventName_shouldValidateValidName() {
    // When & Then
    assertThatCode(() -> validator.validateEventName("Test Event"))
        .doesNotThrowAnyException();
  }

  @Test
  @DisplayName("validateEventName - Debe lanzar excepción si nombre es nulo")
  void validateEventName_shouldThrowExceptionIfNameIsNull() {
    // When & Then
    assertThatThrownBy(() -> validator.validateEventName(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("El nombre del evento es obligatorio");
  }

  @Test
  @DisplayName("validateEventName - Debe lanzar excepción si nombre está vacío")
  void validateEventName_shouldThrowExceptionIfNameIsEmpty() {
    // When & Then
    assertThatThrownBy(() -> validator.validateEventName("   "))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("El nombre del evento es obligatorio");
  }

  @Test
  @DisplayName("validateEventName - Debe lanzar excepción si nombre es muy corto")
  void validateEventName_shouldThrowExceptionIfNameIsTooShort() {
    // When & Then
    assertThatThrownBy(() -> validator.validateEventName("AB"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("El nombre del evento debe tener al menos 3 caracteres");
  }

  @Test
  @DisplayName("validateEventName - Debe lanzar excepción si nombre es muy largo")
  void validateEventName_shouldThrowExceptionIfNameIsTooLong() {
    // Given
    String longName = "A".repeat(51);

    // When & Then
    assertThatThrownBy(() -> validator.validateEventName(longName))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("El nombre del evento no puede exceder 50 caracteres");
  }

  @Test
  @DisplayName("validateEventName - Debe lanzar excepción si nombre son solo números")
  void validateEventName_shouldThrowExceptionIfNameIsOnlyNumbers() {
    // When & Then
    assertThatThrownBy(() -> validator.validateEventName("12345"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("El nombre del evento no puede contener solo números");
  }

  // ========== EVENT DETAILS VALIDATION ==========

  @Test
  @DisplayName("validateEventDetails - Debe validar detalles válidos")
  void validateEventDetails_shouldValidateValidDetails() {
    // When & Then
    assertThatCode(() -> validator.validateEventDetails("These are valid details"))
        .doesNotThrowAnyException();
  }

  @Test
  @DisplayName("validateEventDetails - Debe validar detalles nulos")
  void validateEventDetails_shouldValidateNullDetails() {
    // When & Then
    assertThatCode(() -> validator.validateEventDetails(null))
        .doesNotThrowAnyException();
  }

  @Test
  @DisplayName("validateEventDetails - Debe lanzar excepción si detalles son muy largos")
  void validateEventDetails_shouldThrowExceptionIfDetailsAreTooLong() {
    // Given
    String longDetails = "A".repeat(401);

    // When & Then
    assertThatThrownBy(() -> validator.validateEventDetails(longDetails))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Los detalles del evento no pueden exceder 400 caracteres");
  }

  // ========== EVENT LOCATION VALIDATION ==========

  @Test
  @DisplayName("validateEventLocation - Debe validar ubicación válida")
  void validateEventLocation_shouldValidateValidLocation() {
    // When & Then
    assertThatCode(() -> validator.validateEventLocation("Medellín"))
        .doesNotThrowAnyException();
  }

  @Test
  @DisplayName("validateEventLocation - Debe lanzar excepción si ubicación es nula")
  void validateEventLocation_shouldThrowExceptionIfLocationIsNull() {
    // When & Then
    assertThatThrownBy(() -> validator.validateEventLocation(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("La ubicación del evento es obligatoria");
  }

  @Test
  @DisplayName("validateEventLocation - Debe lanzar excepción si ubicación está vacía")
  void validateEventLocation_shouldThrowExceptionIfLocationIsEmpty() {
    // When & Then
    assertThatThrownBy(() -> validator.validateEventLocation("   "))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("La ubicación del evento es obligatoria");
  }

  @Test
  @DisplayName("validateEventLocation - Debe lanzar excepción si ubicación es muy larga")
  void validateEventLocation_shouldThrowExceptionIfLocationIsTooLong() {
    // Given
    String longLocation = "A".repeat(51);

    // When & Then
    assertThatThrownBy(() -> validator.validateEventLocation(longLocation))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("La ubicación del evento no puede exceder 50 caracteres");
  }

  // ========== COMPLETE EVENT VALIDATION ==========

  @Test
  @DisplayName("validateCompleteEvent - Debe validar evento completo válido")
  void validateCompleteEvent_shouldValidateValidCompleteEvent() {
    // When & Then
    assertThatCode(() -> validator.validateCompleteEvent(validEvent))
        .doesNotThrowAnyException();
  }

  @Test
  @DisplayName("validateCompleteEvent - Debe lanzar excepción si algún campo es inválido")
  void validateCompleteEvent_shouldThrowExceptionIfAnyFieldIsInvalid() {
    // Given
    Event invalidEvent = TestDataBuilder.buildEvent(10L, "AB"); // Muy corto
    invalidEvent.setLocation("Valid Location");
    invalidEvent.setDetails("Valid Details");
    invalidEvent.setStartDate(LocalDate.now().plusDays(30));
    invalidEvent.setEndDate(LocalDate.now().plusDays(32));

    // When & Then
    assertThatThrownBy(() -> validator.validateCompleteEvent(invalidEvent))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("El nombre del evento debe tener al menos 3 caracteres");
  }
}