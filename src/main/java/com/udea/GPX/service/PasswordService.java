package com.udea.gpx.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Servicio para el manejo seguro de contraseñas
 */
@Service
public class PasswordService {

  private final BCryptPasswordEncoder passwordEncoder;

  public PasswordService() {
    // BCrypt con strength 12 para mayor seguridad
    this.passwordEncoder = new BCryptPasswordEncoder(12);
  }

  /**
   * Hashea una contraseña en texto plano
   */
  public String hashPassword(String rawPassword) {
    if (rawPassword == null || rawPassword.trim().isEmpty()) {
      throw new IllegalArgumentException("La contraseña no puede estar vacía");
    }
    return passwordEncoder.encode(rawPassword.trim());
  }

  /**
   * Verifica si una contraseña en texto plano coincide con el hash
   */
  public boolean verifyPassword(String rawPassword, String hashedPassword) {
    if (rawPassword == null || hashedPassword == null) {
      return false;
    }
    return passwordEncoder.matches(rawPassword.trim(), hashedPassword);
  }

  /**
   * Verifica si una contraseña cumple con los requisitos de seguridad
   */
  public boolean isPasswordValid(String password) {
    if (password == null || password.trim().isEmpty()) {
      return false;
    }

    String trimmedPassword = password.trim();

    // Mínimo 8 caracteres
    if (trimmedPassword.length() < 8) {
      return false;
    }

    // Máximo 128 caracteres (BCrypt limit)
    if (trimmedPassword.length() > 128) {
      return false;
    }

    // Al menos una letra
    if (!trimmedPassword.matches(".*[a-zA-Z].*")) {
      return false;
    }

    // Al menos un número
    if (!trimmedPassword.matches(".*\\d.*")) {
      return false;
    }

    return true;
  }

  /**
   * Obtiene los mensajes de error para contraseñas inválidas
   */
  public String getPasswordValidationMessage(String password) {
    if (password == null || password.trim().isEmpty()) {
      return "La contraseña es requerida";
    }

    String trimmedPassword = password.trim();

    if (trimmedPassword.length() < 8) {
      return "La contraseña debe tener al menos 8 caracteres";
    }

    if (trimmedPassword.length() > 128) {
      return "La contraseña no puede tener más de 128 caracteres";
    }

    if (!trimmedPassword.matches(".*[a-zA-Z].*")) {
      return "La contraseña debe contener al menos una letra";
    }

    if (!trimmedPassword.matches(".*\\d.*")) {
      return "La contraseña debe contener al menos un número";
    }

    return "Contraseña válida";
  }

  /**
   * Verifica si un hash de contraseña está usando el algoritmo BCrypt
   */
  public boolean isBCryptHash(String password) {
    return password != null && (password.startsWith("$2a$") || password.startsWith("$2b$")
        || password.startsWith("$2y$"));
  }
}