package com.udea.GPX.util;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * Utilidades para manejo consistente de fechas y timezones
 */
public class DateTimeUtils {

  private static final String COLOMBIA_TIMEZONE = "America/Bogota";
  private static final ZoneId UTC_ZONE = ZoneId.of("UTC");
  private static final ZoneId COLOMBIA_ZONE = ZoneId.of(COLOMBIA_TIMEZONE);

  // Formatters estándar
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter
      .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
  public static final DateTimeFormatter DISPLAY_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

  private DateTimeUtils() {
    // Clase utilitaria - constructor privado
  }

  /**
   * Obtiene la fecha/hora actual en UTC
   */
  public static LocalDateTime nowUTC() {
    return LocalDateTime.now(UTC_ZONE);
  }

  /**
   * Obtiene la fecha actual en UTC
   */
  public static LocalDate todayUTC() {
    return LocalDate.now(UTC_ZONE);
  }

  /**
   * Convierte LocalDateTime (asumido como UTC) a Colombia timezone para display
   */
  public static ZonedDateTime toColombiaTime(LocalDateTime utcDateTime) {
    if (utcDateTime == null) {
      return null;
    }
    return utcDateTime.atZone(UTC_ZONE).withZoneSameInstant(COLOMBIA_ZONE);
  }

  /**
   * Convierte fecha/hora de Colombia a UTC
   */
  public static LocalDateTime fromColombiaToUTC(LocalDateTime colombiaDateTime) {
    if (colombiaDateTime == null) {
      return null;
    }
    return colombiaDateTime.atZone(COLOMBIA_ZONE).withZoneSameInstant(UTC_ZONE).toLocalDateTime();
  }

  /**
   * Formatea fecha para display en timezone de Colombia
   */
  public static String formatForDisplay(LocalDateTime utcDateTime) {
    if (utcDateTime == null) {
      return null;
    }
    ZonedDateTime colombiaTime = toColombiaTime(utcDateTime);
    return colombiaTime.format(DISPLAY_DATETIME_FORMATTER);
  }

  /**
   * Formatea fecha para display
   */
  public static String formatDateForDisplay(LocalDate date) {
    if (date == null) {
      return null;
    }
    return date.format(DATE_FORMATTER);
  }

  /**
   * Verifica si una fecha está en el pasado (comparado con UTC)
   */
  public static boolean isPastDate(LocalDate date) {
    if (date == null) {
      return false;
    }
    return date.isBefore(todayUTC());
  }

  /**
   * Verifica si una fecha/hora está en el pasado (comparado con UTC)
   */
  public static boolean isPastDateTime(LocalDateTime dateTime) {
    if (dateTime == null) {
      return false;
    }
    return dateTime.isBefore(nowUTC());
  }

  /**
   * Verifica si una fecha está en el futuro (comparado con UTC)
   */
  public static boolean isFutureDate(LocalDate date) {
    if (date == null) {
      return false;
    }
    return date.isAfter(todayUTC());
  }

  /**
   * Calcula la diferencia en días entre dos fechas
   */
  public static long daysBetween(LocalDate start, LocalDate end) {
    if (start == null || end == null) {
      return 0;
    }
    return Duration.between(start.atStartOfDay(), end.atStartOfDay()).toDays();
  }

  /**
   * Calcula la diferencia en segundos entre dos fechas/horas
   */
  public static long secondsBetween(LocalDateTime start, LocalDateTime end) {
    if (start == null || end == null) {
      return 0;
    }
    return Duration.between(start, end).getSeconds();
  }

  /**
   * Obtiene el inicio del día en UTC para una fecha dada
   */
  public static LocalDateTime startOfDayUTC(LocalDate date) {
    if (date == null) {
      return null;
    }
    return date.atStartOfDay();
  }

  /**
   * Obtiene el final del día en UTC para una fecha dada
   */
  public static LocalDateTime endOfDayUTC(LocalDate date) {
    if (date == null) {
      return null;
    }
    return date.atTime(23, 59, 59, 999_999_999);
  }

  /**
   * Valida que start date sea anterior o igual a end date
   */
  public static boolean isValidDateRange(LocalDate startDate, LocalDate endDate) {
    if (startDate == null || endDate == null) {
      return false;
    }
    return !startDate.isAfter(endDate);
  }

  /**
   * Valida que start datetime sea anterior o igual a end datetime
   */
  public static boolean isValidDateTimeRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
    if (startDateTime == null || endDateTime == null) {
      return false;
    }
    return !startDateTime.isAfter(endDateTime);
  }
}