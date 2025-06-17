package com.udea.gpx.util;

import java.util.regex.Pattern;

/**
 * Clase utilitaria para sanitización de inputs básica sin dependencias externas
 * para prevenir ataques XSS, SQL injection y otros.
 * Optimizada para prevenir vulnerabilidades ReDoS.
 */
public final class InputSanitizer {

  // Constructor privado para evitar instanciación
  private InputSanitizer() {
    throw new IllegalStateException("Utility class");
  }

  // Patrones optimizados para prevenir ReDoS - sin backtracking exponencial
  private static final Pattern SEVERE_SQL_INJECTION_PATTERN = Pattern.compile(
      "(?i)\\b(drop\\s+table|truncate|delete\\s+from)\\b");

  private static final Pattern SEVERE_XSS_ATTACK_PATTERN = Pattern.compile(
      "(?i)(data:text/html|data:[^,]*script)");

  private static final Pattern PATH_TRAVERSAL_PATTERN = Pattern.compile(
      "(\\.\\./|\\.\\.\\\\/|%2e%2e%2f|%2e%2e%5c)");

  // Patrones adicionales para validaciones específicas
  private static final Pattern EMAIL_PATTERN = Pattern.compile(
      "^[A-Za-z0-9+_.\\-]+@[A-Za-z0-9\\-]+\\.[A-Za-z0-9\\-.]+$");

  private static final Pattern NAME_PATTERN = Pattern.compile(
      "^[\\p{L}\\s'\\-]{1,100}$");

  private static final Pattern URL_PATTERN = Pattern.compile(
      "^https?://[A-Za-z0-9.\\-]+(?:/[A-Za-z0-9._~:/?#\\[\\]@!$&'()*+,;=%\\-]*)?$");

  private static final Pattern PHONE_PATTERN = Pattern.compile(
      "^[0-9\\s()\\+\\-]{1,20}$");

  private static final Pattern IDENTIFICATION_PATTERN = Pattern.compile(
      "^[A-Za-z0-9_\\-]{1,50}$");

  private static final Pattern NUMERIC_WITH_HYPHENS = Pattern.compile(
      "^[0-9\\-]+$");

  // Patrón para campos sociales: permite URLs completas o usernames/texto plano
  private static final Pattern SOCIAL_FIELD_PATTERN = Pattern.compile(
      "^(?:https?://[A-Za-z0-9.\\-]+(?:/[A-Za-z0-9._~:/?#\\[\\]@!$&'()*+,;=%\\-]*)?|[A-Za-z0-9._@\\-/]{1,200})$");

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

    // Límite de longitud para prevenir ataques
    if (trimmed.length() > 10000) {
      throw new IllegalArgumentException("Input demasiado largo");
    }

    // Check for serious SQL injection patterns that should be rejected
    if (SEVERE_SQL_INJECTION_PATTERN.matcher(trimmed).find()) {
      throw new IllegalArgumentException("Input contiene patrones sospechosos de SQL injection");
    }

    // Check for serious XSS attacks that should be rejected
    if (SEVERE_XSS_ATTACK_PATTERN.matcher(trimmed).find()) {
      throw new IllegalArgumentException("Input contiene patrones sospechosos de XSS");
    }

    // Check for javascript: and vbscript: schemes
    if (trimmed.toLowerCase().contains("javascript:") || trimmed.toLowerCase().contains("vbscript:")) {
      throw new IllegalArgumentException("Input contiene patrones sospechosos de XSS");
    }

    if (PATH_TRAVERSAL_PATTERN.matcher(trimmed).find()) {
      throw new IllegalArgumentException("Input contiene patrones de path traversal");
    }

    // Clean/sanitize the content (remove tags, scripts, etc.)
    return trimmed
        .replaceAll("(?i)<script[^>]*+>.*?</script>", "")
        .replaceAll("<[^>]*+>", "")
        .replace("javascript:", "")
        .replace("vbscript:", "")
        .replaceAll("(?i)onload\\s*=", "")
        .replaceAll("(?i)onerror\\s*=", "");
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

    if (email.length() > 320) { // RFC 5321 limit
      throw new IllegalArgumentException("Email demasiado largo");
    }

    String sanitized = sanitizeText(email.toLowerCase().trim());

    // Verificar que sanitized no sea null
    if (sanitized == null) {
      throw new IllegalArgumentException("El email no puede ser procesado");
    }

    // Validación básica de formato de email
    if (!EMAIL_PATTERN.matcher(sanitized).matches()) {
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
    if (!NAME_PATTERN.matcher(sanitized).matches()) {
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

    if (url.length() > 2048) { // URL length limit
      throw new IllegalArgumentException("URL demasiado larga");
    }

    String sanitized = sanitizeText(url);

    // Verificar que sanitized no sea null
    if (sanitized == null) {
      throw new IllegalArgumentException("La URL no puede ser procesada");
    }

    // Solo permitir HTTP y HTTPS
    if (!URL_PATTERN.matcher(sanitized).matches()) {
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
    if (!PHONE_PATTERN.matcher(sanitized).matches()) {
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
    if (!IDENTIFICATION_PATTERN.matcher(sanitized).matches()) {
      throw new IllegalArgumentException("La identificación contiene caracteres no válidos");
    }

    // Reject purely numeric strings with hyphens (like SSN format)
    if (NUMERIC_WITH_HYPHENS.matcher(sanitized).matches() && sanitized.contains("-")) {
      throw new IllegalArgumentException("La identificación contiene caracteres no válidos");
    }

    return sanitized;
  }

  /**
   * Sanitiza campos de redes sociales
   * Permite tanto URLs completas como usernames/texto plano
   * 
   * @param socialField campo social de entrada (URL o username)
   * @return campo social sanitizado
   */
  public static String sanitizeSocialField(String socialField) {
    if (socialField == null) {
      return null;
    }

    if (socialField.length() > 200) { // Límite razonable para URLs y usernames
      throw new IllegalArgumentException("Campo social demasiado largo");
    }

    String sanitized = sanitizeText(socialField);

    // Verificar que sanitized no sea null
    if (sanitized == null) {
      throw new IllegalArgumentException("El campo social no puede ser procesado");
    }

    // Para campos sociales, hacer validaciones flexibles
    // Permitir tanto URLs como usernames simples
    if (!SOCIAL_FIELD_PATTERN.matcher(sanitized).matches()) {
      throw new IllegalArgumentException("Campo social contiene caracteres no válidos");
    }

    return sanitized;
  }
}