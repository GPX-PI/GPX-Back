package com.udea.gpx.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
}