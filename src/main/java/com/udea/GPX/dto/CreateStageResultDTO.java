package com.udea.GPX.dto;

import java.time.LocalDateTime;

public class CreateStageResultDTO {
  private Long stageId;
  private Long vehicleId;
  private LocalDateTime timestamp;
  private Double latitude;
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