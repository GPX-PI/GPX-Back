package com.udea.gpx.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@DisplayName("GlobalExceptionHandler Tests")
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

        private GlobalExceptionHandler globalExceptionHandler;

        @Mock
        private WebRequest webRequest;

        @Mock
        private MethodArgumentNotValidException methodArgumentNotValidException;

        @Mock
        private BindingResult bindingResult;

        @Mock
        private ConstraintViolationException constraintViolationException;

        @Mock
        private ConstraintViolation<Object> constraintViolation;

        @Mock
        private Path propertyPath;

        @BeforeEach
        void setUp() {
                globalExceptionHandler = new GlobalExceptionHandler();
                // Setup common mock behavior with lenient stubbing
                lenient().when(webRequest.getDescription(false)).thenReturn("uri=/test/path");
        }

        @Nested
        @DisplayName("MethodArgumentNotValidException Handling Tests")
        class MethodArgumentNotValidExceptionTests {
                @Test
                @DisplayName("Should handle validation exceptions with field errors")
                void shouldHandleValidationExceptionsWithFieldErrors() {
                        // Given
                        when(webRequest.getDescription(false)).thenReturn("uri=/test/path");
                        FieldError fieldError1 = new FieldError("objectName", "field1", "Field1 is required");
                        FieldError fieldError2 = new FieldError("objectName", "field2", "Field2 is invalid");

                        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
                        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError1, fieldError2));

                        // When
                        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                                        .handleValidationExceptions(methodArgumentNotValidException, webRequest);

                        // Then
                        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                        assertNotNull(response.getBody());

                        Map<String, Object> responseBody = response.getBody();
                        assertNotNull(responseBody);
                        assertEquals(400, responseBody.get("status"));
                        assertEquals("Error de validación", responseBody.get("error"));
                        assertEquals("Los datos enviados no son válidos", responseBody.get("message"));
                        assertEquals("/test/path", responseBody.get("path"));
                        assertNotNull(responseBody.get("timestamp"));

                        @SuppressWarnings("unchecked")
                        Map<String, String> fieldErrorsMap = (Map<String, String>) responseBody.get("fieldErrors");
                        assertEquals(2, fieldErrorsMap.size());
                        assertEquals("Field1 is required", fieldErrorsMap.get("field1"));
                        assertEquals("Field2 is invalid", fieldErrorsMap.get("field2"));
                }

                @Test
                @DisplayName("Should handle validation exceptions with empty field errors")
                void shouldHandleValidationExceptionsWithEmptyFieldErrors() {
                        // Given
                        when(webRequest.getDescription(false)).thenReturn("uri=/test/path");
                        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
                        when(bindingResult.getAllErrors()).thenReturn(List.of());

                        // When
                        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                                        .handleValidationExceptions(methodArgumentNotValidException, webRequest);

                        // Then
                        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                        assertNotNull(response.getBody());

                        Map<String, Object> responseBody = response.getBody();
                        assertNotNull(responseBody);
                        @SuppressWarnings("unchecked")
                        Map<String, String> fieldErrorsMap = (Map<String, String>) responseBody.get("fieldErrors");
                        assertTrue(fieldErrorsMap.isEmpty());
                }

                @Test
                @DisplayName("Should include proper timestamp in validation error response")
                void shouldIncludeProperTimestampInValidationErrorResponse() {
                        // Given
                        when(webRequest.getDescription(false)).thenReturn("uri=/test/path");
                        LocalDateTime beforeRequest = LocalDateTime.now();
                        FieldError fieldError = new FieldError("objectName", "field1", "Field1 is required");

                        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
                        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

                        // When
                        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                                        .handleValidationExceptions(methodArgumentNotValidException, webRequest);

                        // Then
                        LocalDateTime afterRequest = LocalDateTime.now();
                        Map<String, Object> responseBody = response.getBody();
                        assertNotNull(responseBody);
                        LocalDateTime responseTimestamp = (LocalDateTime) responseBody.get("timestamp");

                        assertNotNull(responseTimestamp);
                        assertTrue(responseTimestamp.isAfter(beforeRequest.minusSeconds(1)));
                        assertTrue(responseTimestamp.isBefore(afterRequest.plusSeconds(1)));
                }
        }

        @Nested
        @DisplayName("ConstraintViolationException Handling Tests")
        class ConstraintViolationExceptionTests {
                @Test
                @DisplayName("Should handle constraint violation exceptions")
                void shouldHandleConstraintViolationExceptions() {
                        // Given
                        when(webRequest.getDescription(false)).thenReturn("uri=/test/path");
                        when(propertyPath.toString()).thenReturn("fieldName");
                        when(constraintViolation.getPropertyPath()).thenReturn(propertyPath);
                        when(constraintViolation.getMessage()).thenReturn("Constraint violation message");
                        when(constraintViolationException.getConstraintViolations())
                                        .thenReturn(Set.of(constraintViolation));

                        // When
                        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                                        .handleConstraintViolationException(constraintViolationException, webRequest);

                        // Then
                        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                        assertNotNull(response.getBody());

                        Map<String, Object> responseBody = response.getBody();
                        assertNotNull(responseBody);
                        assertEquals(400, responseBody.get("status"));
                        assertEquals("Violación de restricciones", responseBody.get("error"));
                        assertEquals("Los datos no cumplen las restricciones requeridas", responseBody.get("message"));
                        assertEquals("/test/path", responseBody.get("path"));

                        @SuppressWarnings("unchecked")
                        Map<String, String> violations = (Map<String, String>) responseBody.get("violations");
                        assertEquals(1, violations.size());
                        assertEquals("Constraint violation message", violations.get("fieldName"));
                }

                @Test
                @DisplayName("Should handle multiple constraint violations")
                void shouldHandleMultipleConstraintViolations() {
                        // Given
                        when(webRequest.getDescription(false)).thenReturn("uri=/test/path");
                        @SuppressWarnings("unchecked")
                        ConstraintViolation<Object> violation1 = mock(ConstraintViolation.class);
                        @SuppressWarnings("unchecked")
                        ConstraintViolation<Object> violation2 = mock(ConstraintViolation.class);
                        Path path1 = mock(Path.class);
                        Path path2 = mock(Path.class);

                        when(path1.toString()).thenReturn("field1");
                        when(path2.toString()).thenReturn("field2");
                        when(violation1.getPropertyPath()).thenReturn(path1);
                        when(violation1.getMessage()).thenReturn("Violation 1");
                        when(violation2.getPropertyPath()).thenReturn(path2);
                        when(violation2.getMessage()).thenReturn("Violation 2");
                        when(constraintViolationException.getConstraintViolations())
                                        .thenReturn(Set.of(violation1, violation2));

                        // When
                        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                                        .handleConstraintViolationException(constraintViolationException, webRequest);

                        // Then
                        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                        Map<String, Object> responseBody = response.getBody();
                        assertNotNull(responseBody);

                        @SuppressWarnings("unchecked")
                        Map<String, String> violations = (Map<String, String>) responseBody.get("violations");
                        assertEquals(2, violations.size());
                        assertEquals("Violation 1", violations.get("field1"));
                        assertEquals("Violation 2", violations.get("field2"));
                }
        }

        @Nested
        @DisplayName("IllegalArgumentException Handling Tests")
        class IllegalArgumentExceptionTests {

                @Test
                @DisplayName("Should handle illegal argument exceptions")
                void shouldHandleIllegalArgumentExceptions() {
                        // Given
                        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument provided");

                        // When
                        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                                        .handleIllegalArgumentException(exception, webRequest);

                        // Then
                        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                        assertNotNull(response.getBody());

                        Map<String, Object> responseBody = response.getBody();
                        assertNotNull(responseBody);
                        assertEquals(400, responseBody.get("status"));
                        assertEquals("Argumento inválido", responseBody.get("error"));
                        assertEquals("Invalid argument provided", responseBody.get("message"));
                        assertEquals("/test/path", responseBody.get("path"));
                        assertNotNull(responseBody.get("timestamp"));
                }

                @Test
                @DisplayName("Should handle illegal argument exceptions with null message")
                void shouldHandleIllegalArgumentExceptionsWithNullMessage() {
                        // Given
                        IllegalArgumentException exception = new IllegalArgumentException((String) null);

                        // When
                        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                                        .handleIllegalArgumentException(exception, webRequest);

                        // Then
                        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                        Map<String, Object> responseBody = response.getBody();
                        assertNotNull(responseBody);
                        assertNull(responseBody.get("message"));
                }
        }

        @Nested
        @DisplayName("RuntimeException Handling Tests")
        class RuntimeExceptionTests {

                @Test
                @DisplayName("Should handle runtime exception with 'no encontrado' message as NOT_FOUND")
                void shouldHandleRuntimeExceptionWithNoEncontradoMessageAsNotFound() {
                        // Given
                        RuntimeException exception = new RuntimeException("El recurso no encontrado");

                        // When
                        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleRuntimeException(
                                        exception,
                                        webRequest);

                        // Then
                        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                        assertNull(response.getBody()); // notFound() builds empty body
                }

                @Test
                @DisplayName("Should handle runtime exception with 'not found' message as NOT_FOUND")
                void shouldHandleRuntimeExceptionWithNotFoundMessageAsNotFound() {
                        // Given
                        RuntimeException exception = new RuntimeException("Resource not found");

                        // When
                        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleRuntimeException(
                                        exception,
                                        webRequest);

                        // Then
                        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                }

                @Test
                @DisplayName("Should handle runtime exception with 'ya existe' message as CONFLICT")
                void shouldHandleRuntimeExceptionWithYaExisteMessageAsConflict() {
                        // Given
                        RuntimeException exception = new RuntimeException("El recurso ya existe");

                        // When
                        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleRuntimeException(
                                        exception,
                                        webRequest);

                        // Then
                        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
                        assertNotNull(response.getBody());

                        Map<String, Object> responseBody = response.getBody();
                        assertNotNull(responseBody);
                        assertEquals(409, responseBody.get("status"));
                        assertEquals("Conflicto de datos", responseBody.get("error"));
                        assertEquals("El recurso ya existe", responseBody.get("message"));
                }

                @Test
                @DisplayName("Should handle runtime exception with 'duplicado' message as CONFLICT")
                void shouldHandleRuntimeExceptionWithDuplicadoMessageAsConflict() {
                        // Given
                        RuntimeException exception = new RuntimeException("Registro duplicado");

                        // When
                        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleRuntimeException(
                                        exception,
                                        webRequest);

                        // Then
                        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
                        Map<String, Object> responseBody = response.getBody();
                        assertNotNull(responseBody);
                        assertEquals("Conflicto de datos", responseBody.get("error"));
                }

                @Test
                @DisplayName("Should handle generic runtime exception as BAD_REQUEST")
                void shouldHandleGenericRuntimeExceptionAsBadRequest() {
                        // Given
                        RuntimeException exception = new RuntimeException("Generic error");

                        // When
                        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleRuntimeException(
                                        exception,
                                        webRequest);

                        // Then
                        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                        assertNotNull(response.getBody());

                        Map<String, Object> responseBody = response.getBody();
                        assertNotNull(responseBody);
                        assertEquals(400, responseBody.get("status"));
                        assertEquals("Error de procesamiento", responseBody.get("error"));
                        assertEquals("Generic error", responseBody.get("message"));
                }

                @Test
                @DisplayName("Should handle runtime exception with null message")
                void shouldHandleRuntimeExceptionWithNullMessage() {
                        // Given
                        RuntimeException exception = new RuntimeException((String) null);

                        // When
                        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleRuntimeException(
                                        exception,
                                        webRequest);

                        // Then
                        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                        Map<String, Object> responseBody = response.getBody();
                        assertNotNull(responseBody);
                        assertEquals("Error interno de procesamiento", responseBody.get("message"));
                }
        }

        @Nested
        @DisplayName("Configuration and Annotation Tests")
        class ConfigurationAnnotationTests {

                @Test
                @DisplayName("Should be annotated with @ControllerAdvice")
                void shouldBeAnnotatedWithControllerAdvice() {
                        // Given & When
                        boolean hasControllerAdviceAnnotation = GlobalExceptionHandler.class.isAnnotationPresent(
                                        org.springframework.web.bind.annotation.ControllerAdvice.class);

                        // Then
                        assertTrue(hasControllerAdviceAnnotation,
                                        "GlobalExceptionHandler should be annotated with @ControllerAdvice");
                }

                @Test
                @DisplayName("Should have exception handler methods annotated with @ExceptionHandler")
                void shouldHaveExceptionHandlerMethodsAnnotatedWithExceptionHandler() throws NoSuchMethodException {
                        // Given & When & Then
                        assertTrue(GlobalExceptionHandler.class
                                        .getMethod("handleValidationExceptions", MethodArgumentNotValidException.class,
                                                        WebRequest.class)
                                        .isAnnotationPresent(
                                                        org.springframework.web.bind.annotation.ExceptionHandler.class));

                        assertTrue(GlobalExceptionHandler.class
                                        .getMethod("handleConstraintViolationException",
                                                        ConstraintViolationException.class,
                                                        WebRequest.class)
                                        .isAnnotationPresent(
                                                        org.springframework.web.bind.annotation.ExceptionHandler.class));

                        assertTrue(GlobalExceptionHandler.class
                                        .getMethod("handleIllegalArgumentException", IllegalArgumentException.class,
                                                        WebRequest.class)
                                        .isAnnotationPresent(
                                                        org.springframework.web.bind.annotation.ExceptionHandler.class));

                        assertTrue(GlobalExceptionHandler.class
                                        .getMethod("handleRuntimeException", RuntimeException.class, WebRequest.class)
                                        .isAnnotationPresent(
                                                        org.springframework.web.bind.annotation.ExceptionHandler.class));
                }
        }

        @Nested
        @DisplayName("Response Structure Tests")
        class ResponseStructureTests {

                @Test
                @DisplayName("Should include standard fields in error responses")
                void shouldIncludeStandardFieldsInErrorResponses() {
                        // Given
                        IllegalArgumentException exception = new IllegalArgumentException("Test error");

                        // When
                        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                                        .handleIllegalArgumentException(exception, webRequest);

                        // Then
                        Map<String, Object> responseBody = response.getBody();
                        assertNotNull(responseBody);
                        assertTrue(responseBody.containsKey("timestamp"));
                        assertTrue(responseBody.containsKey("status"));
                        assertTrue(responseBody.containsKey("error"));
                        assertTrue(responseBody.containsKey("message"));
                        assertTrue(responseBody.containsKey("path"));
                }

                @Test
                @DisplayName("Should clean URI format in path field")
                void shouldCleanUriFormatInPathField() {
                        // Given
                        when(webRequest.getDescription(false)).thenReturn("uri=/api/test/endpoint");
                        IllegalArgumentException exception = new IllegalArgumentException("Test error");

                        // When
                        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                                        .handleIllegalArgumentException(exception, webRequest);

                        // Then
                        Map<String, Object> responseBody = response.getBody();
                        assertNotNull(responseBody);
                        assertEquals("/api/test/endpoint", responseBody.get("path"));
                }

                @Test
                @DisplayName("Should handle empty URI description")
                void shouldHandleEmptyUriDescription() {
                        // Given
                        when(webRequest.getDescription(false)).thenReturn("");
                        IllegalArgumentException exception = new IllegalArgumentException("Test error");

                        // When
                        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                                        .handleIllegalArgumentException(exception, webRequest);

                        // Then
                        Map<String, Object> responseBody = response.getBody();
                        assertNotNull(responseBody);
                        assertEquals("", responseBody.get("path"));
                }
        }

        @Nested
        @DisplayName("Integration and Edge Cases Tests")
        class IntegrationEdgeCasesTests {

                @Test
                @DisplayName("Should handle cascaded exceptions properly")
                void shouldHandleCascadedExceptionsProperly() {
                        // Given - Create a RuntimeException that would match multiple patterns
                        RuntimeException exception = new RuntimeException("Resource no encontrado but also duplicado");

                        // When
                        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleRuntimeException(
                                        exception,
                                        webRequest);

                        // Then - Should match the first pattern (no encontrado -> NOT_FOUND)
                        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                }

                @Test
                @DisplayName("Should handle concurrent exception handling")
                void shouldHandleConcurrentExceptionHandling() {
                        // Given
                        IllegalArgumentException exception = new IllegalArgumentException("Concurrent test");

                        // When & Then - Multiple calls should work independently
                        assertDoesNotThrow(() -> {
                                ResponseEntity<Map<String, Object>> response1 = globalExceptionHandler
                                                .handleIllegalArgumentException(exception, webRequest);
                                ResponseEntity<Map<String, Object>> response2 = globalExceptionHandler
                                                .handleIllegalArgumentException(exception, webRequest);

                                assertEquals(response1.getStatusCode(), response2.getStatusCode());

                                Map<String, Object> body1 = response1.getBody();
                                Map<String, Object> body2 = response2.getBody();
                                assertNotNull(body1);
                                assertNotNull(body2);
                                assertEquals(body1.get("message"), body2.get("message"));
                        });
                }

                @Test
                @DisplayName("Should maintain performance under exception load")
                void shouldMaintainPerformanceUnderExceptionLoad() {
                        // Given
                        IllegalArgumentException exception = new IllegalArgumentException("Performance test");

                        // When & Then - Should handle multiple exceptions quickly
                        long startTime = System.currentTimeMillis();

                        for (int i = 0; i < 100; i++) {
                                ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                                                .handleIllegalArgumentException(exception, webRequest);
                                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                        }

                        long duration = System.currentTimeMillis() - startTime;
                        assertTrue(duration < 5000, "Should handle 100 exceptions in less than 5 seconds");
                }
        }
}
