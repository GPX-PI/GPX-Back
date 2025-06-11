package com.udea.gpx.dto;

import com.udea.gpx.validation.Sanitized;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequestDTO {
  @NotBlank(message = "El email es requerido")
  @Email(message = "El email debe tener un formato válido")
  @Sanitized(value = Sanitized.SanitizationType.EMAIL, allowNull = false)
  private String email;

  @NotBlank(message = "La contraseña es requerida")
  @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
  @Sanitized(value = Sanitized.SanitizationType.TEXT, allowNull = false)
  private String password;

  public LoginRequestDTO() {
  }

  public LoginRequestDTO(String email, String password) {
    this.email = email;
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}