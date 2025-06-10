package com.udea.GPX.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.Constraint;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Sanitized Annotation Tests")
class SanitizedTest {

    // ========== ANNOTATION METADATA TESTS ==========

    @Test
    @DisplayName("Sanitized annotation should have correct target elements")
    void sanitizedAnnotation_ShouldHaveCorrectTargets() {
        // Given
        Target target = Sanitized.class.getAnnotation(Target.class);

        // When & Then
        assertNotNull(target);
        ElementType[] expectedTargets = { ElementType.FIELD, ElementType.PARAMETER };
        ElementType[] actualTargets = target.value();

        assertEquals(expectedTargets.length, actualTargets.length);
        for (ElementType expectedTarget : expectedTargets) {
            boolean found = false;
            for (ElementType actualTarget : actualTargets) {
                if (expectedTarget == actualTarget) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "Expected target " + expectedTarget + " not found");
        }
    }

    @Test
    @DisplayName("Sanitized annotation should have runtime retention")
    void sanitizedAnnotation_ShouldHaveRuntimeRetention() {
        // Given
        Retention retention = Sanitized.class.getAnnotation(Retention.class);

        // When & Then
        assertNotNull(retention);
        assertEquals(RetentionPolicy.RUNTIME, retention.value());
    }

    @Test
    @DisplayName("Sanitized annotation should be documented")
    void sanitizedAnnotation_ShouldBeDocumented() {
        // Given & When
        Documented documented = Sanitized.class.getAnnotation(Documented.class);

        // Then
        assertNotNull(documented);
    }

    @Test
    @DisplayName("Sanitized annotation should be a constraint")
    void sanitizedAnnotation_ShouldBeConstraint() {
        // Given
        Constraint constraint = Sanitized.class.getAnnotation(Constraint.class);

        // When & Then
        assertNotNull(constraint);
        Class<?>[] validatedBy = constraint.validatedBy();
        assertEquals(1, validatedBy.length);
        assertEquals(SanitizedValidator.class, validatedBy[0]);
    }

    // ========== ANNOTATION METHODS TESTS ========== @Test
    @DisplayName("Sanitized annotation should have default message method")
    void sanitizedAnnotation_ShouldHaveDefaultMessage() throws Exception {
        // Given
        Method messageMethod = Sanitized.class.getMethod("message");

        // When
        Object defaultValue = messageMethod.getDefaultValue();

        // Then
        assertNotNull(defaultValue);
        assertEquals("El campo contiene caracteres no permitidos", defaultValue);
    }

    @Test
    @DisplayName("Sanitized annotation should have groups method")
    void sanitizedAnnotation_ShouldHaveGroupsMethod() throws Exception {
        // Given
        Method groupsMethod = Sanitized.class.getMethod("groups");

        // When & Then
        assertNotNull(groupsMethod);
        assertEquals(Class[].class, groupsMethod.getReturnType());

        Object defaultValue = groupsMethod.getDefaultValue();
        assertNotNull(defaultValue);
        assertTrue(defaultValue instanceof Class[]);
        assertEquals(0, ((Class<?>[]) defaultValue).length);
    }

    @Test
    @DisplayName("Sanitized annotation should have payload method")
    void sanitizedAnnotation_ShouldHavePayloadMethod() throws Exception {
        // Given
        Method payloadMethod = Sanitized.class.getMethod("payload");

        // When & Then
        assertNotNull(payloadMethod);
        assertEquals(Class[].class, payloadMethod.getReturnType());

        Object defaultValue = payloadMethod.getDefaultValue();
        assertNotNull(defaultValue);
        assertTrue(defaultValue instanceof Class[]);
        assertEquals(0, ((Class<?>[]) defaultValue).length);
    }

    @Test
    @DisplayName("Sanitized annotation should have value method with default")
    void sanitizedAnnotation_ShouldHaveValueMethodWithDefault() throws Exception {
        // Given
        Method valueMethod = Sanitized.class.getMethod("value");

        // When & Then
        assertNotNull(valueMethod);
        assertEquals(Sanitized.SanitizationType.class, valueMethod.getReturnType());

        Object defaultValue = valueMethod.getDefaultValue();
        assertNotNull(defaultValue);
        assertEquals(Sanitized.SanitizationType.TEXT, defaultValue);
    }

    @Test
    @DisplayName("Sanitized annotation should have allowNull method with default")
    void sanitizedAnnotation_ShouldHaveAllowNullMethodWithDefault() throws Exception {
        // Given
        Method allowNullMethod = Sanitized.class.getMethod("allowNull");

        // When & Then
        assertNotNull(allowNullMethod);
        assertEquals(boolean.class, allowNullMethod.getReturnType());

        Object defaultValue = allowNullMethod.getDefaultValue();
        assertNotNull(defaultValue);
        assertEquals(true, defaultValue);
    }

    // ========== SANITIZATION TYPE ENUM TESTS ========== @Test
    @DisplayName("SanitizationType enum should have all expected values")
    void sanitizationType_ShouldHaveAllExpectedValues() {
        // Given
        Sanitized.SanitizationType[] expectedTypes = {
                Sanitized.SanitizationType.TEXT,
                Sanitized.SanitizationType.EMAIL,
                Sanitized.SanitizationType.NAME,
                Sanitized.SanitizationType.URL,
                Sanitized.SanitizationType.PHONE,
                Sanitized.SanitizationType.IDENTIFICATION
        };

        // When
        Sanitized.SanitizationType[] actualTypes = Sanitized.SanitizationType.values();

        // Then
        assertEquals(expectedTypes.length, actualTypes.length);
        for (Sanitized.SanitizationType expectedType : expectedTypes) {
            boolean found = false;
            for (Sanitized.SanitizationType actualType : actualTypes) {
                if (expectedType == actualType) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "Expected type " + expectedType + " not found");
        }
    }

    @Test
    @DisplayName("SanitizationType enum should support valueOf")
    void sanitizationType_ShouldSupportValueOf() {
        // Test each enum value
        assertEquals(Sanitized.SanitizationType.TEXT,
                Sanitized.SanitizationType.valueOf("TEXT"));
        assertEquals(Sanitized.SanitizationType.EMAIL,
                Sanitized.SanitizationType.valueOf("EMAIL"));
        assertEquals(Sanitized.SanitizationType.NAME,
                Sanitized.SanitizationType.valueOf("NAME"));
        assertEquals(Sanitized.SanitizationType.URL,
                Sanitized.SanitizationType.valueOf("URL"));
        assertEquals(Sanitized.SanitizationType.PHONE,
                Sanitized.SanitizationType.valueOf("PHONE"));
        assertEquals(Sanitized.SanitizationType.IDENTIFICATION,
                Sanitized.SanitizationType.valueOf("IDENTIFICATION"));
    }

    @Test
    @DisplayName("SanitizationType enum valueOf should throw exception for invalid values")
    void sanitizationType_ValueOfShouldThrowForInvalidValues() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            Sanitized.SanitizationType.valueOf("INVALID");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Sanitized.SanitizationType.valueOf("text");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Sanitized.SanitizationType.valueOf("");
        });
    }

    @Test
    @DisplayName("SanitizationType enum valueOf should throw exception for null")
    void sanitizationType_ValueOfShouldThrowForNull() {
        // When & Then
        assertThrows(NullPointerException.class, () -> {
            Sanitized.SanitizationType.valueOf(null);
        });
    }

    // ========== ANNOTATION USAGE TESTS ========== // Test class to verify
    // annotation usage
    private static class TestClass {
        @Sanitized(value = Sanitized.SanitizationType.TEXT, allowNull = false)
        private String textField;

        @Sanitized(value = Sanitized.SanitizationType.EMAIL, allowNull = true)
        private String emailField;

        @Sanitized(value = Sanitized.SanitizationType.NAME, allowNull = false)
        private String nameField;

        @Sanitized(value = Sanitized.SanitizationType.URL, allowNull = true)
        private String urlField;

        @Sanitized(value = Sanitized.SanitizationType.PHONE, allowNull = false)
        private String phoneField;

        @Sanitized(value = Sanitized.SanitizationType.IDENTIFICATION, allowNull = true)
        private String identificationField;

        @Sanitized // Should use defaults
        private String defaultField;

        public void testMethod(@Sanitized(Sanitized.SanitizationType.TEXT) String parameter) {
            // Method with sanitized parameter
        }
    }

    @Test
    @DisplayName("Sanitized annotation should work on fields with different configurations")
    void sanitizedAnnotation_ShouldWorkOnFieldsWithDifferentConfigurations() throws Exception {
        // Test TEXT field
        var textField = TestClass.class.getDeclaredField("textField");
        Sanitized textAnnotation = textField.getAnnotation(Sanitized.class);
        assertNotNull(textAnnotation);
        assertEquals(Sanitized.SanitizationType.TEXT, textAnnotation.value());
        assertFalse(textAnnotation.allowNull());

        // Test EMAIL field
        var emailField = TestClass.class.getDeclaredField("emailField");
        Sanitized emailAnnotation = emailField.getAnnotation(Sanitized.class);
        assertNotNull(emailAnnotation);
        assertEquals(Sanitized.SanitizationType.EMAIL, emailAnnotation.value());
        assertTrue(emailAnnotation.allowNull());

        // Test NAME field
        var nameField = TestClass.class.getDeclaredField("nameField");
        Sanitized nameAnnotation = nameField.getAnnotation(Sanitized.class);
        assertNotNull(nameAnnotation);
        assertEquals(Sanitized.SanitizationType.NAME, nameAnnotation.value());
        assertFalse(nameAnnotation.allowNull());

        // Test URL field
        var urlField = TestClass.class.getDeclaredField("urlField");
        Sanitized urlAnnotation = urlField.getAnnotation(Sanitized.class);
        assertNotNull(urlAnnotation);
        assertEquals(Sanitized.SanitizationType.URL, urlAnnotation.value());
        assertTrue(urlAnnotation.allowNull());

        // Test PHONE field
        var phoneField = TestClass.class.getDeclaredField("phoneField");
        Sanitized phoneAnnotation = phoneField.getAnnotation(Sanitized.class);
        assertNotNull(phoneAnnotation);
        assertEquals(Sanitized.SanitizationType.PHONE, phoneAnnotation.value());
        assertFalse(phoneAnnotation.allowNull());

        // Test IDENTIFICATION field
        var identificationField = TestClass.class.getDeclaredField("identificationField");
        Sanitized identificationAnnotation = identificationField.getAnnotation(Sanitized.class);
        assertNotNull(identificationAnnotation);
        assertEquals(Sanitized.SanitizationType.IDENTIFICATION, identificationAnnotation.value());
        assertTrue(identificationAnnotation.allowNull());

        // Test default field
        var defaultField = TestClass.class.getDeclaredField("defaultField");
        Sanitized defaultAnnotation = defaultField.getAnnotation(Sanitized.class);
        assertNotNull(defaultAnnotation);
        assertEquals(Sanitized.SanitizationType.TEXT, defaultAnnotation.value()); // Default
        assertTrue(defaultAnnotation.allowNull()); // Default
    }

    @Test
    @DisplayName("Sanitized annotation should work on method parameters")
    void sanitizedAnnotation_ShouldWorkOnMethodParameters() throws Exception {
        // Given
        Method testMethod = TestClass.class.getMethod("testMethod", String.class);
        var parameters = testMethod.getParameters();

        // When
        assertEquals(1, parameters.length);
        Sanitized annotation = parameters[0].getAnnotation(Sanitized.class);

        // Then
        assertNotNull(annotation);
        assertEquals(Sanitized.SanitizationType.TEXT, annotation.value());
        assertTrue(annotation.allowNull()); // Default value
    }

    // ========== CONSTRAINT VALIDATION INTEGRATION TESTS ==========

    @Test
    @DisplayName("Sanitized annotation should define proper constraint validator")
    void sanitizedAnnotation_ShouldDefineProperConstraintValidator() {
        // Given
        Constraint constraint = Sanitized.class.getAnnotation(Constraint.class);

        // When & Then
        assertNotNull(constraint);
        Class<?>[] validators = constraint.validatedBy();
        assertEquals(1, validators.length);
        assertEquals(SanitizedValidator.class, validators[0]);
    }

    @Test
    @DisplayName("Sanitized annotation should inherit from Payload interface markers")
    void sanitizedAnnotation_ShouldSupportPayloadInterface() throws Exception {
        // Given
        Method payloadMethod = Sanitized.class.getMethod("payload");

        // When
        Class<?> returnType = payloadMethod.getReturnType();

        // Then
        assertTrue(returnType.isArray());
        assertEquals(Class.class, returnType.getComponentType());

        // Verify the default is an array that can hold Payload classes
        Object defaultValue = payloadMethod.getDefaultValue();
        assertTrue(defaultValue instanceof Class[]);
    } // ========== ENUM ORDINAL TESTS ==========

    @Test
    @DisplayName("SanitizationType enum should have stable ordinal values")
    void sanitizationType_ShouldHaveStableOrdinalValues() {
        // Given & When & Then
        assertEquals(0, Sanitized.SanitizationType.TEXT.ordinal());
        assertEquals(1, Sanitized.SanitizationType.EMAIL.ordinal());
        assertEquals(2, Sanitized.SanitizationType.NAME.ordinal());
        assertEquals(3, Sanitized.SanitizationType.URL.ordinal());
        assertEquals(4, Sanitized.SanitizationType.PHONE.ordinal());
        assertEquals(5, Sanitized.SanitizationType.IDENTIFICATION.ordinal());
    }

    @Test
    @DisplayName("SanitizationType enum should have correct string representation")
    void sanitizationType_ShouldHaveCorrectStringRepresentation() {
        // Given & When & Then
        assertEquals("TEXT", Sanitized.SanitizationType.TEXT.toString());
        assertEquals("EMAIL", Sanitized.SanitizationType.EMAIL.toString());
        assertEquals("NAME", Sanitized.SanitizationType.NAME.toString());
        assertEquals("URL", Sanitized.SanitizationType.URL.toString());
        assertEquals("PHONE", Sanitized.SanitizationType.PHONE.toString());
        assertEquals("IDENTIFICATION", Sanitized.SanitizationType.IDENTIFICATION.toString());
    }
}
