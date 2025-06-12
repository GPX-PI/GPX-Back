package com.udea.gpx.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("WebConfig Tests")
class WebConfigTest {

    @Test
    @DisplayName("WebConfig debe estar anotado como Configuration")
    void testWebConfigIsConfiguration() {
        assertTrue(WebConfig.class.isAnnotationPresent(
                org.springframework.context.annotation.Configuration.class));
    }

    @Test
    @DisplayName("WebConfig debe poder ser instanciado")
    void testWebConfigCanBeInstantiated() {
        assertDoesNotThrow(() -> new WebConfig());
    }
}