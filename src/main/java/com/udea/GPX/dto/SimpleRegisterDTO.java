package com.udea.GPX.dto;

import com.udea.GPX.validation.Sanitized;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SimpleRegisterDTO {
  @NotBlank(message = "El nombre es requerido")
  @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
  @Sanitized(value = Sanitized.SanitizationType.NAME, allowNull = true)
  private String firstName;

  @NotBlank(message = "El apellido es requerido")
  @Size(max = 100, message = "El apellido no puede exceder 100 caracteres")
  @Sanitized(value = Sanitized.SanitizationType.NAME, allowNull = true)
  private String lastName;
  @NotBlank(message = "El email es requerido")
  @Sanitized(value = Sanitized.SanitizationType.EMAIL, allowNull = true)
  private String email;

  @NotBlank(message = "La contraseña es requerida")
  @Size(min = 6, max = 100, message = "La contraseña debe tener entre 6 y 100 caracteres")
  @Sanitized(value = Sanitized.SanitizationType.TEXT, allowNull = true)
  private String password;

  public SimpleRegisterDTO() {
  }

  public SimpleRegisterDTO(String firstName, String lastName, String email, String password) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.password = password;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
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