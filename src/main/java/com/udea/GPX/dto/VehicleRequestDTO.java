package com.udea.gpx.dto;

import com.udea.gpx.validation.Sanitized;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class VehicleRequestDTO {
  @NotBlank(message = "El nombre del vehículo es obligatorio")
  @Size(max = 100, message = "El nombre del vehículo no puede exceder 100 caracteres")
  @Sanitized(value = Sanitized.SanitizationType.TEXT, allowNull = true)
  private String name;

  @NotBlank(message = "El SOAT del vehículo es obligatorio")
  @Size(max = 50, message = "El SOAT no puede exceder 50 caracteres")
  @Sanitized(value = Sanitized.SanitizationType.IDENTIFICATION, allowNull = true)
  private String soat;

  @NotBlank(message = "Las placas del vehículo son obligatorias")
  @Size(max = 20, message = "Las placas no pueden exceder 20 caracteres")
  @Sanitized(value = Sanitized.SanitizationType.TEXT, allowNull = true)
  private String plates;

  @NotNull(message = "La categoría del vehículo es obligatoria")
  @Positive(message = "El ID de la categoría debe ser un número positivo")
  private Long categoryId;

  public VehicleRequestDTO() {
  }

  public VehicleRequestDTO(String name, String soat, String plates, Long categoryId) {
    this.name = name;
    this.soat = soat;
    this.plates = plates;
    this.categoryId = categoryId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSoat() {
    return soat;
  }

  public void setSoat(String soat) {
    this.soat = soat;
  }

  public String getPlates() {
    return plates;
  }

  public void setPlates(String plates) {
    this.plates = plates;
  }

  public Long getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Long categoryId) {
    this.categoryId = categoryId;
  }
}