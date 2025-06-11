package com.udea.gpx.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO genérico para respuestas de la API
 */
@Schema(description = "Respuesta estándar de la API")
public class ApiResponseDTO<T> {

  @Schema(description = "Indica si la operación fue exitosa", example = "true")
  private boolean success;

  @Schema(description = "Mensaje descriptivo de la operación", example = "Operación completada exitosamente")
  private String message;

  @Schema(description = "Datos de la respuesta")
  private T data;

  @Schema(description = "Timestamp de la respuesta", example = "1640995200000")
  private long timestamp;

  public ApiResponseDTO() {
    this.timestamp = System.currentTimeMillis();
  }

  public ApiResponseDTO(boolean success, String message, T data) {
    this.success = success;
    this.message = message;
    this.data = data;
    this.timestamp = System.currentTimeMillis();
  }

  public static <T> ApiResponseDTO<T> success(String message, T data) {
    return new ApiResponseDTO<>(true, message, data);
  }

  public static <T> ApiResponseDTO<T> error(String message) {
    return new ApiResponseDTO<>(false, message, null);
  }

  // Getters y Setters
  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }
}