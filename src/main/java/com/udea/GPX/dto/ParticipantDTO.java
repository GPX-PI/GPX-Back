package com.udea.GPX.dto;

import java.time.LocalDateTime;

public class ParticipantDTO {
  private Long eventVehicleId;
  private Long userId;
  private String userName;
  private String userPicture;
  private String teamName;
  private Long vehicleId;
  private String vehicleName;
  private String vehiclePlates;
  private String vehicleSoat;
  private Long categoryId;
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