package com.udea.gpx.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

public class CreateStageResultDTO {

  @NotNull(message = "El ID de la etapa es obligatorio")
  @Positive(message = "El ID de la etapa debe ser un número positivo")
  private Long stageId;

  @NotNull(message = "El ID del vehículo es obligatorio")
  @Positive(message = "El ID del vehículo debe ser un número positivo")
  private Long vehicleId;

  @NotNull(message = "La fecha y hora es obligatoria")
  private LocalDateTime timestamp;

  @DecimalMin(value = "-90.0", message = "La latitud debe estar entre -90 y 90 grados")
  @DecimalMax(value = "90.0", message = "La latitud debe estar entre -90 y 90 grados")
  private Double latitude;

  @DecimalMin(value = "-180.0", message = "La longitud debe estar entre -180 y 180 grados")
  @DecimalMax(value = "180.0", message = "La longitud debe estar entre -180 y 180 grados")
  private Double longitude;

  public CreateStageResultDTO() {
  }

  public CreateStageResultDTO(Long stageId, Long vehicleId, LocalDateTime timestamp, Double latitude,
      Double longitude) {
    this.stageId = stageId;
    this.vehicleId = vehicleId;
    this.timestamp = timestamp;
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public Long getStageId() {
    return stageId;
  }

  public void setStageId(Long stageId) {
    this.stageId = stageId;
  }

  public Long getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(Long vehicleId) {
    this.vehicleId = vehicleId;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public Double getLatitude() {
    return latitude;
  }

  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  public Double getLongitude() {
    return longitude;
  }

  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }
}