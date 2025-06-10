package com.udea.GPX.dto;

import com.udea.GPX.validation.Sanitized;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class ParticipantDTO {
  @NotNull(message = "El ID del evento-vehículo es obligatorio")
  @Positive(message = "El ID del evento-vehículo debe ser positivo")
  private Long eventVehicleId;

  @NotNull(message = "El ID del usuario es obligatorio")
  @Positive(message = "El ID del usuario debe ser positivo")
  private Long userId;

  @NotBlank(message = "El nombre del usuario es obligatorio")
  @Size(max = 100, message = "El nombre del usuario no puede exceder 100 caracteres")
  @Sanitized(value = Sanitized.SanitizationType.NAME, allowNull = false)
  private String userName;

  @Sanitized(value = Sanitized.SanitizationType.URL, allowNull = true)
  private String userPicture;

  @Size(max = 100, message = "El nombre del equipo no puede exceder 100 caracteres")
  @Sanitized(value = Sanitized.SanitizationType.NAME, allowNull = true)
  private String teamName;

  @NotNull(message = "El ID del vehículo es obligatorio")
  @Positive(message = "El ID del vehículo debe ser positivo")
  private Long vehicleId;

  @NotBlank(message = "El nombre del vehículo es obligatorio")
  @Size(max = 100, message = "El nombre del vehículo no puede exceder 100 caracteres")
  @Sanitized(value = Sanitized.SanitizationType.TEXT, allowNull = false)
  private String vehicleName;

  @NotBlank(message = "Las placas del vehículo son obligatorias")
  @Size(max = 20, message = "Las placas no pueden exceder 20 caracteres")
  @Sanitized(value = Sanitized.SanitizationType.TEXT, allowNull = false)
  private String vehiclePlates;

  @Size(max = 50, message = "El SOAT no puede exceder 50 caracteres")
  @Sanitized(value = Sanitized.SanitizationType.IDENTIFICATION, allowNull = true)
  private String vehicleSoat;

  @NotNull(message = "El ID de la categoría es obligatorio")
  @Positive(message = "El ID de la categoría debe ser positivo")
  private Long categoryId;

  @NotBlank(message = "El nombre de la categoría es obligatorio")
  @Size(max = 100, message = "El nombre de la categoría no puede exceder 100 caracteres")
  @Sanitized(value = Sanitized.SanitizationType.NAME, allowNull = false)
  private String categoryName;

  private LocalDateTime registrationDate;

  // Constructor vacío
  public ParticipantDTO() {
  }

  // Constructor completo
  public ParticipantDTO(Long eventVehicleId, Long userId, String userName, String userPicture, String teamName,
      Long vehicleId, String vehicleName, String vehiclePlates, String vehicleSoat,
      Long categoryId, String categoryName, LocalDateTime registrationDate) {
    this.eventVehicleId = eventVehicleId;
    this.userId = userId;
    this.userName = userName;
    this.userPicture = userPicture;
    this.teamName = teamName;
    this.vehicleId = vehicleId;
    this.vehicleName = vehicleName;
    this.vehiclePlates = vehiclePlates;
    this.vehicleSoat = vehicleSoat;
    this.categoryId = categoryId;
    this.categoryName = categoryName;
    this.registrationDate = registrationDate;
  }

  // Getters y Setters
  public Long getEventVehicleId() {
    return eventVehicleId;
  }

  public void setEventVehicleId(Long eventVehicleId) {
    this.eventVehicleId = eventVehicleId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getUserPicture() {
    return userPicture;
  }

  public void setUserPicture(String userPicture) {
    this.userPicture = userPicture;
  }

  public String getTeamName() {
    return teamName;
  }

  public void setTeamName(String teamName) {
    this.teamName = teamName;
  }

  public Long getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(Long vehicleId) {
    this.vehicleId = vehicleId;
  }

  public String getVehicleName() {
    return vehicleName;
  }

  public void setVehicleName(String vehicleName) {
    this.vehicleName = vehicleName;
  }

  public String getVehiclePlates() {
    return vehiclePlates;
  }

  public void setVehiclePlates(String vehiclePlates) {
    this.vehiclePlates = vehiclePlates;
  }

  public String getVehicleSoat() {
    return vehicleSoat;
  }

  public void setVehicleSoat(String vehicleSoat) {
    this.vehicleSoat = vehicleSoat;
  }

  public Long getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Long categoryId) {
    this.categoryId = categoryId;
  }

  public String getCategoryName() {
    return categoryName;
  }

  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }

  public LocalDateTime getRegistrationDate() {
    return registrationDate;
  }

  public void setRegistrationDate(LocalDateTime registrationDate) {
    this.registrationDate = registrationDate;
  }
}