package com.udea.gpx.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DateTimeConfig Tests")
class DateTimeConfigTest {

    private DateTimeConfig dateTimeConfig;

    @BeforeEach
    void setUp() {
        dateTimeConfig = new DateTimeConfig();
    }

    @Test
    @DisplayName("DateTimeConfig debe estar anotado como Configuration")
    void testDateTimeConfigIsConfiguration() {
        assertTrue(DateTimeConfig.class.isAnnotationPresent(
                org.springframework.context.annotation.Configuration.class));
    }

    @Test
    @DisplayName("DateTimeConfig debe poder ser instanciado")
    void testDateTimeConfigCanBeInstantiated() {
        assertNotNull(dateTimeConfig);
    }

    @Test
    @DisplayName("configureTimezone debe establecer la zona horaria a UTC")
    void testConfigureTimezone() {
        dateTimeConfig.configureTimezone();
        assertEquals("UTC", TimeZone.getDefault().getID());
    }

    @Test
    @DisplayName("objectMapper debe crear un mapper configurado correctamente")
    void testObjectMapperCreation() {
        ObjectMapper mapper = dateTimeConfig.objectMapper();
        assertNotNull(mapper);
        assertFalse(mapper.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));
    }

    @Test
    @DisplayName("objectMapper debe serializar LocalDate correctamente")
    void testLocalDateSerialization() throws IOException {
        ObjectMapper mapper = dateTimeConfig.objectMapper();
        LocalDate date = LocalDate.of(2025, Month.JUNE, 13);
        String json = mapper.writeValueAsString(date);
        assertEquals("\"2025-06-13\"", json);
    }

    @Test
    @DisplayName("objectMapper debe serializar Duration correctamente")
    void testDurationSerialization() throws IOException {
        ObjectMapper mapper = dateTimeConfig.objectMapper();
        Duration duration = Duration.ofHours(2).plusMinutes(30);
        String json = mapper.writeValueAsString(duration);
        assertEquals("\"PT2H30M\"", json);
    }

    @Test
    @DisplayName("objectMapper debe deserializar Duration correctamente")
    void testDurationDeserialization() throws IOException {
        ObjectMapper mapper = dateTimeConfig.objectMapper();
        String json = "\"PT2H30M\"";
        Duration duration = mapper.readValue(json, Duration.class);
        assertEquals(Duration.ofHours(2).plusMinutes(30), duration);
    }

    @Test
    @DisplayName("objectMapper debe manejar Duration nula o vacía")
    void testNullDuration() throws IOException {
        ObjectMapper mapper = dateTimeConfig.objectMapper();
        String jsonNull = "null";
        Duration nullDuration = mapper.readValue(jsonNull, Duration.class);
        assertNull(nullDuration);

        String jsonEmpty = "\"\"";
        Duration emptyDuration = mapper.readValue(jsonEmpty, Duration.class);
        assertNull(emptyDuration);
    }

    @Test
    @DisplayName("objectMapper debe manejar Duration inválida")
    void testInvalidDuration() throws IOException {
        ObjectMapper mapper = dateTimeConfig.objectMapper();
        String jsonInvalid = "\"invalid\"";
        Duration invalidDuration = mapper.readValue(jsonInvalid, Duration.class);
        assertEquals(Duration.ZERO, invalidDuration);
    }
}