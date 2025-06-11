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

  /**
   * Maneja errores de validación Bean Validation (@Valid)
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationExceptions(
      MethodArgumentNotValidException ex, WebRequest request) {

    Map<String, Object> response = new HashMap<>();
    Map<String, String> fieldErrors = new HashMap<>();

    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      fieldErrors.put(fieldName, errorMessage);
    });

    response.put("timestamp", LocalDateTime.now());
    response.put("status", HttpStatus.BAD_REQUEST.value());
    response.put("error", "Error de validación");
    response.put("message", "Los datos enviados no son válidos");
    response.put("fieldErrors", fieldErrors);
    response.put("path", request.getDescription(false).replace("uri=", ""));

    logger.warn("Error de validación en {}: {}", request.getDescription(false), fieldErrors);

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

    response.put("timestamp", LocalDateTime.now());
    response.put("status", HttpStatus.BAD_REQUEST.value());
    response.put("error", "Violación de restricciones");
    response.put("message", "Los datos no cumplen las restricciones requeridas");
    response.put("violations", violations);
    response.put("path", request.getDescription(false).replace("uri=", ""));

    logger.warn("Violación de restricciones en {}: {}", request.getDescription(false), violations);

    return ResponseEntity.badRequest().body(response);
  }

  /**
   * Maneja excepciones de argumentos ilegales
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
      IllegalArgumentException ex, WebRequest request) {

    Map<String, Object> response = new HashMap<>();
    response.put("timestamp", LocalDateTime.now());
    response.put("status", HttpStatus.BAD_REQUEST.value());
    response.put("error", "Argumento inválido");
    response.put("message", ex.getMessage());
    response.put("path", request.getDescription(false).replace("uri=", ""));

    logger.warn("Argumento inválido en {}: {}", request.getDescription(false), ex.getMessage());

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
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", "Recurso no encontrado");
        response.put("message", ex.getMessage());
        response.put("path", request.getDescription(false).replace("uri=", ""));

        logger.warn("Recurso no encontrado en {}: {}", request.getDescription(false), ex.getMessage());

        return ResponseEntity.notFound().build();
      }

      if (ex.getMessage().contains("ya existe") || ex.getMessage().contains("duplicado")) {
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.CONFLICT.value());
        response.put("error", "Conflicto de datos");
        response.put("message", ex.getMessage());
        response.put("path", request.getDescription(false).replace("uri=", ""));

        logger.warn("Conflicto de datos en {}: {}", request.getDescription(false), ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
      }
    }

    // RuntimeException genérica
    response.put("timestamp", LocalDateTime.now());
    response.put("status", HttpStatus.BAD_REQUEST.value());
    response.put("error", "Error de procesamiento");
    response.put("message", ex.getMessage() != null ? ex.getMessage() : "Error interno de procesamiento");
    response.put("path", request.getDescription(false).replace("uri=", ""));

    logger.error("Error de runtime en {}: {}", request.getDescription(false), ex.getMessage(), ex);

    return ResponseEntity.badRequest().body(response);
  }

  /**
   * Maneja excepciones específicas de operaciones de archivos
   */
  @ExceptionHandler(FileOperationException.class)
  public ResponseEntity<Map<String, Object>> handleFileOperationException(
      FileOperationException ex, WebRequest request) {

    Map<String, Object> response = new HashMap<>();
    response.put("timestamp", LocalDateTime.now());
    response.put("path", request.getDescription(false).replace("uri=", ""));
    response.put("errorType", ex.getErrorType().getCode());
    response.put("errorCategory", "FILE_OPERATION");

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
      case INVALID_FORMAT:
      case FILE_EMPTY:
      case VALIDATION_ERROR:
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Error de validación de archivo");
        response.put("message", ex.getErrorType().getDescription());
        logger.warn("Error de validación de archivo en {}: {}", request.getDescription(false), ex.getMessage());
        return ResponseEntity.badRequest().body(response);

      case FILE_TOO_LARGE:
        response.put("status", HttpStatus.PAYLOAD_TOO_LARGE.value());
        response.put("error", "Archivo demasiado grande");
        response.put("message", "El archivo excede el tamaño máximo permitido");
        logger.warn("Archivo demasiado grande en {}: {}", request.getDescription(false), ex.getMessage());
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(response);

      case MALICIOUS_CONTENT:
      case ACCESS_DENIED:
        response.put("status", HttpStatus.FORBIDDEN.value());
        response.put("error", "Acceso denegado");
        response.put("message", "Operación de archivo no permitida");
        logger.warn("Acceso denegado a archivo en {}: {}", request.getDescription(false), ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);

      case FILE_NOT_FOUND:
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", "Archivo no encontrado");
        response.put("message", "El archivo solicitado no existe");
        logger.warn("Archivo no encontrado en {}: {}", request.getDescription(false), ex.getMessage());
        return ResponseEntity.notFound().build();

      case STORAGE_ERROR:
      case DELETION_ERROR:
      case PROCESSING_ERROR:
      default:
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", "Error de servidor");
        response.put("message", "Error interno al procesar archivo. Contacte al administrador.");
        logger.error("Error interno de archivo en {}: {}", request.getDescription(false), ex.getMessage(), ex);
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
    response.put("timestamp", LocalDateTime.now());
    response.put("status", HttpStatus.PAYLOAD_TOO_LARGE.value());
    response.put("error", "Archivo demasiado grande");
    response.put("message", "El archivo excede el tamaño máximo permitido (10MB)");
    response.put("errorType", "FILE_TOO_LARGE");
    response.put("errorCategory", "FILE_OPERATION");
    response.put("path", request.getDescription(false).replace("uri=", ""));

    logger.warn("Archivo demasiado grande en {}: {}", request.getDescription(false), ex.getMessage());

    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(response);
  }

  /**
   * Maneja excepciones no controladas
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGenericException(
      Exception ex, WebRequest request) {

    Map<String, Object> response = new HashMap<>();
    response.put("timestamp", LocalDateTime.now());
    response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
    response.put("error", "Error interno del servidor");
    response.put("message", "Ha ocurrido un error inesperado. Por favor contacte al administrador.");
    response.put("path", request.getDescription(false).replace("uri=", ""));

    // Log completo del error para debugging
    logger.error("Error interno no controlado en {}: {}", request.getDescription(false), ex.getMessage(), ex);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }
}