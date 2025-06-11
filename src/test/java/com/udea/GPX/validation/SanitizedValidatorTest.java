package com.udea.gpx.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.udea.gpx.util.InputSanitizer;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("SanitizedValidator Tests")
class SanitizedValidatorTest {

    private SanitizedValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @Mock
    private Sanitized annotation;

    @BeforeEach
    void setUp() {
        validator = new SanitizedValidator();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addConstraintViolation()).thenReturn(context);
    }

    // ========== INITIALIZATION TESTS ==========

    @Test
    @DisplayName("initialize should set validation type from annotation")
    void initialize_ShouldSetValidationType() {
        // Given
        when(annotation.value()).thenReturn(Sanitized.SanitizationType.TEXT);
        when(annotation.allowNull()).thenReturn(false);

        // When
        validator.initialize(annotation);

        // Then - verify no exception is thrown and validator is ready
        assertDoesNotThrow(() -> validator.isValid("test", context));
    }

    @Test
    @DisplayName("initialize should handle all sanitization types")
    void initialize_ShouldHandleAllSanitizationTypes() {
        // Test TEXT type
        when(annotation.value()).thenReturn(Sanitized.SanitizationType.TEXT);
        when(annotation.allowNull()).thenReturn(false);
        assertDoesNotThrow(() -> validator.initialize(annotation));

        // Test EMAIL type
        when(annotation.value()).thenReturn(Sanitized.SanitizationType.EMAIL);
        assertDoesNotThrow(() -> validator.initialize(annotation));

        // Test NAME type
        when(annotation.value()).thenReturn(Sanitized.SanitizationType.NAME);
        assertDoesNotThrow(() -> validator.initialize(annotation));

        // Test URL type
        when(annotation.value()).thenReturn(Sanitized.SanitizationType.URL);
        assertDoesNotThrow(() -> validator.initialize(annotation));

        // Test PHONE type
        when(annotation.value()).thenReturn(Sanitized.SanitizationType.PHONE);
        assertDoesNotThrow(() -> validator.initialize(annotation));

        // Test IDENTIFICATION type
        when(annotation.value()).thenReturn(Sanitized.SanitizationType.IDENTIFICATION);
        assertDoesNotThrow(() -> validator.initialize(annotation));
    }

    // ========== NULL VALUE TESTS ==========

    @Test
    @DisplayName("isValid should return true for null when allowNull is true")
    void isValid_WithNullAndAllowNull_ShouldReturnTrue() {
        // Given
        when(annotation.value()).thenReturn(Sanitized.SanitizationType.TEXT);
        when(annotation.allowNull()).thenReturn(true);
        validator.initialize(annotation);

        // When
        boolean result = validator.isValid(null, context);

        // Then
        assertTrue(result);
        verify(context, never()).buildConstraintViolationWithTemplate(anyString());
    }

    @Test
    @DisplayName("isValid should return false for null when allowNull is false")
    void isValid_WithNullAndNotAllowNull_ShouldReturnFalse() {
        // Given
        when(annotation.value()).thenReturn(Sanitized.SanitizationType.TEXT);
        when(annotation.allowNull()).thenReturn(false);
        validator.initialize(annotation);

        // When
        boolean result = validator.isValid(null, context);

        // Then
        assertFalse(result);
    }

    // ========== TEXT SANITIZATION TESTS ==========

    @Test
    @DisplayName("isValid should validate clean text successfully")
    void isValid_WithCleanText_ShouldReturnTrue() {
        // Given
        when(annotation.value()).thenReturn(Sanitized.SanitizationType.TEXT);
        when(annotation.allowNull()).thenReturn(false);
        validator.initialize(annotation);

        try (MockedStatic<InputSanitizer> mockedSanitizer = mockStatic(InputSanitizer.class)) {
            mockedSanitizer.when(() -> InputSanitizer.sanitizeText("clean text"))
                    .thenReturn("clean text");

            // When
            boolean result = validator.isValid("clean text", context);

            // Then
            assertTrue(result);
            verify(context, never()).buildConstraintViolationWithTemplate(anyString());
        }
    }

    @Test
    @DisplayName("isValid should handle text sanitization exceptions")
    void isValid_WithMaliciousText_ShouldReturnFalse() {
        // Given
        when(annotation.value()).thenReturn(Sanitized.SanitizationType.TEXT);
        when(annotation.allowNull()).thenReturn(false);
        validator.initialize(annotation);

        try (MockedStatic<InputSanitizer> mockedSanitizer = mockStatic(InputSanitizer.class)) {
            mockedSanitizer.when(() -> InputSanitizer.sanitizeText("<script>alert('xss')</script>"))
                    .thenThrow(new IllegalArgumentException("Input contiene patrones sospechosos de XSS"));

            // When
            boolean result = validator.isValid("<script>alert('xss')</script>", context);

            // Then
            assertFalse(result);
            verify(context).disableDefaultConstraintViolation();
            verify(context).buildConstraintViolationWithTemplate("Input contiene patrones sospechosos de XSS");
        }
    }

    // ========== EMAIL SANITIZATION TESTS ==========

    @Test
    @DisplayName("isValid should validate clean email successfully")
    void isValid_WithCleanEmail_ShouldReturnTrue() {
        // Given
        when(annotation.value()).thenReturn(Sanitized.SanitizationType.EMAIL);
        when(annotation.allowNull()).thenReturn(false);
        validator.initialize(annotation);

        try (MockedStatic<InputSanitizer> mockedSanitizer = mockStatic(InputSanitizer.class)) {
            mockedSanitizer.when(() -> InputSanitizer.sanitizeEmail("test@example.com"))
                    .thenReturn("test@example.com");

            // When
            boolean result = validator.isValid("test@example.com", context);

            // Then
            assertTrue(result);
            verify(context, never()).buildConstraintViolationWithTemplate(anyString());
        }
    }

    @Test
    @DisplayName("isValid should handle email sanitization exceptions")
    void isValid_WithInvalidEmail_ShouldReturnFalse() {
        // Given
        when(annotation.value()).thenReturn(Sanitized.SanitizationType.EMAIL);
        when(annotation.allowNull()).thenReturn(false);
        validator.initialize(annotation);

        try (MockedStatic<InputSanitizer> mockedSanitizer = mockStatic(InputSanitizer.class)) {
            mockedSanitizer.when(() -> InputSanitizer.sanitizeEmail("invalid-email"))
                    .thenThrow(new IllegalArgumentException("Formato de email inválido"));

            // When
            boolean result = validator.isValid("invalid-email", context);

            // Then
            assertFalse(result);
            verify(context).disableDefaultConstraintViolation();
            verify(context).buildConstraintViolationWithTemplate("Formato de email inválido");
        }
    }

    // ========== NAME SANITIZATION TESTS ==========

    @Test
    @DisplayName("isValid should validate clean name successfully")
    void isValid_WithCleanName_ShouldReturnTrue() {
        // Given
        when(annotation.value()).thenReturn(Sanitized.SanitizationType.NAME);
        when(annotation.allowNull()).thenReturn(false);
        validator.initialize(annotation);

        try (MockedStatic<InputSanitizer> mockedSanitizer = mockStatic(InputSanitizer.class)) {
            mockedSanitizer.when(() -> InputSanitizer.sanitizeName("Juan Carlos"))
                    .thenReturn("Juan Carlos");

            // When
            boolean result = validator.isValid("Juan Carlos", context);

            // Then
            assertTrue(result);
            verify(context, never()).buildConstraintViolationWithTemplate(anyString());
        }
    }

    @Test
    @DisplayName("isValid should handle name with invalid characters")
    void isValid_WithInvalidName_ShouldReturnFalse() {
        // Given
        when(annotation.value()).thenReturn(Sanitized.SanitizationType.NAME);
        when(annotation.allowNull()).thenReturn(false);
        validator.initialize(annotation);

        try (MockedStatic<InputSanitizer> mockedSanitizer = mockStatic(InputSanitizer.class)) {
            mockedSanitizer.when(() -> InputSanitizer.sanitizeName("Juan123@"))
                    .thenThrow(new IllegalArgumentException("El nombre contiene caracteres no válidos"));

            // When
            boolean result = validator.isValid("Juan123@", context);

            // Then
            assertFalse(result);
            verify(context).disableDefaultConstraintViolation();
            verify(context).buildConstraintViolationWithTemplate("El nombre contiene caracteres no válidos");
        }
    }

    // ========== URL SANITIZATION TESTS ==========

    @Test
    @DisplayName("isValid should validate clean URL successfully")
    void isValid_WithCleanUrl_ShouldReturnTrue() {
        // Given
        when(annotation.value()).thenReturn(Sanitized.SanitizationType.URL);
        when(annotation.allowNull()).thenReturn(false);
        validator.initialize(annotation);

        try (MockedStatic<InputSanitizer> mockedSanitizer = mockStatic(InputSanitizer.class)) {
            mockedSanitizer.when(() -> InputSanitizer.sanitizeUrl("https://example.com"))
                    .thenReturn("https://example.com");

            // When
            boolean result = validator.isValid("https://example.com", context);

            // Then
            assertTrue(result);
            verify(context, never()).buildConstraintViolationWithTemplate(anyString());
        }
    }

    @Test
    @DisplayName("isValid should handle dangerous URL patterns")
    void isValid_WithDangerousUrl_ShouldReturnFalse() {
        // Given
        when(annotation.value()).thenReturn(Sanitized.SanitizationType.URL);
        when(annotation.allowNull()).thenReturn(false);
        validator.initialize(annotation);

        try (MockedStatic<InputSanitizer> mockedSanitizer = mockStatic(InputSanitizer.class)) {
            mockedSanitizer.when(() -> InputSanitizer.sanitizeUrl("javascript:alert('xss')"))
                    .thenThrow(new IllegalArgumentException("URL contiene esquema peligroso"));

            // When
            boolean result = validator.isValid("javascript:alert('xss')", context);

            // Then
            assertFalse(result);
            verify(context).disableDefaultConstraintViolation();
            verify(context).buildConstraintViolationWithTemplate("URL contiene esquema peligroso");
        }
    }

    // ========== PHONE SANITIZATION TESTS ==========

    @Test
    @DisplayName("isValid should validate clean phone successfully")
    void isValid_WithCleanPhone_ShouldReturnTrue() {
        // Given
        when(annotation.value()).thenReturn(Sanitized.SanitizationType.PHONE);
        when(annotation.allowNull()).thenReturn(false);
        validator.initialize(annotation);

        try (MockedStatic<InputSanitizer> mockedSanitizer = mockStatic(InputSanitizer.class)) {
            mockedSanitizer.when(() -> InputSanitizer.sanitizePhone("3001234567"))
                    .thenReturn("3001234567");

            // When
            boolean result = validator.isValid("3001234567", context);

            // Then
            assertTrue(result);
            verify(context, never()).buildConstraintViolationWithTemplate(anyString());
        }
    }

    @Test
    @DisplayName("isValid should handle invalid phone patterns")
    void isValid_WithInvalidPhone_ShouldReturnFalse() {
        // Given
        when(annotation.value()).thenReturn(Sanitized.SanitizationType.PHONE);
        when(annotation.allowNull()).thenReturn(false);
        validator.initialize(annotation);

        try (MockedStatic<InputSanitizer> mockedSanitizer = mockStatic(InputSanitizer.class)) {
            mockedSanitizer.when(() -> InputSanitizer.sanitizePhone("123abc"))
                    .thenThrow(new IllegalArgumentException("Formato de teléfono inválido"));

            // When
            boolean result = validator.isValid("123abc", context);

            // Then
            assertFalse(result);
            verify(context).disableDefaultConstraintViolation();
            verify(context).buildConstraintViolationWithTemplate("Formato de teléfono inválido");
        }
    }

    // ========== IDENTIFICATION SANITIZATION TESTS ==========

    @Test
    @DisplayName("isValid should validate clean identification successfully")
    void isValid_WithCleanIdentification_ShouldReturnTrue() {
        // Given
        when(annotation.value()).thenReturn(Sanitized.SanitizationType.IDENTIFICATION);
        when(annotation.allowNull()).thenReturn(false);
        validator.initialize(annotation);

        try (MockedStatic<InputSanitizer> mockedSanitizer = mockStatic(InputSanitizer.class)) {
            mockedSanitizer.when(() -> InputSanitizer.sanitizeIdentification("12345678"))
                    .thenReturn("12345678");

            // When
            boolean result = validator.isValid("12345678", context);

            // Then
            assertTrue(result);
            verify(context, never()).buildConstraintViolationWithTemplate(anyString());
        }
    }

    @Test
    @DisplayName("isValid should handle invalid identification patterns")
    void isValid_WithInvalidIdentification_ShouldReturnFalse() {
        // Given
        when(annotation.value()).thenReturn(Sanitized.SanitizationType.IDENTIFICATION);
        when(annotation.allowNull()).thenReturn(false);
        validator.initialize(annotation);

        try (MockedStatic<InputSanitizer> mockedSanitizer = mockStatic(InputSanitizer.class)) {
            mockedSanitizer.when(() -> InputSanitizer.sanitizeIdentification("123abc456"))
                    .thenThrow(new IllegalArgumentException("Formato de identificación inválido"));

            // When
            boolean result = validator.isValid("123abc456", context);

            // Then
            assertFalse(result);
            verify(context).disableDefaultConstraintViolation();
            verify(context).buildConstraintViolationWithTemplate("Formato de identificación inválido");
        }
    }

    // ========== EMPTY STRING TESTS ==========

    @Test
    @DisplayName("isValid should handle empty strings correctly")
    void isValid_WithEmptyString_ShouldDelegateToSanitizer() {
        // Given
        when(annotation.value()).thenReturn(Sanitized.SanitizationType.TEXT);
        when(annotation.allowNull()).thenReturn(false);
        validator.initialize(annotation);

        try (MockedStatic<InputSanitizer> mockedSanitizer = mockStatic(InputSanitizer.class)) {
            mockedSanitizer.when(() -> InputSanitizer.sanitizeText(""))
                    .thenReturn("");

            // When
            boolean result = validator.isValid("", context);

            // Then
            assertTrue(result);
            verify(context, never()).buildConstraintViolationWithTemplate(anyString());
        }
    }

    @Test
    @DisplayName("isValid should handle whitespace-only strings")
    void isValid_WithWhitespaceOnly_ShouldDelegateToSanitizer() {
        // Given
        when(annotation.value()).thenReturn(Sanitized.SanitizationType.TEXT);
        when(annotation.allowNull()).thenReturn(false);
        validator.initialize(annotation);

        try (MockedStatic<InputSanitizer> mockedSanitizer = mockStatic(InputSanitizer.class)) {
            mockedSanitizer.when(() -> InputSanitizer.sanitizeText("   "))
                    .thenReturn("");

            // When
            boolean result = validator.isValid("   ", context);

            // Then
            assertTrue(result);
            verify(context, never()).buildConstraintViolationWithTemplate(anyString());
        }
    }

    // ========== EDGE CASES AND ERROR HANDLING ==========

    @Test
    @DisplayName("isValid should handle unexpected sanitizer exceptions")
    void isValid_WithUnexpectedSanitizerException_ShouldReturnFalse() {
        // Given
        when(annotation.value()).thenReturn(Sanitized.SanitizationType.TEXT);
        when(annotation.allowNull()).thenReturn(false);
        validator.initialize(annotation);

        try (MockedStatic<InputSanitizer> mockedSanitizer = mockStatic(InputSanitizer.class)) {
            mockedSanitizer.when(() -> InputSanitizer.sanitizeText("test"))
                    .thenThrow(new RuntimeException("Unexpected error"));

            // When
            boolean result = validator.isValid("test", context);

            // Then
            assertFalse(result);
            verify(context).disableDefaultConstraintViolation();
            verify(context).buildConstraintViolationWithTemplate("Unexpected error");
        }
    }

    @Test
    @DisplayName("isValid should handle very long strings")
    void isValid_WithVeryLongString_ShouldDelegateToSanitizer() {
        // Given
        when(annotation.value()).thenReturn(Sanitized.SanitizationType.TEXT);
        when(annotation.allowNull()).thenReturn(false);
        validator.initialize(annotation);

        String longString = "a".repeat(10000);

        try (MockedStatic<InputSanitizer> mockedSanitizer = mockStatic(InputSanitizer.class)) {
            mockedSanitizer.when(() -> InputSanitizer.sanitizeText(longString))
                    .thenReturn(longString);

            // When
            boolean result = validator.isValid(longString, context);

            // Then
            assertTrue(result);
            verify(context, never()).buildConstraintViolationWithTemplate(anyString());
        }
    }

    @Test
    @DisplayName("isValid should handle special characters in different sanitization types")
    void isValid_WithSpecialCharacters_ShouldHandlePerType() {
        // Test with special characters for each type
        String specialChars = "áéíóúñü@#$%&*()[]{}";

        // TEXT type
        when(annotation.value()).thenReturn(Sanitized.SanitizationType.TEXT);
        when(annotation.allowNull()).thenReturn(false);
        validator.initialize(annotation);

        try (MockedStatic<InputSanitizer> mockedSanitizer = mockStatic(InputSanitizer.class)) {
            mockedSanitizer.when(() -> InputSanitizer.sanitizeText(specialChars))
                    .thenReturn(specialChars);

            // When
            boolean result = validator.isValid(specialChars, context);

            // Then
            assertTrue(result);
            verify(context, never()).buildConstraintViolationWithTemplate(anyString());
        }
    }

    @Test
    @DisplayName("initialize should handle null annotation gracefully")
    void initialize_WithNullAnnotation_ShouldHandleGracefully() {
        // This test verifies that the validator doesn't crash with null annotation
        // The behavior might vary depending on implementation
        assertDoesNotThrow(() -> validator.initialize(null));
    }
}
