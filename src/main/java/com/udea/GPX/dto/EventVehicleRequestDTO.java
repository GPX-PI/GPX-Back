package com.udea.gpx.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class EventVehicleRequestDTO {

  @NotNull(message = "El ID del evento es obligatorio")
  @Positive(message = "El ID del evento debe ser un número positivo")
  private Long eventId;

  @NotNull(message = "El ID del vehículo es obligatorio")
  @Positive(message = "El ID del vehículo debe ser un número positivo")
  private Long vehicleId;

  // Constructor vacío
  public EventVehicleRequestDTO() {
  }

  // Constructor completo
  public EventVehicleRequestDTO(Long eventId, Long vehicleId) {
    this.eventId = eventId;
    this.vehicleId = vehicleId;
  }

  // Getters y Setters
  public Long getEventId() {
    return eventId;
  }

  public void setEventId(Long eventId) {
    this.eventId = eventId;
  }

  public Long getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(Long vehicleId) {
    this.vehicleId = vehicleId;
  }
}
