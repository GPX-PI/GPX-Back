package com.udea.gpx.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para respuestas de autenticación
 */
@Schema(description = "Respuesta de autenticación con tokens JWT")
public class AuthResponseDTO {

  @Schema(description = "Token de acceso JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
  private String accessToken;

  @Schema(description = "Token de actualización", example = "refresh-token-uuid-12345")
  private String refreshToken;

  @Schema(description = "Token de acceso (compatibilidad)", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
  private String token;

  @Schema(description = "ID del usuario", example = "1")
  private Long userId;

  @Schema(description = "Si el usuario es administrador", example = "false")
  private boolean admin;

  @Schema(description = "Proveedor de autenticación", example = "LOCAL", allowableValues = { "LOCAL", "GOOGLE" })
  private String authProvider;

  @Schema(description = "Si el perfil está completo", example = "true")
  private boolean profileComplete;

  @Schema(description = "Nombre del usuario", example = "Juan")
  private String firstName;

  @Schema(description = "URL de la foto de perfil", example = "https://example.com/photo.jpg")
  private String picture;

  @Schema(description = "Mensaje adicional", example = "Login exitoso")
  private String message;

  public AuthResponseDTO() {
  }

  // Getters y Setters
  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public boolean isAdmin() {
    return admin;
  }

  public void setAdmin(boolean admin) {
    this.admin = admin;
  }

  public String getAuthProvider() {
    return authProvider;
  }

  public void setAuthProvider(String authProvider) {
    this.authProvider = authProvider;
  }

  public boolean isProfileComplete() {
    return profileComplete;
  }

  public void setProfileComplete(boolean profileComplete) {
    this.profileComplete = profileComplete;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getPicture() {
    return picture;
  }

  public void setPicture(String picture) {
    this.picture = picture;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}