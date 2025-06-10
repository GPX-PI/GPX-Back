package com.udea.GPX.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import static org.assertj.core.api.Assertions.*;

@DisplayName("DateTimeUtils Tests")
class DateTimeUtilsTest {

  @Test
  @DisplayName("nowUTC - Debe obtener fecha/hora actual en UTC")
  void nowUTC_shouldGetCurrentDateTimeInUTC() {
    // When
    LocalDateTime now = DateTimeUtils.nowUTC();

    // Then
    assertThat(now).isNotNull();
    assertThat(now).isBeforeOrEqualTo(LocalDateTime.now().plusSeconds(1));
  }

  @Test
  @DisplayName("todayUTC - Debe obtener fecha actual en UTC")
  void todayUTC_shouldGetCurrentDateInUTC() {
    // When
    LocalDate today = DateTimeUtils.todayUTC();

    // Then
    assertThat(today).isNotNull();
    assertThat(today).isEqualTo(LocalDate.now());
  }

  @Test
  @DisplayName("toColombiaTime - Debe convertir UTC a hora de Colombia")
  void toColombiaTime_shouldConvertUTCToColombiaTime() {
    // Given
    LocalDateTime utcDateTime = LocalDateTime.of(2024, 6, 10, 15, 30);

    // When
    ZonedDateTime colombiaTime = DateTimeUtils.toColombiaTime(utcDateTime);

    // Then
    assertThat(colombiaTime).isNotNull();
    assertThat(colombiaTime.getZone()).isEqualTo(ZoneId.of("America/Bogota"));
    // Colombia está UTC-5, entonces 15:30 UTC = 10:30 Colombia
    assertThat(colombiaTime.getHour()).isEqualTo(10);
    assertThat(colombiaTime.getMinute()).isEqualTo(30);
  }

  @Test
  @DisplayName("toColombiaTime - Debe manejar entrada nula")
  void toColombiaTime_shouldHandleNullInput() {
    // When & Then
    assertThat(DateTimeUtils.toColombiaTime(null)).isNull();
  }

  @Test
  @DisplayName("fromColombiaToUTC - Debe convertir Colombia a UTC")
  void fromColombiaToUTC_shouldConvertColombiaTimeToUTC() {
    // Given
    LocalDateTime colombiaDateTime = LocalDateTime.of(2024, 6, 10, 10, 30);

    // When
    LocalDateTime utcDateTime = DateTimeUtils.fromColombiaToUTC(colombiaDateTime);

    // Then
    assertThat(utcDateTime).isNotNull();
    // Colombia está UTC-5, entonces 10:30 Colombia = 15:30 UTC
    assertThat(utcDateTime.getHour()).isEqualTo(15);
    assertThat(utcDateTime.getMinute()).isEqualTo(30);
  }

  @Test
  @DisplayName("fromColombiaToUTC - Debe manejar entrada nula")
  void fromColombiaToUTC_shouldHandleNullInput() {
    // When & Then
    assertThat(DateTimeUtils.fromColombiaToUTC(null)).isNull();
  }

  @Test
  @DisplayName("formatForDisplay - Debe formatear fecha para display en Colombia")
  void formatForDisplay_shouldFormatDateForColombiaDisplay() {
    // Given
    LocalDateTime utcDateTime = LocalDateTime.of(2024, 6, 10, 15, 30, 45);

    // When
    String result = DateTimeUtils.formatForDisplay(utcDateTime);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).contains("10/06/2024");
    assertThat(result).contains("10:30:45"); // Hora de Colombia
  }

  @Test
  @DisplayName("formatForDisplay - Debe manejar entrada nula")
  void formatForDisplay_shouldHandleNullInput() {
    // When & Then
    assertThat(DateTimeUtils.formatForDisplay(null)).isNull();
  }

  @Test
  @DisplayName("formatDateForDisplay - Debe formatear fecha")
  void formatDateForDisplay_shouldFormatDate() {
    // Given
    LocalDate date = LocalDate.of(2024, 6, 10);

    // When
    String result = DateTimeUtils.formatDateForDisplay(date);

    // Then
    assertThat(result).isEqualTo("2024-06-10");
  }

  @Test
  @DisplayName("formatDateForDisplay - Debe manejar entrada nula")
  void formatDateForDisplay_shouldHandleNullInput() {
    // When & Then
    assertThat(DateTimeUtils.formatDateForDisplay(null)).isNull();
  }

  @Test
  @DisplayName("isPastDate - Debe validar fechas en el pasado")
  void isPastDate_shouldValidatePastDates() {
    // Given
    LocalDate pastDate = LocalDate.now().minusDays(1);
    LocalDate futureDate = LocalDate.now().plusDays(1);
    LocalDate today = LocalDate.now();

    // When & Then
    assertThat(DateTimeUtils.isPastDate(pastDate)).isTrue();
    assertThat(DateTimeUtils.isPastDate(futureDate)).isFalse();
    assertThat(DateTimeUtils.isPastDate(today)).isFalse();
    assertThat(DateTimeUtils.isPastDate(null)).isFalse();
  }

  @Test
  @DisplayName("isPastDateTime - Debe validar fechas/horas en el pasado")
  void isPastDateTime_shouldValidatePastDateTimes() {
    // Given
    LocalDateTime pastDateTime = LocalDateTime.now().minusHours(1);
    LocalDateTime futureDateTime = LocalDateTime.now().plusHours(1);

    // When & Then
    assertThat(DateTimeUtils.isPastDateTime(pastDateTime)).isTrue();
    assertThat(DateTimeUtils.isPastDateTime(futureDateTime)).isFalse();
    assertThat(DateTimeUtils.isPastDateTime(null)).isFalse();
  }

  @Test
  @DisplayName("isFutureDate - Debe validar fechas en el futuro")
  void isFutureDate_shouldValidateFutureDates() {
    // Given
    LocalDate futureDate = LocalDate.now().plusDays(1);
    LocalDate pastDate = LocalDate.now().minusDays(1);
    LocalDate today = LocalDate.now();

    // When & Then
    assertThat(DateTimeUtils.isFutureDate(futureDate)).isTrue();
    assertThat(DateTimeUtils.isFutureDate(pastDate)).isFalse();
    assertThat(DateTimeUtils.isFutureDate(today)).isFalse();
    assertThat(DateTimeUtils.isFutureDate(null)).isFalse();
  }

  @Test
  @DisplayName("daysBetween - Debe calcular días entre fechas")
  void daysBetween_shouldCalculateDaysBetweenDates() {
    // Given
    LocalDate start = LocalDate.of(2024, 6, 10);
    LocalDate end = LocalDate.of(2024, 6, 15);

    // When
    long days = DateTimeUtils.daysBetween(start, end);

    // Then
    assertThat(days).isEqualTo(5);
  }

  @Test
  @DisplayName("daysBetween - Debe manejar nulos")
  void daysBetween_shouldHandleNulls() {
    // Given
    LocalDate date = LocalDate.of(2024, 6, 10);

    // When & Then
    assertThat(DateTimeUtils.daysBetween(null, date)).isEqualTo(0);
    assertThat(DateTimeUtils.daysBetween(date, null)).isEqualTo(0);
    assertThat(DateTimeUtils.daysBetween(null, null)).isEqualTo(0);
  }

  @Test
  @DisplayName("secondsBetween - Debe calcular segundos entre fechas/horas")
  void secondsBetween_shouldCalculateSecondsBetweenDateTimes() {
    // Given
    LocalDateTime start = LocalDateTime.of(2024, 6, 10, 10, 0, 0);
    LocalDateTime end = LocalDateTime.of(2024, 6, 10, 10, 2, 30);

    // When
    long seconds = DateTimeUtils.secondsBetween(start, end);

    // Then
    assertThat(seconds).isEqualTo(150); // 2 minutos 30 segundos
  }

  @Test
  @DisplayName("secondsBetween - Debe manejar nulos")
  void secondsBetween_shouldHandleNulls() {
    // Given
    LocalDateTime dateTime = LocalDateTime.of(2024, 6, 10, 10, 0);

    // When & Then
    assertThat(DateTimeUtils.secondsBetween(null, dateTime)).isEqualTo(0);
    assertThat(DateTimeUtils.secondsBetween(dateTime, null)).isEqualTo(0);
    assertThat(DateTimeUtils.secondsBetween(null, null)).isEqualTo(0);
  }

  @Test
  @DisplayName("startOfDayUTC - Debe obtener inicio del día")
  void startOfDayUTC_shouldGetStartOfDay() {
    // Given
    LocalDate date = LocalDate.of(2024, 6, 10);

    // When
    LocalDateTime startOfDay = DateTimeUtils.startOfDayUTC(date);

    // Then
    assertThat(startOfDay).isNotNull();
    assertThat(startOfDay.toLocalDate()).isEqualTo(date);
    assertThat(startOfDay.getHour()).isEqualTo(0);
    assertThat(startOfDay.getMinute()).isEqualTo(0);
    assertThat(startOfDay.getSecond()).isEqualTo(0);
  }

  @Test
  @DisplayName("startOfDayUTC - Debe manejar entrada nula")
  void startOfDayUTC_shouldHandleNullInput() {
    // When & Then
    assertThat(DateTimeUtils.startOfDayUTC(null)).isNull();
  }

  @Test
  @DisplayName("endOfDayUTC - Debe obtener final del día")
  void endOfDayUTC_shouldGetEndOfDay() {
    // Given
    LocalDate date = LocalDate.of(2024, 6, 10);

    // When
    LocalDateTime endOfDay = DateTimeUtils.endOfDayUTC(date);

    // Then
    assertThat(endOfDay).isNotNull();
    assertThat(endOfDay.toLocalDate()).isEqualTo(date);
    assertThat(endOfDay.getHour()).isEqualTo(23);
    assertThat(endOfDay.getMinute()).isEqualTo(59);
    assertThat(endOfDay.getSecond()).isEqualTo(59);
  }

  @Test
  @DisplayName("endOfDayUTC - Debe manejar entrada nula")
  void endOfDayUTC_shouldHandleNullInput() {
    // When & Then
    assertThat(DateTimeUtils.endOfDayUTC(null)).isNull();
  }

  @Test
  @DisplayName("isValidDateRange - Debe validar rangos de fechas")
  void isValidDateRange_shouldValidateDateRanges() {
    // Given
    LocalDate start = LocalDate.of(2024, 6, 10);
    LocalDate end = LocalDate.of(2024, 6, 15);
    LocalDate invalidEnd = LocalDate.of(2024, 6, 5);

    // When & Then
    assertThat(DateTimeUtils.isValidDateRange(start, end)).isTrue();
    assertThat(DateTimeUtils.isValidDateRange(start, start)).isTrue(); // Mismo día válido
    assertThat(DateTimeUtils.isValidDateRange(start, invalidEnd)).isFalse();
    assertThat(DateTimeUtils.isValidDateRange(null, end)).isFalse();
    assertThat(DateTimeUtils.isValidDateRange(start, null)).isFalse();
  }

  @Test
  @DisplayName("isValidDateTimeRange - Debe validar rangos de fechas/horas")
  void isValidDateTimeRange_shouldValidateDateTimeRanges() {
    // Given
    LocalDateTime start = LocalDateTime.of(2024, 6, 10, 10, 0);
    LocalDateTime end = LocalDateTime.of(2024, 6, 10, 12, 0);
    LocalDateTime invalidEnd = LocalDateTime.of(2024, 6, 10, 9, 0);

    // When & Then
    assertThat(DateTimeUtils.isValidDateTimeRange(start, end)).isTrue();
    assertThat(DateTimeUtils.isValidDateTimeRange(start, start)).isTrue(); // Mismo momento válido
    assertThat(DateTimeUtils.isValidDateTimeRange(start, invalidEnd)).isFalse();
    assertThat(DateTimeUtils.isValidDateTimeRange(null, end)).isFalse();
    assertThat(DateTimeUtils.isValidDateTimeRange(start, null)).isFalse();
  }
}