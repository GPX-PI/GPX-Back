package com.udea.gpx.util;

import java.util.regex.Pattern;

/**
 * Clase utilitaria para sanitización de inputs básica sin dependencias externas
 * para prevenir ataques XSS, SQL injection y otros.
 */
public final class InputSanitizer {

  // Constructor privado para evitar instanciación
  private InputSanitizer() {
    throw new IllegalStateException("Utility class");
  }

  // Patrones para detectar posibles ataques
  private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
      "(?i).*(\\b(union|select|insert|delete|drop|create|alter|exec|execute)\\b).*");
  private static final Pattern XSS_ATTACK_PATTERN = Pattern.compile(
      "(?i).*(javascript:(?!void\\(0\\))|vbscript:|data:(text/html|.*script)).*");

  private static final Pattern XSS_PATTERN = Pattern.compile(
      "(?i).*(on\\w+\\s*=|<iframe|<object|<embed|<link|<meta).*");
  private static final Pattern PATH_TRAVERSAL_PATTERN = Pattern.compile(
      ".*(\\.\\./|\\.\\.\\\\|%2e%2e%2f|%2e%2e\\\\).*");

  /**
   * Sanitiza texto removiendo caracteres peligrosos
   * 
   * @param input texto de entrada
   * @return texto sanitizado
   */
  public static String sanitizeText(String input) {
    if (input == null) {
      return null;
    }

    String trimmed = input.trim();

    // Check for serious SQL injection patterns that should be rejected
    if (SQL_INJECTION_PATTERN.matcher(trimmed).matches()) {
      throw new IllegalArgumentException("Input contiene patrones sospechosos de SQL injection");
    } // Check for serious XSS attacks that should be rejected
    if (XSS_ATTACK_PATTERN.matcher(trimmed).matches()) {
      throw new IllegalArgumentException("Input contiene patrones sospechosos de XSS");
    }

    if (PATH_TRAVERSAL_PATTERN.matcher(trimmed).matches()) {
      throw new IllegalArgumentException("Input contiene patrones de path traversal");
    } // Clean/sanitize the content (remove tags, scripts, etc.)
    String sanitized = trimmed
        .replaceAll("<script[^>]*>.*?</script>", "")
        .replaceAll("<[^>]*+>", "")
        .replace("javascript:", "")
        .replace("vbscript:", "")
        .replaceAll("onload\\s*=", "")
        .replaceAll("onerror\\s*=", "");

    // Check for remaining XSS patterns after cleaning
    if (XSS_PATTERN.matcher(sanitized).matches()) {
      throw new IllegalArgumentException("Input contiene patrones sospechosos de XSS");
    }

    return sanitized;
  }

  /**
   * Valida y sanitiza email
   * 
   * @param email email de entrada
   * @return email sanitizado
   */
  public static String sanitizeEmail(String email) {
    if (email == null) {
      return null;
    }
    String sanitized = sanitizeText(email.toLowerCase().trim());

    // Verificar que sanitized no sea null
    if (sanitized == null) {
      throw new IllegalArgumentException("El email no puede ser procesado");
    }

    // Validación básica de formato de email - domain must have at least one dot
    if (!sanitized.matches("^[A-Za-z0-9+_.\\-]+@[A-Za-z0-9\\-]+\\.[A-Za-z0-9\\-.]+$")) {
      throw new IllegalArgumentException("El email debe tener un formato válido");
    }

    return sanitized;
  }

  /**
   * Sanitiza nombres (solo letras, espacios y algunos caracteres especiales)
   * 
   * @param name nombre de entrada
   * @return nombre sanitizado
   */
  public static String sanitizeName(String name) {
    if (name == null) {
      return null;
    }
    String sanitized = sanitizeText(name);

    // Verificar que sanitized no sea null
    if (sanitized == null) {
      throw new IllegalArgumentException("El nombre no puede ser procesado");
    }

    // Solo permitir letras, espacios, guiones y apostrofes (incluye caracteres
    // internacionales)
    if (!sanitized.matches("^[\\p{L}\\s'-]+$")) {
      throw new IllegalArgumentException("El nombre contiene caracteres no válidos");
    }

    return sanitized;
  }

  /**
   * Sanitiza URLs validando que sean seguras
   * 
   * @param url URL de entrada
   * @return URL sanitizada
   */
  public static String sanitizeUrl(String url) {
    if (url == null) {
      return null;
    }

    String sanitized = sanitizeText(url);

    // Verificar que sanitized no sea null
    if (sanitized == null) {
      throw new IllegalArgumentException("La URL no puede ser procesada");
    }

    // Solo permitir HTTP y HTTPS
    if (!sanitized.matches("^https?://[A-Za-z0-9.-]+(/[A-Za-z0-9./_-]*)?$")) {
      throw new IllegalArgumentException("URL no válida o protocolo no permitido");
    }

    return sanitized;
  }

  /**
   * Sanitiza números de teléfono
   * 
   * @param phone teléfono de entrada
   * @return teléfono sanitizado
   */
  public static String sanitizePhone(String phone) {
    if (phone == null) {
      return null;
    }

    String sanitized = sanitizeText(phone);

    // Verificar que sanitized no sea null
    if (sanitized == null) {
      throw new IllegalArgumentException("El teléfono no puede ser procesado");
    }

    // Solo permitir números, espacios, guiones, paréntesis y el símbolo +
    if (!sanitized.matches("^[0-9\\s()+-]+$")) {
      throw new IllegalArgumentException("El teléfono contiene caracteres no válidos");
    }

    return sanitized;
  }

  /**
   * Sanitiza identificaciones (solo números y letras)
   * 
   * @param identification identificación de entrada
   * @return identificación sanitizada
   */
  public static String sanitizeIdentification(String identification) {
    if (identification == null) {
      return null;
    }

    String sanitized = sanitizeText(identification);

    // Verificar que sanitized no sea null
    if (sanitized == null) {
      throw new IllegalArgumentException("La identificación no puede ser procesada");
    }

    // Allow alphanumeric with underscores and hyphens, but reject purely numeric
    // with hyphens
    if (!sanitized.matches("^[A-Za-z0-9_-]+$")) {
      throw new IllegalArgumentException("La identificación contiene caracteres no válidos");
    }

    // Reject purely numeric strings with hyphens (like SSN format)
    if (sanitized.matches("^[0-9-]+$") && sanitized.contains("-")) {
      throw new IllegalArgumentException("La identificación contiene caracteres no válidos");
    }

    return sanitized;
  }
}