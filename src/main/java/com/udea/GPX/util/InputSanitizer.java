package com.udea.GPX.util;

import java.util.regex.Pattern;

/**
 * Utilidad para sanitizar inputs de usuario y prevenir ataques comunes
 */
public class InputSanitizer {

  // Patrones de regex para validaciones
  private static final Pattern HTML_PATTERN = Pattern.compile("<[^>]+>");
  private static final Pattern SCRIPT_PATTERN = Pattern.compile("(?i)<script[^>]*>.*?</script>");
  private static final Pattern SQL_INJECTION_PATTERN = Pattern
      .compile("(?i)(union|select|insert|update|delete|drop|exec|script)");
  private static final Pattern XSS_PATTERN = Pattern.compile("(?i)(javascript:|vbscript:|onload|onerror|onclick)");

  // Caracteres peligrosos básicos
  private static final String[] DANGEROUS_CHARS = { "<", ">", "&", "\"", "'", "/", "\\" };

  /**
   * Sanitiza texto básico removiendo caracteres peligrosos
   */
  public static String sanitizeText(String input) {
    if (input == null || input.trim().isEmpty()) {
      return input;
    }

    String sanitized = input.trim();

    // Remover tags HTML
    sanitized = HTML_PATTERN.matcher(sanitized).replaceAll("");

    // Remover scripts
    sanitized = SCRIPT_PATTERN.matcher(sanitized).replaceAll("");

    // Escapar caracteres peligrosos básicos (opcional, puede ser demasiado
    // estricto)
    // for (String dangerous : DANGEROUS_CHARS) {
    // sanitized = sanitized.replace(dangerous, "");
    // }

    return sanitized;
  }

  /**
   * Sanitiza nombres (nombres, apellidos)
   */
  public static String sanitizeName(String name) {
    if (name == null || name.trim().isEmpty()) {
      return name;
    }

    String sanitized = sanitizeText(name);

    // Solo permitir letras, espacios, guiones y apostrofes para nombres
    sanitized = sanitized.replaceAll("[^a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s'-]", "");

    return sanitized.trim();
  }

  /**
   * Sanitiza email básico
   */
  public static String sanitizeEmail(String email) {
    if (email == null || email.trim().isEmpty()) {
      return email;
    }

    String sanitized = email.trim().toLowerCase();

    // Remover cualquier cosa que no sea válida en emails
    sanitized = sanitized.replaceAll("[^a-zA-Z0-9@._-]", "");

    return sanitized;
  }

  /**
   * Sanitiza números de teléfono
   */
  public static String sanitizePhone(String phone) {
    if (phone == null || phone.trim().isEmpty()) {
      return phone;
    }

    String sanitized = phone.trim();

    // Solo permitir números, espacios, guiones, paréntesis y símbolos +
    sanitized = sanitized.replaceAll("[^0-9\\s()+-]", "");

    return sanitized.trim();
  }

  /**
   * Sanitiza identificación (documento)
   */
  public static String sanitizeIdentification(String identification) {
    if (identification == null || identification.trim().isEmpty()) {
      return identification;
    }

    String sanitized = identification.trim();

    // Solo permitir números y guiones para documentos
    sanitized = sanitized.replaceAll("[^0-9-]", "");

    return sanitized;
  }

  /**
   * Sanitiza URLs/Links de redes sociales
   */
  public static String sanitizeUrl(String url) {
    if (url == null || url.trim().isEmpty()) {
      return url;
    }

    String sanitized = url.trim();

    // Verificar que no contenga scripts maliciosos
    if (XSS_PATTERN.matcher(sanitized).find()) {
      return ""; // Retornar vacío si se detecta XSS
    }

    return sanitized;
  }

  /**
   * Validación adicional para detectar intentos de inyección SQL
   */
  public static boolean containsSqlInjection(String input) {
    if (input == null)
      return false;
    return SQL_INJECTION_PATTERN.matcher(input).find();
  }

  /**
   * Validación adicional para detectar XSS
   */
  public static boolean containsXss(String input) {
    if (input == null)
      return false;
    return XSS_PATTERN.matcher(input).find() || HTML_PATTERN.matcher(input).find();
  }
}