package com.udea.GPX.util;

import org.springframework.stereotype.Component;
import java.util.regex.Pattern;

/**
 * Componente para sanitización de inputs básica sin dependencias externas
 * para prevenir ataques XSS, SQL injection y otros.
 */
@Component
public class InputSanitizer {

  // Patrones para detectar posibles ataques
  private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
      "(?i).*(union|select|insert|update|delete|drop|create|alter|exec|execute|script|javascript|vbscript|onload|onerror|alert|prompt|confirm|eval|expression|<script|</script).*");

  private static final Pattern XSS_PATTERN = Pattern.compile(
      "(?i).*(javascript:|vbscript:|data:|on\\w+\\s*=|<script|</script|<iframe|<object|<embed|<link|<meta).*");

  private static final Pattern PATH_TRAVERSAL_PATTERN = Pattern.compile(
      ".*(\\.\\./|\\.\\.\\\\|\\.\\./|%2e%2e%2f|%2e%2e\\\\).*");

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

    String sanitized = input.trim()
        .replaceAll("<script[^>]*>.*?</script>", "")
        .replaceAll("<.*?>", "")
        .replaceAll("javascript:", "")
        .replaceAll("vbscript:", "")
        .replaceAll("onload\\s*=", "")
        .replaceAll("onerror\\s*=", "");

    // Validaciones adicionales de seguridad
    if (SQL_INJECTION_PATTERN.matcher(sanitized).matches()) {
      throw new IllegalArgumentException("Input contiene patrones sospechosos de SQL injection");
    }

    if (XSS_PATTERN.matcher(sanitized).matches()) {
      throw new IllegalArgumentException("Input contiene patrones sospechosos de XSS");
    }

    if (PATH_TRAVERSAL_PATTERN.matcher(sanitized).matches()) {
      throw new IllegalArgumentException("Input contiene patrones de path traversal");
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

    // Validación básica de formato de email
    if (!sanitized.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")) {
      throw new IllegalArgumentException("Formato de email inválido");
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

    // Solo permitir letras, espacios, guiones y apostrofes
    if (!sanitized.matches("^[A-Za-záéíóúÁÉÍÓÚñÑ\\s'-]+$")) {
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

    // Solo permitir números y letras
    if (!sanitized.matches("^[A-Za-z0-9]+$")) {
      throw new IllegalArgumentException("La identificación contiene caracteres no válidos");
    }

    return sanitized;
  }
}