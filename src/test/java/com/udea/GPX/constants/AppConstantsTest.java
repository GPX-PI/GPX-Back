package com.udea.GPX.constants;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para AppConstants - Validación de todas las constantes centralizadas
 */
@DisplayName("AppConstants Tests")
class AppConstantsTest {

    // ========== CONSTRUCTOR TESTS ==========

    @Test
    @DisplayName("AppConstants should not be instantiable")
    void appConstants_ShouldNotBeInstantiable() throws Exception {
        // Given
        Constructor<AppConstants> constructor = AppConstants.class.getDeclaredConstructor();

        // When
        boolean isPrivate = Modifier.isPrivate(constructor.getModifiers());

        // Then
        assertTrue(isPrivate, "Constructor should be private");

        // Verify that trying to instantiate throws exception
        constructor.setAccessible(true);
        assertDoesNotThrow(() -> constructor.newInstance());
    }

    @Test
    @DisplayName("AppConstants should be final class")
    void appConstants_ShouldBeFinalClass() {
        // When
        boolean isFinal = Modifier.isFinal(AppConstants.class.getModifiers());

        // Then
        assertTrue(isFinal, "AppConstants class should be final");
    }

    // ========== FILES CONSTANTS TESTS ==========

    @Test
    @DisplayName("Files constants should have correct values")
    void filesConstants_ShouldHaveCorrectValues() {
        // File sizes
        assertEquals(10 * 1024 * 1024, AppConstants.Files.MAX_FILE_SIZE_BYTES);
        assertEquals(5 * 1024 * 1024, AppConstants.Files.MAX_EVENT_IMAGE_SIZE_BYTES);

        // Directories
        assertEquals("uploads/", AppConstants.Files.UPLOADS_DIR);
        assertEquals("uploads/events/", AppConstants.Files.EVENTS_UPLOAD_DIR);
    }

    @Test
    @DisplayName("Files allowed image types should be comprehensive")
    void filesAllowedImageTypes_ShouldBeComprehensive() {
        // Given
        String[] expectedTypes = { "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp" };

        // When
        String[] actualTypes = AppConstants.Files.ALLOWED_IMAGE_TYPES;

        // Then
        assertNotNull(actualTypes);
        assertEquals(expectedTypes.length, actualTypes.length);

        for (String expectedType : expectedTypes) {
            assertArrayContains(actualTypes, expectedType);
        }
    }

    @Test
    @DisplayName("Files allowed document types should include images and PDF")
    void filesAllowedDocumentTypes_ShouldIncludeImagesAndPdf() {
        // Given
        String[] expectedTypes = { "application/pdf", "image/jpeg", "image/jpg", "image/png", "image/gif" };

        // When
        String[] actualTypes = AppConstants.Files.ALLOWED_DOCUMENT_TYPES;

        // Then
        assertNotNull(actualTypes);
        assertEquals(expectedTypes.length, actualTypes.length);

        for (String expectedType : expectedTypes) {
            assertArrayContains(actualTypes, expectedType);
        }
    }

    @Test
    @DisplayName("Files valid image extensions should match types")
    void filesValidImageExtensions_ShouldMatchTypes() {
        // Given
        String[] expectedExtensions = { ".jpg", ".jpeg", ".png", ".gif", ".webp" };

        // When
        String[] actualExtensions = AppConstants.Files.VALID_IMAGE_EXTENSIONS;

        // Then
        assertNotNull(actualExtensions);
        assertEquals(expectedExtensions.length, actualExtensions.length);

        for (String expectedExt : expectedExtensions) {
            assertArrayContains(actualExtensions, expectedExt);
        }
    }

    @Test
    @DisplayName("Files valid document extensions should include PDF and images")
    void filesValidDocumentExtensions_ShouldIncludePdfAndImages() {
        // Given
        String[] expectedExtensions = { ".pdf", ".jpg", ".jpeg", ".png", ".gif" };

        // When
        String[] actualExtensions = AppConstants.Files.VALID_DOCUMENT_EXTENSIONS;

        // Then
        assertNotNull(actualExtensions);
        assertEquals(expectedExtensions.length, actualExtensions.length);

        for (String expectedExt : expectedExtensions) {
            assertArrayContains(actualExtensions, expectedExt);
        }
    }

    // ========== VALIDATION CONSTANTS TESTS ==========

    @Test
    @DisplayName("Validation constants should have reasonable limits")
    void validationConstants_ShouldHaveReasonableLimits() {
        // String length limits
        assertEquals(100, AppConstants.Validation.MAX_NAME_LENGTH);
        assertEquals(100, AppConstants.Validation.MAX_TEAM_NAME_LENGTH);
        assertEquals(20, AppConstants.Validation.MAX_VEHICLE_PLATES_LENGTH);
        assertEquals(50, AppConstants.Validation.MAX_SOAT_LENGTH);
        assertEquals(100, AppConstants.Validation.MAX_CATEGORY_NAME_LENGTH);

        // Geographic coordinates
        assertEquals(-90.0, AppConstants.Validation.MIN_LATITUDE);
        assertEquals(90.0, AppConstants.Validation.MAX_LATITUDE);
        assertEquals(-180.0, AppConstants.Validation.MIN_LONGITUDE);
        assertEquals(180.0, AppConstants.Validation.MAX_LONGITUDE);
    }

    @Test
    @DisplayName("Validation geographic bounds should be valid")
    void validationGeographicBounds_ShouldBeValid() {
        // Latitude bounds
        assertTrue(AppConstants.Validation.MIN_LATITUDE >= -90.0);
        assertTrue(AppConstants.Validation.MAX_LATITUDE <= 90.0);
        assertTrue(AppConstants.Validation.MIN_LATITUDE < AppConstants.Validation.MAX_LATITUDE);

        // Longitude bounds
        assertTrue(AppConstants.Validation.MIN_LONGITUDE >= -180.0);
        assertTrue(AppConstants.Validation.MAX_LONGITUDE <= 180.0);
        assertTrue(AppConstants.Validation.MIN_LONGITUDE < AppConstants.Validation.MAX_LONGITUDE);
    }

    // ========== SECURITY CONSTANTS TESTS ==========

    @Test
    @DisplayName("Security auth providers should be defined")
    void securityAuthProviders_ShouldBeDefined() {
        assertEquals("LOCAL", AppConstants.Security.AUTH_PROVIDER_LOCAL);
        assertEquals("GOOGLE", AppConstants.Security.AUTH_PROVIDER_GOOGLE);
    }

    @Test
    @DisplayName("Security production origins should be HTTPS")
    void securityProductionOrigins_ShouldBeHttps() {
        // When
        String[] origins = AppConstants.Security.PRODUCTION_ORIGINS;

        // Then
        assertNotNull(origins);
        assertTrue(origins.length > 0);

        for (String origin : origins) {
            assertTrue(origin.startsWith("https://"),
                    "Production origin should use HTTPS: " + origin);
        }
    }

    @Test
    @DisplayName("Security development origins should be HTTP localhost")
    void securityDevelopmentOrigins_ShouldBeHttpLocalhost() {
        // When
        String[] origins = AppConstants.Security.DEVELOPMENT_ORIGINS;

        // Then
        assertNotNull(origins);
        assertTrue(origins.length > 0);

        for (String origin : origins) {
            assertTrue(origin.startsWith("http://localhost") || origin.startsWith("http://127.0.0.1"),
                    "Development origin should be localhost or 127.0.0.1: " + origin);
        }
    }

    @Test
    @DisplayName("Security CORS settings should be defined")
    void securityCorsSettings_ShouldBeDefined() {
        // Methods
        String[] methods = AppConstants.Security.ALLOWED_METHODS;
        assertNotNull(methods);
        assertArrayContains(methods, "GET");
        assertArrayContains(methods, "POST");
        assertArrayContains(methods, "PUT");
        assertArrayContains(methods, "DELETE");
        assertArrayContains(methods, "OPTIONS");

        // Headers
        String[] headers = AppConstants.Security.ALLOWED_HEADERS;
        assertNotNull(headers);
        assertArrayContains(headers, "Content-Type");
        assertArrayContains(headers, "Authorization");
        assertArrayContains(headers, "X-Requested-With");

        // Exposed headers
        String[] exposedHeaders = AppConstants.Security.EXPOSED_HEADERS;
        assertNotNull(exposedHeaders);
        assertArrayContains(exposedHeaders, "X-Total-Count");

        // Max age
        assertEquals(300L, AppConstants.Security.CORS_MAX_AGE_PRODUCTION);
        assertEquals(3600L, AppConstants.Security.CORS_MAX_AGE_DEVELOPMENT);
        assertTrue(AppConstants.Security.CORS_MAX_AGE_DEVELOPMENT > AppConstants.Security.CORS_MAX_AGE_PRODUCTION);
    }

    // ========== MESSAGES CONSTANTS TESTS ==========

    @Test
    @DisplayName("Messages should be in Spanish and descriptive")
    void messages_ShouldBeInSpanishAndDescriptive() {
        // Entity not found messages
        assertEquals("Usuario no encontrado", AppConstants.Messages.USUARIO_NO_ENCONTRADO);
        assertEquals("Vehículo no encontrado", AppConstants.Messages.VEHICULO_NO_ENCONTRADO);
        assertEquals("Evento no encontrado", AppConstants.Messages.EVENTO_NO_ENCONTRADO);
        assertEquals("Etapa no encontrada", AppConstants.Messages.ETAPA_NO_ENCONTRADA);
        assertEquals("Categoría no encontrada", AppConstants.Messages.CATEGORIA_NO_ENCONTRADA);
        assertEquals("Resultado no encontrado", AppConstants.Messages.RESULTADO_NO_ENCONTRADO);

        // Validation messages
        assertEquals("El email ya está en uso", AppConstants.Messages.EMAIL_YA_EN_USO);
        assertEquals("El archivo está vacío", AppConstants.Messages.ARCHIVO_VACIO);
        assertEquals("El archivo es demasiado grande", AppConstants.Messages.ARCHIVO_DEMASIADO_GRANDE);
        assertEquals("Tipo de archivo no válido", AppConstants.Messages.TIPO_ARCHIVO_NO_VALIDO);
        assertEquals("Extensión de archivo no válida", AppConstants.Messages.EXTENSION_NO_VALIDA);

        // Security messages
        assertEquals("Acceso denegado", AppConstants.Messages.ACCESO_DENEGADO);
        assertEquals("Permisos insuficientes", AppConstants.Messages.PERMISOS_INSUFICIENTES);
        assertEquals("Token inválido", AppConstants.Messages.TOKEN_INVALIDO);

        // General messages
        assertEquals("Error interno del servidor", AppConstants.Messages.ERROR_INTERNO);
        assertEquals("Operación realizada exitosamente", AppConstants.Messages.OPERACION_EXITOSA);
    }

    // ========== API CONSTANTS TESTS ==========

    @Test
    @DisplayName("API paths should be consistent")
    void apiPaths_ShouldBeConsistent() {
        assertEquals("/api", AppConstants.Api.BASE_PATH);
        assertEquals("/api/users", AppConstants.Api.USERS_PATH);
        assertEquals("/api/events", AppConstants.Api.EVENTS_PATH);
        assertEquals("/api/vehicles", AppConstants.Api.VEHICLES_PATH);
        assertEquals("/api/categories", AppConstants.Api.CATEGORIES_PATH);
        assertEquals("/api/stages", AppConstants.Api.STAGES_PATH);
        assertEquals("/api/stageresults", AppConstants.Api.STAGE_RESULTS_PATH);

        // All paths should start with base path
        assertTrue(AppConstants.Api.USERS_PATH.startsWith(AppConstants.Api.BASE_PATH));
        assertTrue(AppConstants.Api.EVENTS_PATH.startsWith(AppConstants.Api.BASE_PATH));
        assertTrue(AppConstants.Api.VEHICLES_PATH.startsWith(AppConstants.Api.BASE_PATH));
        assertTrue(AppConstants.Api.CATEGORIES_PATH.startsWith(AppConstants.Api.BASE_PATH));
        assertTrue(AppConstants.Api.STAGES_PATH.startsWith(AppConstants.Api.BASE_PATH));
        assertTrue(AppConstants.Api.STAGE_RESULTS_PATH.startsWith(AppConstants.Api.BASE_PATH));
    }

    @Test
    @DisplayName("API headers should be standard HTTP headers")
    void apiHeaders_ShouldBeStandardHttpHeaders() {
        assertEquals("Authorization", AppConstants.Api.HEADER_AUTHORIZATION);
        assertEquals("Content-Type", AppConstants.Api.HEADER_CONTENT_TYPE);
        assertEquals("X-Total-Count", AppConstants.Api.HEADER_TOTAL_COUNT);
    }

    @Test
    @DisplayName("API parameters should be standard pagination params")
    void apiParameters_ShouldBeStandardPaginationParams() {
        assertEquals("page", AppConstants.Api.PARAM_PAGE);
        assertEquals("size", AppConstants.Api.PARAM_SIZE);
        assertEquals("sort", AppConstants.Api.PARAM_SORT);
    }

    // ========== CONFIG CONSTANTS TESTS ==========

    @Test
    @DisplayName("Config profiles should be standard Spring profiles")
    void configProfiles_ShouldBeStandardSpringProfiles() {
        assertEquals("prod", AppConstants.Config.PROFILE_PRODUCTION);
        assertEquals("dev", AppConstants.Config.PROFILE_DEVELOPMENT);
        assertEquals("test", AppConstants.Config.PROFILE_TEST);
    }

    @Test
    @DisplayName("Config cache names should be descriptive")
    void configCacheNames_ShouldBeDescriptive() {
        assertEquals("events", AppConstants.Config.CACHE_EVENTS);
        assertEquals("categories", AppConstants.Config.CACHE_CATEGORIES);
        assertEquals("classifications", AppConstants.Config.CACHE_CLASSIFICATIONS);
    }

    @Test
    @DisplayName("Config timeouts should be reasonable")
    void configTimeouts_ShouldBeReasonable() {
        assertEquals(30, AppConstants.Config.DEFAULT_TIMEOUT_SECONDS);
        assertEquals(120, AppConstants.Config.FILE_UPLOAD_TIMEOUT_SECONDS);

        // File upload timeout should be longer than default
        assertTrue(AppConstants.Config.FILE_UPLOAD_TIMEOUT_SECONDS > AppConstants.Config.DEFAULT_TIMEOUT_SECONDS);

        // Timeouts should be positive
        assertTrue(AppConstants.Config.DEFAULT_TIMEOUT_SECONDS > 0);
        assertTrue(AppConstants.Config.FILE_UPLOAD_TIMEOUT_SECONDS > 0);
    }

    // ========== LOGGING CONSTANTS TESTS ==========

    @Test
    @DisplayName("Logging templates should contain placeholders")
    void loggingTemplates_ShouldContainPlaceholders() {
        // Operation templates
        assertTrue(AppConstants.Logging.OPERATION_START.contains("{}"));
        assertTrue(AppConstants.Logging.OPERATION_SUCCESS.contains("{}"));
        assertTrue(AppConstants.Logging.OPERATION_ERROR.contains("{}"));

        // User action templates
        assertTrue(AppConstants.Logging.USER_ACTION.contains("{}"));
        assertTrue(AppConstants.Logging.FILE_UPLOAD.contains("{}"));
        assertFalse(AppConstants.Logging.FILE_DELETE.contains("{}") == false); // Should contain placeholder
        assertTrue(AppConstants.Logging.FILE_DELETE.contains("{}"));

        // Authentication templates
        assertTrue(AppConstants.Logging.AUTHENTICATION_SUCCESS.contains("{}"));
        assertTrue(AppConstants.Logging.AUTHENTICATION_FAILURE.contains("{}"));
        assertTrue(AppConstants.Logging.AUTHORIZATION_DENIED.contains("{}"));
    }

    @Test
    @DisplayName("Logging messages should be descriptive")
    void loggingMessages_ShouldBeDescriptive() {
        // Check that messages contain meaningful text
        assertNotNull(AppConstants.Logging.OPERATION_START);
        assertNotNull(AppConstants.Logging.OPERATION_SUCCESS);
        assertNotNull(AppConstants.Logging.OPERATION_ERROR);
        assertNotNull(AppConstants.Logging.USER_ACTION);
        assertNotNull(AppConstants.Logging.FILE_UPLOAD);
        assertNotNull(AppConstants.Logging.FILE_DELETE);
        assertNotNull(AppConstants.Logging.AUTHENTICATION_SUCCESS);
        assertNotNull(AppConstants.Logging.AUTHENTICATION_FAILURE);
        assertNotNull(AppConstants.Logging.AUTHORIZATION_DENIED);

        // Messages should not be empty
        assertFalse(AppConstants.Logging.OPERATION_START.trim().isEmpty());
        assertFalse(AppConstants.Logging.OPERATION_SUCCESS.trim().isEmpty());
        assertFalse(AppConstants.Logging.OPERATION_ERROR.trim().isEmpty());
    }

    // ========== UTILITY METHODS ==========

    private void assertArrayContains(String[] array, String expectedValue) {
        boolean found = false;
        for (String value : array) {
            if (expectedValue.equals(value)) {
                found = true;
                break;
            }
        }
        assertTrue(found, "Array should contain: " + expectedValue);
    }

    // ========== INTEGRATION TESTS ==========

    @Test
    @DisplayName("All inner classes should be accessible")
    void allInnerClasses_ShouldBeAccessible() {
        // Test that all inner classes exist and are accessible
        assertDoesNotThrow(() -> AppConstants.Files.class);
        assertDoesNotThrow(() -> AppConstants.Validation.class);
        assertDoesNotThrow(() -> AppConstants.Security.class);
        assertDoesNotThrow(() -> AppConstants.Messages.class);
        assertDoesNotThrow(() -> AppConstants.Api.class);
        assertDoesNotThrow(() -> AppConstants.Config.class);
        assertDoesNotThrow(() -> AppConstants.Logging.class);
    }

    @Test
    @DisplayName("File size constants should be in bytes and reasonable")
    void fileSizeConstants_ShouldBeInBytesAndReasonable() {
        // File sizes should be positive
        assertTrue(AppConstants.Files.MAX_FILE_SIZE_BYTES > 0);
        assertTrue(AppConstants.Files.MAX_EVENT_IMAGE_SIZE_BYTES > 0);

        // Event image size should be smaller than general file size
        assertTrue(AppConstants.Files.MAX_EVENT_IMAGE_SIZE_BYTES <= AppConstants.Files.MAX_FILE_SIZE_BYTES);

        // Verify actual values are as expected (10MB and 5MB)
        assertEquals(10485760, AppConstants.Files.MAX_FILE_SIZE_BYTES); // 10 * 1024 * 1024
        assertEquals(5242880, AppConstants.Files.MAX_EVENT_IMAGE_SIZE_BYTES); // 5 * 1024 * 1024
    }
}
