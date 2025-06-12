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
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.udea.gpx.exception.FileOperationException;

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
  private static final String ERROR_CATEGORY = "errorCategory";
  private static final String ERROR_TYPE = "errorType";
  private static final String FILE_OPERATION = "FILE_OPERATION";
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
    } // RuntimeException genérica
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
   * Maneja excepciones específicas de operaciones de archivos
   */
  @ExceptionHandler(FileOperationException.class)
  public ResponseEntity<Map<String, Object>> handleFileOperationException(
      FileOperationException ex, WebRequest request) {
    Map<String, Object> response = new HashMap<>();
    response.put(TIMESTAMP, LocalDateTime.now());
    response.put(PATH, request.getDescription(false).replace(URI_PREFIX, ""));
    response.put(ERROR_TYPE, ex.getErrorType().getCode());
    response.put(ERROR_CATEGORY, FILE_OPERATION);

    // Detalles adicionales sin exponer información sensible
    Map<String, Object> fileInfo = new HashMap<>();
    if (ex.getOriginalFileName() != null && !ex.getOriginalFileName().contains("/")
        && !ex.getOriginalFileName().contains("\\")) {
      fileInfo.put("filename", ex.getOriginalFileName());
    }
    if (ex.getOperation() != null) {
      fileInfo.put("operation", ex.getOperation());
    }
    response.put("fileInfo", fileInfo);

    // Mapear tipos de error a códigos HTTP y mensajes apropiados
    switch (ex.getErrorType()) {
      case INVALID_FORMAT, FILE_EMPTY, VALIDATION_ERROR:
        response.put(STATUS, HttpStatus.BAD_REQUEST.value());
        response.put(ERROR, "Error de validación de archivo");
        response.put(MESSAGE, ex.getErrorType().getDescription());
        if (logger.isWarnEnabled()) {
          logger.warn("Error de validación de archivo en {}: {}", request.getDescription(false), ex.getMessage());
        }
        return ResponseEntity.badRequest().body(response);
      case FILE_TOO_LARGE:
        response.put(STATUS, HttpStatus.PAYLOAD_TOO_LARGE.value());
        response.put(ERROR, "Archivo demasiado grande");
        response.put(MESSAGE, "El archivo excede el tamaño máximo permitido");
        if (logger.isWarnEnabled()) {
          logger.warn("Archivo demasiado grande en {}: {}", request.getDescription(false), ex.getMessage());

        }
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(response);
      case MALICIOUS_CONTENT, ACCESS_DENIED:
        response.put(STATUS, HttpStatus.FORBIDDEN.value());
        response.put(ERROR, "Acceso denegado");
        response.put(MESSAGE, "Operación de archivo no permitida");
        if (logger.isWarnEnabled()) {
          logger.warn("Acceso denegado a archivo en {}: {}", request.getDescription(false), ex.getMessage());
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
      case FILE_NOT_FOUND:
        response.put(STATUS, HttpStatus.NOT_FOUND.value());
        response.put(ERROR, "Archivo no encontrado");
        response.put(MESSAGE, "El archivo solicitado no existe");
        if (logger.isWarnEnabled()) {
          logger.warn("Archivo no encontrado en {}: {}", request.getDescription(false), ex.getMessage());
        }
        return ResponseEntity.notFound().build();
      case STORAGE_ERROR, DELETION_ERROR, PROCESSING_ERROR:
      default:
        response.put(STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put(ERROR, "Error de servidor");
        response.put(MESSAGE, "Error interno al procesar archivo. Contacte al administrador.");
        if (logger.isErrorEnabled()) {
          logger.error("Error interno de archivo en {}: {}", request.getDescription(false), ex.getMessage(), ex);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  /**
   * Maneja excepciones de tamaño de archivo excedido
   */
  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<Map<String, Object>> handleMaxUploadSizeExceededException(
      MaxUploadSizeExceededException ex, WebRequest request) {
    Map<String, Object> response = new HashMap<>();
    response.put(TIMESTAMP, LocalDateTime.now());
    response.put(STATUS, HttpStatus.PAYLOAD_TOO_LARGE.value());
    response.put(ERROR, "Archivo demasiado grande");
    response.put(MESSAGE, "El archivo excede el tamaño máximo permitido (10MB)");
    response.put(ERROR_TYPE, "FILE_TOO_LARGE");
    response.put(ERROR_CATEGORY, FILE_OPERATION);
    response.put(PATH, request.getDescription(false).replace(URI_PREFIX, ""));
    if (logger.isErrorEnabled()) {
      logger.warn("Archivo demasiado grande en {}: {}", request.getDescription(false), ex.getMessage());
    }
    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(response);
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