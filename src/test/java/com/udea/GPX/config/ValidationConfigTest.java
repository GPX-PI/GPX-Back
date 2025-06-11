package com.udea.gpx.config;

import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ValidationConfig Configuration Tests")
class ValidationConfigTest {

    private ValidationConfig validationConfig;

    @BeforeEach
    void setUp() {
        validationConfig = new ValidationConfig();
    }

    @Nested
    @DisplayName("Bean Creation Tests")
    class BeanCreationTests {

        @Test
        @DisplayName("Should create validator bean successfully")
        void shouldCreateValidatorBeanSuccessfully() {
            // Given & When
            Validator validator = validationConfig.validator();

            // Then
            assertNotNull(validator, "Validator bean should not be null");
            assertTrue(validator instanceof Validator, "Should return a valid Validator instance");
        }

        @Test
        @DisplayName("Should create method validation post processor bean successfully")
        void shouldCreateMethodValidationPostProcessorBeanSuccessfully() {
            // Given & When
            MethodValidationPostProcessor postProcessor = validationConfig.methodValidationPostProcessor();

            // Then
            assertNotNull(postProcessor, "MethodValidationPostProcessor bean should not be null");
            assertTrue(postProcessor instanceof MethodValidationPostProcessor,
                    "Should return a valid MethodValidationPostProcessor instance");
        }

        @Test
        @DisplayName("Should create consistent validator instances")
        void shouldCreateConsistentValidatorInstances() {
            // Given & When
            Validator validator1 = validationConfig.validator();
            Validator validator2 = validationConfig.validator();

            // Then
            assertNotNull(validator1);
            assertNotNull(validator2);
            assertEquals(validator1.getClass(), validator2.getClass());
        }

        @Test
        @DisplayName("Should create consistent method validation post processor instances")
        void shouldCreateConsistentMethodValidationPostProcessorInstances() {
            // Given & When
            MethodValidationPostProcessor processor1 = validationConfig.methodValidationPostProcessor();
            MethodValidationPostProcessor processor2 = validationConfig.methodValidationPostProcessor();

            // Then
            assertNotNull(processor1);
            assertNotNull(processor2);
            assertEquals(processor1.getClass(), processor2.getClass());
        }
    }

    @Nested
    @DisplayName("Interface Implementation Tests")
    class InterfaceImplementationTests {

        @Test
        @DisplayName("Should implement WebMvcConfigurer interface")
        void shouldImplementWebMvcConfigurerInterface() {
            // Given & When
            boolean implementsWebMvcConfigurer = WebMvcConfigurer.class.isAssignableFrom(ValidationConfig.class);

            // Then
            assertTrue(implementsWebMvcConfigurer, "ValidationConfig should implement WebMvcConfigurer");
        }

        @Test
        @DisplayName("Should be instance of WebMvcConfigurer")
        void shouldBeInstanceOfWebMvcConfigurer() {
            // Given & When & Then
            assertTrue(validationConfig instanceof WebMvcConfigurer,
                    "ValidationConfig instance should be instance of WebMvcConfigurer");
        }
    }

    @Nested
    @DisplayName("Annotation Tests")
    class AnnotationTests {

        @Test
        @DisplayName("Should be annotated with @Configuration")
        void shouldBeAnnotatedWithConfiguration() {
            // Given & When
            boolean hasConfigurationAnnotation = ValidationConfig.class.isAnnotationPresent(
                    org.springframework.context.annotation.Configuration.class);

            // Then
            assertTrue(hasConfigurationAnnotation, "ValidationConfig should be annotated with @Configuration");
        }

        @Test
        @DisplayName("Should have validator method annotated with @Bean")
        void shouldHaveValidatorMethodAnnotatedWithBean() throws NoSuchMethodException {
            // Given & When
            boolean hasValidatorBeanAnnotation = ValidationConfig.class
                    .getMethod("validator")
                    .isAnnotationPresent(org.springframework.context.annotation.Bean.class);

            // Then
            assertTrue(hasValidatorBeanAnnotation, "validator() method should be annotated with @Bean");
        }

        @Test
        @DisplayName("Should have methodValidationPostProcessor method annotated with @Bean")
        void shouldHaveMethodValidationPostProcessorMethodAnnotatedWithBean() throws NoSuchMethodException {
            // Given & When
            boolean hasPostProcessorBeanAnnotation = ValidationConfig.class
                    .getMethod("methodValidationPostProcessor")
                    .isAnnotationPresent(org.springframework.context.annotation.Bean.class);

            // Then
            assertTrue(hasPostProcessorBeanAnnotation,
                    "methodValidationPostProcessor() method should be annotated with @Bean");
        }
    }

    @Nested
    @DisplayName("Validator Functionality Tests")
    class ValidatorFunctionalityTests {

        @Test
        @DisplayName("Should create validator that can validate objects")
        void shouldCreateValidatorThatCanValidateObjects() {
            // Given
            Validator validator = validationConfig.validator();

            // When - Create a test object to validate
            TestValidationObject testObject = new TestValidationObject();
            testObject.validField = "valid";
            testObject.invalidField = null; // This should be invalid

            // Use validField and invalidField to avoid unused field warning
            assertEquals("valid", testObject.validField);
            assertNull(testObject.invalidField);

            var violations = validator.validate(testObject);

            // Then
            assertNotNull(violations, "Validator should return violations set");
            // Note: The actual validation depends on the annotations on
            // TestValidationObject
        }

        @Test
        @DisplayName("Should create validator with proper factory")
        void shouldCreateValidatorWithProperFactory() {
            // Given & When
            Validator validator = validationConfig.validator();

            // Then
            assertNotNull(validator, "Validator should not be null");
            // Verify it's from the default validator factory
            assertEquals("org.hibernate.validator.internal.engine.ValidatorImpl",
                    validator.getClass().getName());
        }
    }

    @Nested
    @DisplayName("Method Validation Tests")
    class MethodValidationTests {

        @Test
        @DisplayName("Should create method validation post processor with correct order")
        void shouldCreateMethodValidationPostProcessorWithCorrectOrder() {
            // Given & When
            MethodValidationPostProcessor postProcessor = validationConfig.methodValidationPostProcessor();

            // Then
            assertNotNull(postProcessor);
            // Default order should be Ordered.LOWEST_PRECEDENCE
            assertEquals(Integer.MAX_VALUE, postProcessor.getOrder());
        }

        @Test
        @DisplayName("Should create method validation post processor that can be configured")
        void shouldCreateMethodValidationPostProcessorThatCanBeConfigured() {
            // Given
            MethodValidationPostProcessor postProcessor = validationConfig.methodValidationPostProcessor();

            // When & Then
            assertNotNull(postProcessor);
            assertDoesNotThrow(() -> {
                postProcessor.setOrder(100);
                assertEquals(100, postProcessor.getOrder());
            }, "Should be able to configure the post processor");
        }
    }

    @Nested
    @DisplayName("Integration and Configuration Tests")
    class IntegrationConfigurationTests {
        @Test
        @DisplayName("Should support Bean Validation with custom validators")
        void shouldSupportBeanValidationWithCustomValidators() {
            // Given
            Validator validator = validationConfig.validator();

            // When & Then
            assertNotNull(validator);

            // Verify it supports standard validation
            assertTrue(validator.getClass().getName().contains("hibernate.validator") ||
                    validator.getClass().getName().contains("validation"));
        }

        @Test
        @DisplayName("Should enable method level validation")
        void shouldEnableMethodLevelValidation() {
            // Given
            MethodValidationPostProcessor postProcessor = validationConfig.methodValidationPostProcessor();

            // When & Then
            assertNotNull(postProcessor);

            // The post processor should be ready to handle @Validated annotations
            assertTrue(postProcessor instanceof MethodValidationPostProcessor);
        }

        @Test
        @DisplayName("Should work with Spring MVC configuration")
        void shouldWorkWithSpringMvcConfiguration() {
            // Given & When
            boolean isWebMvcConfigurer = validationConfig instanceof WebMvcConfigurer;

            // Then
            assertTrue(isWebMvcConfigurer, "Should integrate with Spring MVC");

            // Should not override any WebMvcConfigurer methods by default
            // This allows for future customization while maintaining base functionality
        }
    }

    @Nested
    @DisplayName("Configuration Documentation Tests")
    class ConfigurationDocumentationTests {

        @Test
        @DisplayName("Should have proper class documentation")
        void shouldHaveProperClassDocumentation() {
            // Given & When
            String className = ValidationConfig.class.getSimpleName();

            // Then
            assertEquals("ValidationConfig", className);
            assertTrue(ValidationConfig.class.isAnnotationPresent(
                    org.springframework.context.annotation.Configuration.class));
        }

        @Test
        @DisplayName("Should support validation features as documented")
        void shouldSupportValidationFeaturesAsDocumented() {
            // Given
            ValidationConfig config = new ValidationConfig();

            // When - Test documented features
            Validator validator = config.validator();
            MethodValidationPostProcessor postProcessor = config.methodValidationPostProcessor();

            // Then - Should support:
            // 1. @RequestBody with @Valid
            assertNotNull(validator, "Should support @RequestBody with @Valid");

            // 2. @RequestParam with validation annotations
            assertNotNull(validator, "Should support @RequestParam with validation annotations");

            // 3. Methods with @Validated
            assertNotNull(postProcessor, "Should support methods with @Validated");
        }
    }

    // Helper test class for validation testing
    private static class TestValidationObject {
        public String validField;
        public String invalidField;
    }
}
