package com.udea.gpx.util;

import org.springframework.stereotype.Component;

import com.udea.gpx.model.Event;
import com.udea.gpx.model.EventVehicle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Utilidad para validar reglas de negocio específicas del sistema gpx Racing
 */
@Component
public class BusinessRuleValidator {

  private static final Logger logger = LoggerFactory.getLogger(BusinessRuleValidator.class);

  // Constantes para reglas de negocio
  private static final int MAX_DAYS_FOR_REGISTRATION = 365; // 1 año
  private static final int MIN_DAYS_BEFORE_START = 1; // 1 día antes
  private static final int DEFAULT_MAX_VEHICLES = 100;
  private static final boolean ENFORCE_CAPACITY_LIMITS = true;

  /**
   * Valida que las fechas de un evento sean coherentes y cumplan las reglas de
   * negocio
   */
  public void validateEventDates(Event event) {
    logger.debug("🔍 BusinessRuleValidator.validateEventDates - Validando fechas del evento: {}",
        event.getName());

    if (event.getStartDate() == null || event.getEndDate() == null) {
      throw new IllegalArgumentException("Las fechas de inicio y fin del evento son obligatorias");
    }

    if (event.getStartDate().isAfter(event.getEndDate())) {
      throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
    }

    // No permitir crear eventos que hayan terminado hace más de un año
    LocalDate oneYearAgo = LocalDate.now().minusYears(1);
    if (event.getEndDate().isBefore(oneYearAgo)) {
      throw new IllegalArgumentException("No se pueden crear eventos que terminaron hace más de un año");
    }

    // Validar que el evento no sea demasiado lejano (máximo 2 años en el futuro)
    LocalDate twoYearsFromNow = LocalDate.now().plusYears(2);
    if (event.getStartDate().isAfter(twoYearsFromNow)) {
      throw new IllegalArgumentException("No se pueden crear eventos con más de 2 años de anticipación");
    }

    // Validar duración razonable del evento (máximo 30 días)
    long eventDuration = ChronoUnit.DAYS.between(event.getStartDate(), event.getEndDate());
    if (eventDuration > 30) {
      throw new IllegalArgumentException("La duración del evento no puede exceder 30 días");
    }

    logger.debug("✅ BusinessRuleValidator.validateEventDates - Fechas válidas, duración: {} días", eventDuration);
  }

  /**
   * Valida que un vehículo se pueda registrar en un evento
   */
  public void validateVehicleRegistration(Event event, Long vehicleId) {
    logger.debug(
        "🔍 BusinessRuleValidator.validateVehicleRegistration - Validando registro del vehículo {}",
        vehicleId);

    if (event == null) {
      throw new IllegalArgumentException("El evento no existe");
    }

    logger.debug("🔍 BusinessRuleValidator.validateVehicleRegistration - Validando evento {}", event.getId());

    // No permitir registro en eventos pasados
    if (event.getEndDate().isBefore(LocalDate.now())) {
      throw new IllegalArgumentException("No se puede registrar vehículos en eventos que ya han terminado");
    }

    // Validar que el registro no sea demasiado temprano
    long daysUntilEvent = ChronoUnit.DAYS.between(LocalDate.now(), event.getStartDate());
    if (daysUntilEvent > MAX_DAYS_FOR_REGISTRATION) {
      throw new IllegalArgumentException(
          String.format("No se puede registrar vehículos con más de %d días de anticipación",
              MAX_DAYS_FOR_REGISTRATION));
    }

    // Validar tiempo mínimo antes del inicio
    if (daysUntilEvent < MIN_DAYS_BEFORE_START) {
      throw new IllegalArgumentException(
          String.format("Los registros se cierran %d día(s) antes del inicio del evento",
              MIN_DAYS_BEFORE_START));
    }

    logger.debug("✅ BusinessRuleValidator.validateVehicleRegistration - Registro válido, {} días antes del evento",
        daysUntilEvent);
  }

  /**
   * Valida la capacidad máxima de un evento
   */
  public void validateEventCapacity(Event event, List<EventVehicle> currentRegistrations) {
    logger.debug("🔍 BusinessRuleValidator.validateEventCapacity - Validando capacidad del evento {}",
        event.getId());

    if (!ENFORCE_CAPACITY_LIMITS) {
      logger.debug("✅ BusinessRuleValidator.validateEventCapacity - Límites de capacidad deshabilitados");
      return;
    }

    int maxCapacity = DEFAULT_MAX_VEHICLES;
    int currentCount = currentRegistrations.size();

    if (currentCount >= maxCapacity) {
      throw new IllegalArgumentException(
          String.format("El evento ha alcanzado su capacidad máxima de %d vehículos", maxCapacity));
    }

    // Advertencia cuando se está cerca del límite (90%)
    if (currentCount >= maxCapacity * 0.9) {
      logger.warn("⚠️ BusinessRuleValidator.validateEventCapacity - Evento {} cerca del límite: {}/{}",
          event.getId(), currentCount, maxCapacity);
    }

    logger.debug("✅ BusinessRuleValidator.validateEventCapacity - Capacidad disponible: {}/{}",
        currentCount, maxCapacity);
  }

  /**
   * Valida que una fecha/hora de resultado de etapa sea válida
   */
  public void validateStageResultTimestamp(LocalDateTime timestamp, Event event) {
    logger.debug("🔍 BusinessRuleValidator.validateStageResultTimestamp - Validando timestamp de resultado");

    if (timestamp == null) {
      throw new IllegalArgumentException("La fecha y hora del resultado es obligatoria");
    }

    // Validar que el timestamp esté dentro del rango del evento
    if (event != null) {
      LocalDate timestampDate = timestamp.toLocalDate();
      if (timestampDate.isBefore(event.getStartDate()) ||
          timestampDate.isAfter(event.getEndDate().plusDays(1))) {
        throw new IllegalArgumentException(
            "La fecha del resultado debe estar dentro del período del evento");
      }
    }

    logger.debug("✅ BusinessRuleValidator.validateStageResultTimestamp - Timestamp válido");
  }

  /**
   * Valida que un usuario pueda registrarse en un evento específico
   */
  public void validateUserEventRegistration(Long userId, Event event) {
    logger.debug(
        "🔍 BusinessRuleValidator.validateUserEventRegistration - Validando registro de usuario {}",
        userId);

    if (userId == null || userId <= 0) {
      throw new IllegalArgumentException("ID de usuario inválido");
    }

    // Validar que el evento permita nuevos registros
    validateVehicleRegistration(event, null);

    logger.debug("✅ BusinessRuleValidator.validateUserEventRegistration - Registro de usuario válido");
  }

  /**
   * Valida coordenadas GPS
   */
  public void validateGpsCoordinates(Double latitude, Double longitude) {
    logger.debug("🔍 BusinessRuleValidator.validateGpsCoordinates - Validando coordenadas GPS");

    if (latitude != null && (latitude < -90.0 || latitude > 90.0)) {
      throw new IllegalArgumentException("La latitud debe estar entre -90 y 90 grados");
    }

    if (longitude != null && (longitude < -180.0 || longitude > 180.0)) {
      throw new IllegalArgumentException("La longitud debe estar entre -180 y 180 grados");
    }

    // Validar que las coordenadas no sean (0,0) a menos que sea intencional
    if (latitude != null && longitude != null &&
        latitude == 0.0 && longitude == 0.0) {
      logger.warn(
          "⚠️ BusinessRuleValidator.validateGpsCoordinates - Coordenadas (0,0) detectadas, verificar si es intencional");
    }

    logger.debug("✅ BusinessRuleValidator.validateGpsCoordinates - Coordenadas válidas");
  }

  /**
   * Valida que el nombre del evento sea apropiado
   */
  public void validateEventName(String eventName) {
    logger.debug("🔍 BusinessRuleValidator.validateEventName - Validando nombre del evento");

    if (eventName == null || eventName.trim().isEmpty()) {
      throw new IllegalArgumentException("El nombre del evento es obligatorio");
    }

    if (eventName.length() < 3) {
      throw new IllegalArgumentException("El nombre del evento debe tener al menos 3 caracteres");
    }

    if (eventName.length() > 50) {
      throw new IllegalArgumentException("El nombre del evento no puede exceder 50 caracteres");
    }

    // Evitar nombres con solo números
    if (eventName.matches("^\\d+$")) {
      throw new IllegalArgumentException("El nombre del evento no puede contener solo números");
    }

    logger.debug("✅ BusinessRuleValidator.validateEventName - Nombre válido: {}", eventName);
  }

  /**
   * Valida los detalles del evento
   */
  public void validateEventDetails(String details) {
    logger.debug("🔍 BusinessRuleValidator.validateEventDetails - Validando detalles del evento");

    if (details != null && details.length() > 400) {
      throw new IllegalArgumentException("Los detalles del evento no pueden exceder 400 caracteres");
    }

    logger.debug("✅ BusinessRuleValidator.validateEventDetails - Detalles válidos");
  }

  /**
   * Valida la ubicación del evento
   */
  public void validateEventLocation(String location) {
    logger.debug("🔍 BusinessRuleValidator.validateEventLocation - Validando ubicación del evento");

    if (location == null || location.trim().isEmpty()) {
      throw new IllegalArgumentException("La ubicación del evento es obligatoria");
    }

    if (location.length() > 50) {
      throw new IllegalArgumentException("La ubicación del evento no puede exceder 50 caracteres");
    }

    logger.debug("✅ BusinessRuleValidator.validateEventLocation - Ubicación válida: {}", location);
  }

  /**
   * Validación completa de un evento antes de creación/actualización
   */
  public void validateCompleteEvent(Event event) {
    logger.debug("🔍 BusinessRuleValidator.validateCompleteEvent - Validación completa del evento {}",
        event.getName());

    validateEventName(event.getName());
    validateEventLocation(event.getLocation());
    validateEventDetails(event.getDetails());
    validateEventDates(event);

    logger.debug("✅ BusinessRuleValidator.validateCompleteEvent - Evento válido completamente");
  }
}