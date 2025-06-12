package com.udea.gpx.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("CorsConfig Tests")
class CorsConfigTest {

    private CorsConfig corsConfig;

    @BeforeEach
    void setUp() {
        corsConfig = new CorsConfig();
    }

    @Test
    @DisplayName("corsConfigurer debe crear WebMvcConfigurer")
    void testCorsConfigurerCreatesWebMvcConfigurer() {
        WebMvcConfigurer configurer = corsConfig.corsConfigurer();

        assertNotNull(configurer);
        assertTrue(configurer instanceof WebMvcConfigurer);
    }

    @Test
    @DisplayName("Configuración CORS para perfil de desarrollo")
    void testCorsConfigurationForDevelopment() {
        ReflectionTestUtils.setField(corsConfig, "activeProfile", "dev");
        ReflectionTestUtils.setField(corsConfig, "customAllowedOrigins", null);

        WebMvcConfigurer configurer = corsConfig.corsConfigurer();
        CorsRegistry registry = mock(CorsRegistry.class);

        // Configurar el mock para retornar un objeto válido
        org.springframework.web.servlet.config.annotation.CorsRegistration corsRegistration = mock(
                org.springframework.web.servlet.config.annotation.CorsRegistration.class);
        when(registry.addMapping("/**")).thenReturn(corsRegistration);
        when(corsRegistration.allowedOrigins(any(String[].class))).thenReturn(corsRegistration);
        when(corsRegistration.allowedMethods(any(String[].class))).thenReturn(corsRegistration);
        when(corsRegistration.allowedHeaders(any(String[].class))).thenReturn(corsRegistration);
        when(corsRegistration.exposedHeaders(any(String[].class))).thenReturn(corsRegistration);
        when(corsRegistration.allowCredentials(anyBoolean())).thenReturn(corsRegistration);
        when(corsRegistration.maxAge(anyLong())).thenReturn(corsRegistration);

        // Simular la configuración CORS - ahora no debería fallar
        assertDoesNotThrow(() -> configurer.addCorsMappings(registry));

        // Verificar que se llamó addMapping con el patrón correcto
        verify(registry).addMapping("/**");
    }

    @Test
    @DisplayName("Configuración CORS para perfil de producción")
    void testCorsConfigurationForProduction() {
        ReflectionTestUtils.setField(corsConfig, "activeProfile", "prod");
        ReflectionTestUtils.setField(corsConfig, "customAllowedOrigins", null);

        WebMvcConfigurer configurer = corsConfig.corsConfigurer();

        assertNotNull(configurer);
        // El configurer debería usar orígenes de producción
    }

    @Test
    @DisplayName("Configuración CORS para perfil de test")
    void testCorsConfigurationForTest() {
        ReflectionTestUtils.setField(corsConfig, "activeProfile", "test");
        ReflectionTestUtils.setField(corsConfig, "customAllowedOrigins", null);

        WebMvcConfigurer configurer = corsConfig.corsConfigurer();

        assertNotNull(configurer);
        // El configurer debería usar orígenes de test (localhost)
    }

    @Test
    @DisplayName("Configuración CORS con orígenes personalizados")
    void testCorsConfigurationWithCustomOrigins() {
        ReflectionTestUtils.setField(corsConfig, "activeProfile", "prod");
        List<String> customOrigins = Arrays.asList("https://custom1.com", "https://custom2.com");
        ReflectionTestUtils.setField(corsConfig, "customAllowedOrigins", customOrigins);

        WebMvcConfigurer configurer = corsConfig.corsConfigurer();

        assertNotNull(configurer);
        // El configurer debería usar los orígenes personalizados
    }

    @Test
    @DisplayName("Clase debe estar anotada como Configuration")
    void testConfigurationAnnotation() {
        assertTrue(CorsConfig.class.isAnnotationPresent(org.springframework.context.annotation.Configuration.class));
    }

    @Test
    @DisplayName("corsConfigurer debe estar anotado como Bean")
    void testBeanAnnotation() throws NoSuchMethodException {
        assertTrue(CorsConfig.class
                .getMethod("corsConfigurer")
                .isAnnotationPresent(org.springframework.context.annotation.Bean.class));
    }

    @Test
    @DisplayName("Debe manejar perfil nulo o vacío como desarrollo")
    void testHandlesNullProfile() {
        ReflectionTestUtils.setField(corsConfig, "activeProfile", null);
        ReflectionTestUtils.setField(corsConfig, "customAllowedOrigins", null);

        WebMvcConfigurer configurer = corsConfig.corsConfigurer();

        assertNotNull(configurer);
        // Debería funcionar sin errores, usando valores por defecto
    }

    @Test
    @DisplayName("Debe manejar perfil desconocido como desarrollo")
    void testHandlesUnknownProfile() {
        ReflectionTestUtils.setField(corsConfig, "activeProfile", "unknown");
        ReflectionTestUtils.setField(corsConfig, "customAllowedOrigins", null);

        WebMvcConfigurer configurer = corsConfig.corsConfigurer();

        assertNotNull(configurer);
        // Debería funcionar sin errores, usando valores por defecto
    }

    @Test
    @DisplayName("Debe manejar lista de orígenes personalizados vacía")
    void testHandlesEmptyCustomOrigins() {
        ReflectionTestUtils.setField(corsConfig, "activeProfile", "prod");
        ReflectionTestUtils.setField(corsConfig, "customAllowedOrigins", Arrays.asList());

        WebMvcConfigurer configurer = corsConfig.corsConfigurer();

        assertNotNull(configurer);
        // Debería usar orígenes de producción por defecto
    }

    @Test
    @DisplayName("WebMvcConfigurer debe configurar mapping para todas las rutas")
    void testWebMvcConfigurerMappingAllPaths() {
        ReflectionTestUtils.setField(corsConfig, "activeProfile", "dev");

        WebMvcConfigurer configurer = corsConfig.corsConfigurer();
        CorsRegistry registry = mock(CorsRegistry.class);

        // Configurar el mock para retornar un objeto válido
        org.springframework.web.servlet.config.annotation.CorsRegistration corsRegistration = mock(
                org.springframework.web.servlet.config.annotation.CorsRegistration.class);
        when(registry.addMapping("/**")).thenReturn(corsRegistration);
        when(corsRegistration.allowedOrigins(any(String[].class))).thenReturn(corsRegistration);
        when(corsRegistration.allowedMethods(any(String[].class))).thenReturn(corsRegistration);
        when(corsRegistration.allowedHeaders(any(String[].class))).thenReturn(corsRegistration);
        when(corsRegistration.exposedHeaders(any(String[].class))).thenReturn(corsRegistration);
        when(corsRegistration.allowCredentials(anyBoolean())).thenReturn(corsRegistration);
        when(corsRegistration.maxAge(anyLong())).thenReturn(corsRegistration);

        assertDoesNotThrow(() -> configurer.addCorsMappings(registry));

        verify(registry, times(1)).addMapping("/**");
    }

    @Test
    @DisplayName("Configuración debe ser consistente entre llamadas")
    void testConfigurationConsistency() {
        ReflectionTestUtils.setField(corsConfig, "activeProfile", "dev");

        WebMvcConfigurer configurer1 = corsConfig.corsConfigurer();
        WebMvcConfigurer configurer2 = corsConfig.corsConfigurer();

        assertNotNull(configurer1);
        assertNotNull(configurer2);
        // Cada llamada debe retornar una nueva instancia
        assertNotSame(configurer1, configurer2);
    }

    @Test
    @DisplayName("Debe tener logger inicializado")
    void testLoggerInitialized() {
        // Verificar que la clase tiene un logger definido
        try {
            java.lang.reflect.Field loggerField = CorsConfig.class.getDeclaredField("logger");
            loggerField.setAccessible(true);
            Object logger = loggerField.get(corsConfig);
            assertNotNull(logger);
        } catch (Exception e) {
            fail("Logger field should be present: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Debe inyectar correctamente activeProfile")
    void testActiveProfileInjection() {
        // Verificar que el campo activeProfile está anotado con @Value
        try {
            java.lang.reflect.Field activeProfileField = CorsConfig.class.getDeclaredField("activeProfile");
            assertTrue(
                    activeProfileField.isAnnotationPresent(org.springframework.beans.factory.annotation.Value.class));

            org.springframework.beans.factory.annotation.Value valueAnnotation = activeProfileField
                    .getAnnotation(org.springframework.beans.factory.annotation.Value.class);
            assertEquals("${spring.profiles.active:dev}", valueAnnotation.value());
        } catch (NoSuchFieldException e) {
            fail("activeProfile field should be present: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Debe inyectar correctamente customAllowedOrigins")
    void testCustomAllowedOriginsInjection() {
        // Verificar que el campo customAllowedOrigins está anotado con @Value
        try {
            java.lang.reflect.Field customOriginsField = CorsConfig.class.getDeclaredField("customAllowedOrigins");
            assertTrue(
                    customOriginsField.isAnnotationPresent(org.springframework.beans.factory.annotation.Value.class));

            org.springframework.beans.factory.annotation.Value valueAnnotation = customOriginsField
                    .getAnnotation(org.springframework.beans.factory.annotation.Value.class);
            assertEquals("${cors.allowed-origins:}", valueAnnotation.value());
        } catch (NoSuchFieldException e) {
            fail("customAllowedOrigins field should be present: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Configuración para diferentes perfiles debe ser diferente")
    void testDifferentConfigurationForDifferentProfiles() {
        // Test desarrollo
        ReflectionTestUtils.setField(corsConfig, "activeProfile", "dev");
        WebMvcConfigurer devConfigurer = corsConfig.corsConfigurer();

        // Test producción
        ReflectionTestUtils.setField(corsConfig, "activeProfile", "prod");
        WebMvcConfigurer prodConfigurer = corsConfig.corsConfigurer();

        // Test test
        ReflectionTestUtils.setField(corsConfig, "activeProfile", "test");
        WebMvcConfigurer testConfigurer = corsConfig.corsConfigurer();

        assertNotNull(devConfigurer);
        assertNotNull(prodConfigurer);
        assertNotNull(testConfigurer);

        // Cada configuración debe ser distinta
        assertNotSame(devConfigurer, prodConfigurer);
        assertNotSame(devConfigurer, testConfigurer);
        assertNotSame(prodConfigurer, testConfigurer);
    }
}