package com.udea.gpx.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  // Constants for repeated literals
  private static final String TIMESTAMP = "timestamp";
  private static final String STATUS = "status";
  private static final String ERROR = "error";
  private static final String MESSAGE = "message";
  private static final String PATH = "path";
  private static final String URI_PREFIX = "uri=";

  /**
   * Maneja errores de validación Bean Validation (@Valid)
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationExceptions(
      MethodArgumentNotValidException ex, WebRequest request) {

    Map<String, Object> response = new HashMap<>();
    Map<String, String> fieldErrors = new HashMap<>();

    ex.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      fieldErrors.put(fieldName, errorMessage);
    });
    response.put(TIMESTAMP, LocalDateTime.now());
    response.put(STATUS, HttpStatus.BAD_REQUEST.value());
    response.put(ERROR, "Error de validación");
    response.put(MESSAGE, "Los datos enviados no son válidos");
    response.put("fieldErrors", fieldErrors);
    response.put(PATH, request.getDescription(false).replace(URI_PREFIX, ""));

    if (logger.isWarnEnabled()) {
      logger.warn("Error de validación en {}: {}", request.getDescription(false), fieldErrors);
    }

    return ResponseEntity.badRequest().body(response);
  }

  /**
   * Maneja violaciones de constraints de validación
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Map<String, Object>> handleConstraintViolationException(
      ConstraintViolationException ex, WebRequest request) {

    Map<String, Object> response = new HashMap<>();
    Map<String, String> violations = new HashMap<>();

    Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
    for (ConstraintViolation<?> violation : constraintViolations) {
      String propertyPath = violation.getPropertyPath().toString();
      String message = violation.getMessage();
      violations.put(propertyPath, message);
    }
    response.put(TIMESTAMP, LocalDateTime.now());
    response.put(STATUS, HttpStatus.BAD_REQUEST.value());
    response.put(ERROR, "Violación de restricciones");
    response.put(MESSAGE, "Los datos no cumplen las restricciones requeridas");
    response.put("violations", violations);
    response.put(PATH, request.getDescription(false).replace(URI_PREFIX, ""));

    if (logger.isWarnEnabled()) {
      logger.warn("Violación de restricciones en {}: {}", request.getDescription(false), violations);
    }

    return ResponseEntity.badRequest().body(response);
  }

  /**
   * Maneja excepciones de argumentos ilegales
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
      IllegalArgumentException ex, WebRequest request) {
    Map<String, Object> response = new HashMap<>();
    response.put(TIMESTAMP, LocalDateTime.now());
    response.put(STATUS, HttpStatus.BAD_REQUEST.value());
    response.put(ERROR, "Argumento inválido");
    response.put(MESSAGE, ex.getMessage());
    response.put(PATH, request.getDescription(false).replace(URI_PREFIX, ""));

    if (logger.isWarnEnabled()) {
      logger.warn("Argumento inválido en {}: {}", request.getDescription(false), ex.getMessage());
    }

    return ResponseEntity.badRequest().body(response);
  }

  /**
   * Maneja excepciones de runtime genéricas
   */
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<Map<String, Object>> handleRuntimeException(
      RuntimeException ex, WebRequest request) {

    Map<String, Object> response = new HashMap<>();

    // Casos especiales de RuntimeException
    if (ex.getMessage() != null) {
      if (ex.getMessage().contains("no encontrado") || ex.getMessage().contains("not found")) {
        response.put(TIMESTAMP, LocalDateTime.now());
        response.put(STATUS, HttpStatus.NOT_FOUND.value());
        response.put(ERROR, "Recurso no encontrado");
        response.put(MESSAGE, ex.getMessage());
        response.put(PATH, request.getDescription(false).replace(URI_PREFIX, ""));

        if (logger.isWarnEnabled()) {
          logger.warn("Recurso no encontrado en {}: {}", request.getDescription(false), ex.getMessage());
        }

        return ResponseEntity.notFound().build();
      }
      if (ex.getMessage().contains("ya existe") || ex.getMessage().contains("duplicado")) {
        response.put(TIMESTAMP, LocalDateTime.now());
        response.put(STATUS, HttpStatus.CONFLICT.value());
        response.put(ERROR, "Conflicto de datos");
        response.put(MESSAGE, ex.getMessage());
        response.put(PATH, request.getDescription(false).replace(URI_PREFIX, ""));

        if (logger.isWarnEnabled()) {
          logger.warn("Conflicto de datos en {}: {}", request.getDescription(false), ex.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
      }
    }

    // RuntimeException genérica
    response.put(TIMESTAMP, LocalDateTime.now());
    response.put(STATUS, HttpStatus.BAD_REQUEST.value());
    response.put(ERROR, "Error de procesamiento");
    response.put(MESSAGE, ex.getMessage() != null ? ex.getMessage() : "Error interno de procesamiento");
    response.put(PATH, request.getDescription(false).replace(URI_PREFIX, ""));

    if (logger.isErrorEnabled()) {
      logger.error("Error de runtime en {}: {}", request.getDescription(false), ex.getMessage(), ex);
    }

    return ResponseEntity.badRequest().body(response);
  }

  /**
   * Maneja excepciones no controladas
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGenericException(
      Exception ex, WebRequest request) {
    Map<String, Object> response = new HashMap<>();
    response.put(TIMESTAMP, LocalDateTime.now());
    response.put(STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value());
    response.put(ERROR, "Error interno del servidor");
    response.put(MESSAGE, "Ha ocurrido un error inesperado. Por favor contacte al administrador.");
    response.put(PATH, request.getDescription(false).replace(URI_PREFIX, ""));

    // Log completo del error para debugging
    if (logger.isErrorEnabled()) {
      logger.error("Error interno no controlado en {}: {}", request.getDescription(false), ex.getMessage(), ex);
    }

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }
}