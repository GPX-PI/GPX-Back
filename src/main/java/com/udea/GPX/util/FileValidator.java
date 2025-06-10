package com.udea.GPX.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * Validador de archivos con verificación de contenido real y seguridad
 */
@Component
public class FileValidator {

  // Tamaños máximos por tipo de archivo (en bytes)
  private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024; // 10MB
  private static final long MAX_DOCUMENT_SIZE = 5 * 1024 * 1024; // 5MB

  // Magic numbers (signatures) para tipos de archivo comunes
  private static final Map<String, List<byte[]>> FILE_SIGNATURES = new HashMap<>();

  // Extensiones permitidas por categoría
  private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");
  private static final Set<String> ALLOWED_DOCUMENT_EXTENSIONS = Set.of("pdf", "doc", "docx", "txt");

  static {
    // JPEG
    FILE_SIGNATURES.put("jpeg", Arrays.asList(
        new byte[] { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0 },
        new byte[] { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE1 },
        new byte[] { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE8 }));

    // PNG
    FILE_SIGNATURES.put("png", List.of(
        new byte[] { (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A }));

    // GIF
    FILE_SIGNATURES.put("gif", Arrays.asList(
        new byte[] { 'G', 'I', 'F', '8', '7', 'a' },
        new byte[] { 'G', 'I', 'F', '8', '9', 'a' }));

    // WEBP
    FILE_SIGNATURES.put("webp", List.of(
        new byte[] { 'R', 'I', 'F', 'F' }));

    // PDF
    FILE_SIGNATURES.put("pdf", List.of(
        new byte[] { '%', 'P', 'D', 'F', '-' }));
  }

  /**
   * Valida archivo de imagen con verificación de contenido real
   */
  public ValidationResult validateImageFile(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      return ValidationResult.error("Archivo vacío o nulo");
    }

    // Validar tamaño
    if (file.getSize() > MAX_IMAGE_SIZE) {
      return ValidationResult.error("Archivo demasiado grande. Máximo permitido: " +
          (MAX_IMAGE_SIZE / 1024 / 1024) + "MB");
    }

    // Validar nombre de archivo
    String filename = file.getOriginalFilename();
    if (filename == null || filename.trim().isEmpty()) {
      return ValidationResult.error("Nombre de archivo inválido");
    }

    // Sanitizar nombre de archivo
    try {
      filename = InputSanitizer.sanitizeText(filename);
    } catch (IllegalArgumentException e) {
      return ValidationResult.error("Nombre de archivo contiene caracteres peligrosos: " + e.getMessage());
    }

    // Validar extensión
    String extension = getFileExtension(filename).toLowerCase();
    if (!ALLOWED_IMAGE_EXTENSIONS.contains(extension)) {
      return ValidationResult.error("Extensión de archivo no permitida. Permitidas: " +
          String.join(", ", ALLOWED_IMAGE_EXTENSIONS));
    }

    // Validar MIME type
    String mimeType = file.getContentType();
    if (mimeType == null || !mimeType.startsWith("image/")) {
      return ValidationResult.error("Tipo MIME inválido para imagen");
    }

    // Validar contenido real (magic numbers)
    try {
      if (!validateFileSignature(file, extension)) {
        return ValidationResult.error("El contenido del archivo no coincide con su extensión");
      }
    } catch (IOException e) {
      return ValidationResult.error("Error al leer el archivo: " + e.getMessage());
    }

    // Verificar contenido malicioso básico
    try {
      if (containsMaliciousContent(file)) {
        return ValidationResult.error("El archivo contiene contenido potencialmente malicioso");
      }
    } catch (IOException e) {
      return ValidationResult.error("Error al escanear el archivo: " + e.getMessage());
    }

    return ValidationResult.success();
  }

  /**
   * Valida archivo de documento
   */
  public ValidationResult validateDocumentFile(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      return ValidationResult.error("Archivo vacío o nulo");
    }

    // Validar tamaño
    if (file.getSize() > MAX_DOCUMENT_SIZE) {
      return ValidationResult.error("Archivo demasiado grande. Máximo permitido: " +
          (MAX_DOCUMENT_SIZE / 1024 / 1024) + "MB");
    }

    // Validar nombre de archivo
    String filename = file.getOriginalFilename();
    if (filename == null || filename.trim().isEmpty()) {
      return ValidationResult.error("Nombre de archivo inválido");
    }

    // Sanitizar nombre de archivo
    try {
      filename = InputSanitizer.sanitizeText(filename);
    } catch (IllegalArgumentException e) {
      return ValidationResult.error("Nombre de archivo contiene caracteres peligrosos: " + e.getMessage());
    }

    // Validar extensión
    String extension = getFileExtension(filename).toLowerCase();
    if (!ALLOWED_DOCUMENT_EXTENSIONS.contains(extension)) {
      return ValidationResult.error("Extensión de archivo no permitida. Permitidas: " +
          String.join(", ", ALLOWED_DOCUMENT_EXTENSIONS));
    }

    return ValidationResult.success();
  }

  /**
   * Valida magic numbers del archivo
   */
  private boolean validateFileSignature(MultipartFile file, String extension) throws IOException {
    if (!FILE_SIGNATURES.containsKey(extension)) {
      return true; // Si no tenemos signature definida, permitir
    }

    byte[] fileHeader = new byte[16]; // Leer los primeros 16 bytes
    int bytesRead = file.getInputStream().read(fileHeader);

    if (bytesRead < 4) {
      return false; // Archivo demasiado pequeño
    }

    List<byte[]> signatures = FILE_SIGNATURES.get(extension);
    for (byte[] signature : signatures) {
      if (matchesSignature(fileHeader, signature)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Verifica si el header coincide con una signature
   */
  private boolean matchesSignature(byte[] fileHeader, byte[] signature) {
    if (signature.length > fileHeader.length) {
      return false;
    }

    for (int i = 0; i < signature.length; i++) {
      if (fileHeader[i] != signature[i]) {
        return false;
      }
    }

    return true;
  }

  /**
   * Busca contenido potencialmente malicioso
   */
  private boolean containsMaliciousContent(MultipartFile file) throws IOException {
    byte[] content = file.getBytes();
    String contentString = new String(content, "UTF-8");

    // Buscar patrones maliciosos comunes
    String[] maliciousPatterns = {
        "<script", "javascript:", "eval(", "exec(", "cmd.exe",
        "powershell", "../../", "file://", "data:", "vbscript:"
    };

    String lowerContent = contentString.toLowerCase();
    for (String pattern : maliciousPatterns) {
      if (lowerContent.contains(pattern)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Extrae la extensión del nombre de archivo
   */
  private String getFileExtension(String filename) {
    if (filename == null || !filename.contains(".")) {
      return "";
    }
    return filename.substring(filename.lastIndexOf(".") + 1);
  }

  /**
   * Clase para resultado de validación
   */
  public static class ValidationResult {
    private final boolean valid;
    private final String errorMessage;

    private ValidationResult(boolean valid, String errorMessage) {
      this.valid = valid;
      this.errorMessage = errorMessage;
    }

    public static ValidationResult success() {
      return new ValidationResult(true, null);
    }

    public static ValidationResult error(String message) {
      return new ValidationResult(false, message);
    }

    public boolean isValid() {
      return valid;
    }

    public String getErrorMessage() {
      return errorMessage;
    }
  }
}