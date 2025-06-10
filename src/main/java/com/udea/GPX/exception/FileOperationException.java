package com.udea.GPX.exception;

/**
 * Excepción específica para operaciones de archivos
 */
public class FileOperationException extends RuntimeException {

  private final FileErrorType errorType;
  private final String originalFileName;
  private final String operation;

  public FileOperationException(String message, FileErrorType errorType) {
    super(message);
    this.errorType = errorType;
    this.originalFileName = null;
    this.operation = null;
  }

  public FileOperationException(String message, FileErrorType errorType, String originalFileName, String operation) {
    super(message);
    this.errorType = errorType;
    this.originalFileName = originalFileName;
    this.operation = operation;
  }

  public FileOperationException(String message, Throwable cause, FileErrorType errorType) {
    super(message, cause);
    this.errorType = errorType;
    this.originalFileName = null;
    this.operation = null;
  }

  public FileOperationException(String message, Throwable cause, FileErrorType errorType, String originalFileName,
      String operation) {
    super(message, cause);
    this.errorType = errorType;
    this.originalFileName = originalFileName;
    this.operation = operation;
  }

  public FileErrorType getErrorType() {
    return errorType;
  }

  public String getOriginalFileName() {
    return originalFileName;
  }

  public String getOperation() {
    return operation;
  }

  public enum FileErrorType {
    INVALID_FORMAT("INVALID_FORMAT", "Formato de archivo no válido"),
    FILE_TOO_LARGE("FILE_TOO_LARGE", "Archivo demasiado grande"),
    FILE_EMPTY("FILE_EMPTY", "Archivo vacío"),
    MALICIOUS_CONTENT("MALICIOUS_CONTENT", "Contenido malicioso detectado"),
    STORAGE_ERROR("STORAGE_ERROR", "Error de almacenamiento"),
    VALIDATION_ERROR("VALIDATION_ERROR", "Error de validación de archivo"),
    DELETION_ERROR("DELETION_ERROR", "Error al eliminar archivo"),
    ACCESS_DENIED("ACCESS_DENIED", "Acceso denegado al archivo"),
    FILE_NOT_FOUND("FILE_NOT_FOUND", "Archivo no encontrado"),
    PROCESSING_ERROR("PROCESSING_ERROR", "Error de procesamiento de archivo");

    private final String code;
    private final String description;

    FileErrorType(String code, String description) {
      this.code = code;
      this.description = description;
    }

    public String getCode() {
      return code;
    }

    public String getDescription() {
      return description;
    }
  }
}