package com.udea.GPX.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("FileValidator Tests")
class FileValidatorTest {

  private FileValidator fileValidator;

  @BeforeEach
  void setUp() {
    fileValidator = new FileValidator();
  }

  // ========== VALIDATION RESULT TESTS ==========

  @Test
  @DisplayName("ValidationResult.success - Debe crear resultado exitoso")
  void validationResult_success_shouldCreateSuccessResult() {
    // When
    FileValidator.ValidationResult result = FileValidator.ValidationResult.success();

    // Then
    assertThat(result.isValid()).isTrue();
    assertThat(result.getErrorMessage()).isNull();
  }

  @Test
  @DisplayName("ValidationResult.error - Debe crear resultado con error")
  void validationResult_error_shouldCreateErrorResult() {
    // Given
    String errorMessage = "Test error message";

    // When
    FileValidator.ValidationResult result = FileValidator.ValidationResult.error(errorMessage);

    // Then
    assertThat(result.isValid()).isFalse();
    assertThat(result.getErrorMessage()).isEqualTo(errorMessage);
  }

  // ========== VALIDATE IMAGE FILE TESTS ==========

  @Test
  @DisplayName("validateImageFile - Debe fallar con archivo null")
  void validateImageFile_shouldFailWithNullFile() {
    // When
    FileValidator.ValidationResult result = fileValidator.validateImageFile(null);

    // Then
    assertThat(result.isValid()).isFalse();
    assertThat(result.getErrorMessage()).contains("Archivo vacío o nulo");
  }

  @Test
  @DisplayName("validateImageFile - Debe fallar con archivo vacío")
  void validateImageFile_shouldFailWithEmptyFile() {
    // Given
    MultipartFile file = mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(true);

    // When
    FileValidator.ValidationResult result = fileValidator.validateImageFile(file);

    // Then
    assertThat(result.isValid()).isFalse();
    assertThat(result.getErrorMessage()).contains("Archivo vacío o nulo");
  }

  @Test
  @DisplayName("validateImageFile - Debe fallar con archivo demasiado grande")
  void validateImageFile_shouldFailWithOversizedFile() {
    // Given
    MultipartFile file = mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getSize()).thenReturn(15L * 1024 * 1024); // 15MB

    // When
    FileValidator.ValidationResult result = fileValidator.validateImageFile(file);

    // Then
    assertThat(result.isValid()).isFalse();
    assertThat(result.getErrorMessage()).contains("Archivo demasiado grande");
    assertThat(result.getErrorMessage()).contains("10MB");
  }

  @Test
  @DisplayName("validateImageFile - Debe fallar con nombre de archivo null")
  void validateImageFile_shouldFailWithNullFilename() {
    // Given
    MultipartFile file = mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getSize()).thenReturn(1024L);
    when(file.getOriginalFilename()).thenReturn(null);

    // When
    FileValidator.ValidationResult result = fileValidator.validateImageFile(file);

    // Then
    assertThat(result.isValid()).isFalse();
    assertThat(result.getErrorMessage()).contains("Nombre de archivo inválido");
  }

  @Test
  @DisplayName("validateImageFile - Debe fallar con nombre de archivo vacío")
  void validateImageFile_shouldFailWithEmptyFilename() {
    // Given
    MultipartFile file = mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getSize()).thenReturn(1024L);
    when(file.getOriginalFilename()).thenReturn("   ");

    // When
    FileValidator.ValidationResult result = fileValidator.validateImageFile(file);

    // Then
    assertThat(result.isValid()).isFalse();
    assertThat(result.getErrorMessage()).contains("Nombre de archivo inválido");
  }

  @Test
  @DisplayName("validateImageFile - Debe fallar con extensión no permitida")
  void validateImageFile_shouldFailWithInvalidExtension() {
    // Given
    MultipartFile file = mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getSize()).thenReturn(1024L);
    when(file.getOriginalFilename()).thenReturn("test.exe");

    // When
    FileValidator.ValidationResult result = fileValidator.validateImageFile(file);

    // Then
    assertThat(result.isValid()).isFalse();
    assertThat(result.getErrorMessage()).contains("Extensión de archivo no permitida");
    // Verificar que contiene todas las extensiones esperadas sin importar el orden
    assertThat(result.getErrorMessage()).contains("jpeg");
    assertThat(result.getErrorMessage()).contains("jpg");
    assertThat(result.getErrorMessage()).contains("gif");
    assertThat(result.getErrorMessage()).contains("png");
    assertThat(result.getErrorMessage()).contains("webp");
  }

  @Test
  @DisplayName("validateImageFile - Debe fallar con MIME type inválido")
  void validateImageFile_shouldFailWithInvalidMimeType() {
    // Given
    MultipartFile file = mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getSize()).thenReturn(1024L);
    when(file.getOriginalFilename()).thenReturn("test.jpg");
    when(file.getContentType()).thenReturn("application/pdf");

    // When
    FileValidator.ValidationResult result = fileValidator.validateImageFile(file);

    // Then
    assertThat(result.isValid()).isFalse();
    assertThat(result.getErrorMessage()).contains("Tipo MIME inválido para imagen");
  }

  @Test
  @DisplayName("validateImageFile - Debe fallar con MIME type null")
  void validateImageFile_shouldFailWithNullMimeType() {
    // Given
    MultipartFile file = mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getSize()).thenReturn(1024L);
    when(file.getOriginalFilename()).thenReturn("test.jpg");
    when(file.getContentType()).thenReturn(null);

    // When
    FileValidator.ValidationResult result = fileValidator.validateImageFile(file);

    // Then
    assertThat(result.isValid()).isFalse();
    assertThat(result.getErrorMessage()).contains("Tipo MIME inválido para imagen");
  }

  @Test
  @DisplayName("validateImageFile - Debe fallar cuando signature no coincide")
  void validateImageFile_shouldFailWithInvalidFileSignature() throws IOException {
    // Given
    MultipartFile file = mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getSize()).thenReturn(1024L);
    when(file.getOriginalFilename()).thenReturn("test.jpeg");
    when(file.getContentType()).thenReturn("image/jpeg");

    // Archivo con contenido que no es JPEG
    byte[] fakeContent = "This is not a JPEG file".getBytes();
    when(file.getInputStream()).thenReturn(new ByteArrayInputStream(fakeContent));
    when(file.getBytes()).thenReturn(fakeContent);

    // When
    FileValidator.ValidationResult result = fileValidator.validateImageFile(file);

    // Then
    assertThat(result.isValid()).isFalse();
    assertThat(result.getErrorMessage()).contains("El contenido del archivo no coincide con su extensión");
  }

  @Test
  @DisplayName("validateImageFile - Debe fallar con contenido malicioso")
  void validateImageFile_shouldFailWithMaliciousContent() throws IOException {
    // Given
    MultipartFile file = mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getSize()).thenReturn(1024L);
    when(file.getOriginalFilename()).thenReturn("test.jpg");
    when(file.getContentType()).thenReturn("image/jpeg");

    // Header válido de JPEG pero con contenido malicioso
    byte[] jpegHeader = { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0 };
    byte[] maliciousContent = "<script>alert('malicious')</script>".getBytes();
    byte[] combinedContent = new byte[jpegHeader.length + maliciousContent.length];
    System.arraycopy(jpegHeader, 0, combinedContent, 0, jpegHeader.length);
    System.arraycopy(maliciousContent, 0, combinedContent, jpegHeader.length, maliciousContent.length);

    when(file.getInputStream()).thenReturn(new ByteArrayInputStream(combinedContent));
    when(file.getBytes()).thenReturn(combinedContent);

    // When
    FileValidator.ValidationResult result = fileValidator.validateImageFile(file);

    // Then
    assertThat(result.isValid()).isFalse();
    assertThat(result.getErrorMessage()).contains("contenido potencialmente malicioso");
  }

  @Test
  @DisplayName("validateImageFile - Debe manejar IOException al leer archivo")
  void validateImageFile_shouldHandleIOExceptionOnRead() throws IOException {
    // Given
    MultipartFile file = mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getSize()).thenReturn(1024L);
    when(file.getOriginalFilename()).thenReturn("test.jpeg");
    when(file.getContentType()).thenReturn("image/jpeg");
    when(file.getInputStream()).thenThrow(new IOException("Read error"));
    when(file.getBytes()).thenReturn("dummy content".getBytes());

    // When
    FileValidator.ValidationResult result = fileValidator.validateImageFile(file);

    // Then
    assertThat(result.isValid()).isFalse();
    assertThat(result.getErrorMessage()).contains("Error al leer el archivo");
  }

  @Test
  @DisplayName("validateImageFile - Debe manejar IOException al escanear contenido")
  void validateImageFile_shouldHandleIOExceptionOnScan() throws IOException {
    // Given
    MultipartFile file = mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getSize()).thenReturn(1024L);
    when(file.getOriginalFilename()).thenReturn("test.jpg");
    when(file.getContentType()).thenReturn("image/jpeg");

    // Header válido de JPEG
    byte[] jpegHeader = { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0 };
    when(file.getInputStream()).thenReturn(new ByteArrayInputStream(jpegHeader));
    when(file.getBytes()).thenThrow(new IOException("Scan error"));

    // When
    FileValidator.ValidationResult result = fileValidator.validateImageFile(file);

    // Then
    assertThat(result.isValid()).isFalse();
    assertThat(result.getErrorMessage()).contains("Error al escanear el archivo");
  }

  @Test
  @DisplayName("validateImageFile - Debe fallar con archivo demasiado pequeño")
  void validateImageFile_shouldFailWithTooSmallFile() throws IOException {
    // Given
    MultipartFile file = mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getSize()).thenReturn(1024L);
    when(file.getOriginalFilename()).thenReturn("test.jpeg");
    when(file.getContentType()).thenReturn("image/jpeg");

    // Archivo muy pequeño (menos de 4 bytes)
    byte[] tinyContent = { (byte) 0xFF, (byte) 0xD8 };
    when(file.getInputStream()).thenReturn(new ByteArrayInputStream(tinyContent));
    when(file.getBytes()).thenReturn(tinyContent);

    // When
    FileValidator.ValidationResult result = fileValidator.validateImageFile(file);

    // Then
    assertThat(result.isValid()).isFalse();
    assertThat(result.getErrorMessage()).contains("El contenido del archivo no coincide con su extensión");
  }

  @Test
  @DisplayName("validateImageFile - Debe validar PNG correctamente")
  void validateImageFile_shouldValidatePngCorrectly() throws IOException {
    // Given
    MultipartFile file = mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getSize()).thenReturn(1024L);
    when(file.getOriginalFilename()).thenReturn("test.png");
    when(file.getContentType()).thenReturn("image/png");

    // Header válido de PNG
    byte[] pngHeader = { (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A };
    byte[] validContent = "valid png content".getBytes();
    byte[] combinedContent = new byte[pngHeader.length + validContent.length];
    System.arraycopy(pngHeader, 0, combinedContent, 0, pngHeader.length);
    System.arraycopy(validContent, 0, combinedContent, pngHeader.length, validContent.length);

    when(file.getInputStream()).thenReturn(new ByteArrayInputStream(combinedContent));
    when(file.getBytes()).thenReturn(combinedContent);

    // When
    FileValidator.ValidationResult result = fileValidator.validateImageFile(file);

    // Then
    assertThat(result.isValid()).isTrue();
    assertThat(result.getErrorMessage()).isNull();
  }

  // ========== VALIDATE DOCUMENT FILE TESTS ==========

  @Test
  @DisplayName("validateDocumentFile - Debe fallar con archivo null")
  void validateDocumentFile_shouldFailWithNullFile() {
    // When
    FileValidator.ValidationResult result = fileValidator.validateDocumentFile(null);

    // Then
    assertThat(result.isValid()).isFalse();
    assertThat(result.getErrorMessage()).contains("Archivo vacío o nulo");
  }

  @Test
  @DisplayName("validateDocumentFile - Debe fallar con archivo vacío")
  void validateDocumentFile_shouldFailWithEmptyFile() {
    // Given
    MultipartFile file = mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(true);

    // When
    FileValidator.ValidationResult result = fileValidator.validateDocumentFile(file);

    // Then
    assertThat(result.isValid()).isFalse();
    assertThat(result.getErrorMessage()).contains("Archivo vacío o nulo");
  }

  @Test
  @DisplayName("validateDocumentFile - Debe fallar con archivo demasiado grande")
  void validateDocumentFile_shouldFailWithOversizedFile() {
    // Given
    MultipartFile file = mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getSize()).thenReturn(10L * 1024 * 1024); // 10MB

    // When
    FileValidator.ValidationResult result = fileValidator.validateDocumentFile(file);

    // Then
    assertThat(result.isValid()).isFalse();
    assertThat(result.getErrorMessage()).contains("Archivo demasiado grande");
    assertThat(result.getErrorMessage()).contains("5MB");
  }

  @Test
  @DisplayName("validateDocumentFile - Debe fallar con nombre de archivo null")
  void validateDocumentFile_shouldFailWithNullFilename() {
    // Given
    MultipartFile file = mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getSize()).thenReturn(1024L);
    when(file.getOriginalFilename()).thenReturn(null);

    // When
    FileValidator.ValidationResult result = fileValidator.validateDocumentFile(file);

    // Then
    assertThat(result.isValid()).isFalse();
    assertThat(result.getErrorMessage()).contains("Nombre de archivo inválido");
  }

  @Test
  @DisplayName("validateDocumentFile - Debe fallar con extensión no permitida")
  void validateDocumentFile_shouldFailWithInvalidExtension() {
    // Given
    MultipartFile file = mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getSize()).thenReturn(1024L);
    when(file.getOriginalFilename()).thenReturn("test.exe");

    // When
    FileValidator.ValidationResult result = fileValidator.validateDocumentFile(file);

    // Then
    assertThat(result.isValid()).isFalse();
    assertThat(result.getErrorMessage()).contains("Extensión de archivo no permitida");
    // Verificar que contiene todas las extensiones esperadas sin importar el orden
    assertThat(result.getErrorMessage()).contains("docx");
    assertThat(result.getErrorMessage()).contains("doc");
    assertThat(result.getErrorMessage()).contains("txt");
    assertThat(result.getErrorMessage()).contains("pdf");
  }

  @ParameterizedTest
  @ValueSource(strings = { "document.pdf", "document.txt", "document.doc", "document.docx" })
  @DisplayName("validateDocumentFile - Debe validar documentos válidos correctamente")
  void validateDocumentFile_shouldValidateValidDocuments(String filename) {
    // Given
    MultipartFile file = mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getSize()).thenReturn(1024L);
    when(file.getOriginalFilename()).thenReturn(filename);

    // When
    FileValidator.ValidationResult result = fileValidator.validateDocumentFile(file);

    // Then
    assertThat(result.isValid()).isTrue();
    assertThat(result.getErrorMessage()).isNull();
  }

  // ========== EDGE CASES ==========

  @Test
  @DisplayName("validateImageFile - Debe manejar nombres con múltiples puntos")
  void validateImageFile_shouldHandleFilenameWithMultipleDots() {
    // Given
    MultipartFile file = mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getSize()).thenReturn(1024L);
    when(file.getOriginalFilename()).thenReturn("my.image.test.jpeg");
    when(file.getContentType()).thenReturn("image/jpeg");

    // Agregar mocks faltantes con contenido que falle signature
    byte[] dummyContent = "dummy content".getBytes();
    try {
      when(file.getInputStream()).thenReturn(new ByteArrayInputStream(dummyContent));
      when(file.getBytes()).thenReturn(dummyContent);
    } catch (IOException e) {
      // This shouldn't happen in a test
    }

    // When
    FileValidator.ValidationResult result = fileValidator.validateImageFile(file);

    // Then - Debe usar la última extensión (.jpeg) pero fallar por signature
    assertThat(result.getErrorMessage()).doesNotContain("Extensión de archivo no permitida");
    assertThat(result.isValid()).isFalse(); // Fallará por signature invalid
  }

  @Test
  @DisplayName("validateImageFile - Debe manejar extensiones en mayúsculas")
  void validateImageFile_shouldHandleUpperCaseExtensions() {
    // Given
    MultipartFile file = mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getSize()).thenReturn(1024L);
    when(file.getOriginalFilename()).thenReturn("test.JPEG");
    when(file.getContentType()).thenReturn("image/jpeg");

    // Agregar mocks faltantes con contenido que falle signature
    byte[] dummyContent = "dummy content".getBytes();
    try {
      when(file.getInputStream()).thenReturn(new ByteArrayInputStream(dummyContent));
      when(file.getBytes()).thenReturn(dummyContent);
    } catch (IOException e) {
      // This shouldn't happen in a test
    }

    // When
    FileValidator.ValidationResult result = fileValidator.validateImageFile(file);

    // Then - Debe convertir a minúsculas y validar pero fallar por signature
    assertThat(result.getErrorMessage()).doesNotContain("Extensión de archivo no permitida");
    assertThat(result.isValid()).isFalse(); // Fallará por signature invalid
  }

  @Test
  @DisplayName("validateImageFile - Debe manejar archivo sin extensión")
  void validateImageFile_shouldHandleFilenameWithoutExtension() {
    // Given
    MultipartFile file = mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getSize()).thenReturn(1024L);
    when(file.getOriginalFilename()).thenReturn("testfile");

    // When
    FileValidator.ValidationResult result = fileValidator.validateImageFile(file);

    // Then
    assertThat(result.isValid()).isFalse();
    assertThat(result.getErrorMessage()).contains("Extensión de archivo no permitida");
  }

  // ========== ALTA PRIORIDAD - TESTS CRÍTICOS FALTANTES ==========

  // Tests del InputSanitizer Integration (ALTA PRIORIDAD) @Test
  @DisplayName("validateImageFile - Debe manejar excepción de InputSanitizer con caracteres peligrosos")
  void validateImageFile_shouldHandleInputSanitizerExceptionWithDangerousChars() throws IOException {
    // Given
    MultipartFile file = mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getSize()).thenReturn(1024L);
    // Filename con caracteres que InputSanitizer detectará como SQL injection
    when(file.getOriginalFilename()).thenReturn("test'; DROP TABLE users; --.jpg");
    when(file.getContentType()).thenReturn("image/jpeg");

    // Agregar contenido válido para evitar NPE
    byte[] validContent = "dummy content".getBytes();
    when(file.getInputStream()).thenReturn(new ByteArrayInputStream(validContent));
    when(file.getBytes()).thenReturn(validContent);

    // When
    FileValidator.ValidationResult result = fileValidator.validateImageFile(file);

    // Then
    assertThat(result.isValid()).isFalse();
    assertThat(result.getErrorMessage()).contains("caracteres peligrosos");
    assertThat(result.getErrorMessage()).contains("SQL injection");
  }

  @Test
  @DisplayName("validateImageFile - Debe manejar excepción de InputSanitizer con XSS")
  void validateImageFile_shouldHandleInputSanitizerExceptionWithXSS() throws IOException {
    // Given
    MultipartFile file = mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getSize()).thenReturn(1024L);
    // Filename con javascript: que InputSanitizer detectará como SQL injection
    // (porque incluye 'javascript' y 'alert')
    when(file.getOriginalFilename()).thenReturn("javascript:alert('xss').jpg");
    when(file.getContentType()).thenReturn("image/jpeg");

    // Agregar contenido válido para evitar NPE
    byte[] validContent = "dummy content".getBytes();
    when(file.getInputStream()).thenReturn(new ByteArrayInputStream(validContent));
    when(file.getBytes()).thenReturn(validContent); // When
    FileValidator.ValidationResult result = fileValidator.validateImageFile(file);

    // Then
    assertThat(result.isValid()).isFalse();
    assertThat(result.getErrorMessage()).contains("caracteres peligrosos");
    assertThat(result.getErrorMessage()).contains("XSS");
  }

  @Test
  @DisplayName("validateImageFile - Debe manejar excepción de InputSanitizer con XSS puro")
  void validateImageFile_shouldHandleInputSanitizerExceptionWithPureXSS() throws IOException {
    // Given
    MultipartFile file = mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getSize()).thenReturn(1024L);
    // Filename con data: que InputSanitizer detectará específicamente como XSS
    when(file.getOriginalFilename()).thenReturn("data:text/html,<h1>test</h1>.jpg");
    when(file.getContentType()).thenReturn("image/jpeg");

    // Agregar contenido válido para evitar NPE
    byte[] validContent = "dummy content".getBytes();
    when(file.getInputStream()).thenReturn(new ByteArrayInputStream(validContent));
    when(file.getBytes()).thenReturn(validContent);

    // When
    FileValidator.ValidationResult result = fileValidator.validateImageFile(file);

    // Then
    assertThat(result.isValid()).isFalse();
    assertThat(result.getErrorMessage()).contains("caracteres peligrosos");
    assertThat(result.getErrorMessage()).contains("XSS");
  }

  @Test
  @DisplayName("validateDocumentFile - Debe manejar excepción de InputSanitizer")
  void validateDocumentFile_shouldHandleInputSanitizerException() {
    // Given
    MultipartFile file = mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getSize()).thenReturn(1024L);
    // Filename con path traversal que InputSanitizer detectará
    when(file.getOriginalFilename()).thenReturn("../../../etc/passwd.pdf");

    // When
    FileValidator.ValidationResult result = fileValidator.validateDocumentFile(file);

    // Then
    assertThat(result.isValid()).isFalse();
    assertThat(result.getErrorMessage()).contains("caracteres peligrosos");
    assertThat(result.getErrorMessage()).contains("path traversal");
  }

  // Tests de GIF y WEBP Validation (ALTA PRIORIDAD)
  @Test
  @DisplayName("validateImageFile - Debe validar GIF87a correctamente")
  void validateImageFile_shouldValidateGif87aCorrectly() throws IOException {
    // Given
    MultipartFile file = mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getSize()).thenReturn(1024L);
    when(file.getOriginalFilename()).thenReturn("test.gif");
    when(file.getContentType()).thenReturn("image/gif");

    // Header válido de GIF87a
    byte[] gif87aHeader = { 'G', 'I', 'F', '8', '7', 'a' };
    byte[] validContent = "valid gif content".getBytes();
    byte[] combinedContent = new byte[gif87aHeader.length + validContent.length];
    System.arraycopy(gif87aHeader, 0, combinedContent, 0, gif87aHeader.length);
    System.arraycopy(validContent, 0, combinedContent, gif87aHeader.length, validContent.length);

    when(file.getInputStream()).thenReturn(new ByteArrayInputStream(combinedContent));
    when(file.getBytes()).thenReturn(combinedContent);

    // When
    FileValidator.ValidationResult result = fileValidator.validateImageFile(file);

    // Then
    assertThat(result.isValid()).isTrue();
    assertThat(result.getErrorMessage()).isNull();
  }

  @Test
  @DisplayName("validateImageFile - Debe validar GIF89a correctamente")
  void validateImageFile_shouldValidateGif89aCorrectly() throws IOException {
    // Given
    MultipartFile file = mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getSize()).thenReturn(1024L);
    when(file.getOriginalFilename()).thenReturn("test.gif");
    when(file.getContentType()).thenReturn("image/gif");

    // Header válido de GIF89a
    byte[] gif89aHeader = { 'G', 'I', 'F', '8', '9', 'a' };
    byte[] validContent = "valid gif content".getBytes();
    byte[] combinedContent = new byte[gif89aHeader.length + validContent.length];
    System.arraycopy(gif89aHeader, 0, combinedContent, 0, gif89aHeader.length);
    System.arraycopy(validContent, 0, combinedContent, gif89aHeader.length, validContent.length);

    when(file.getInputStream()).thenReturn(new ByteArrayInputStream(combinedContent));
    when(file.getBytes()).thenReturn(combinedContent);

    // When
    FileValidator.ValidationResult result = fileValidator.validateImageFile(file);

    // Then
    assertThat(result.isValid()).isTrue();
    assertThat(result.getErrorMessage()).isNull();
  }

  @Test
  @DisplayName("validateImageFile - Debe validar WEBP correctamente")
  void validateImageFile_shouldValidateWebpCorrectly() throws IOException {
    // Given
    MultipartFile file = mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getSize()).thenReturn(1024L);
    when(file.getOriginalFilename()).thenReturn("test.webp");
    when(file.getContentType()).thenReturn("image/webp");

    // Header válido de WEBP (RIFF)
    byte[] webpHeader = { 'R', 'I', 'F', 'F' };
    byte[] validContent = "WEBP content".getBytes();
    byte[] combinedContent = new byte[webpHeader.length + validContent.length];
    System.arraycopy(webpHeader, 0, combinedContent, 0, webpHeader.length);
    System.arraycopy(validContent, 0, combinedContent, webpHeader.length, validContent.length);

    when(file.getInputStream()).thenReturn(new ByteArrayInputStream(combinedContent));
    when(file.getBytes()).thenReturn(combinedContent);

    // When
    FileValidator.ValidationResult result = fileValidator.validateImageFile(file);

    // Then
    assertThat(result.isValid()).isTrue();
    assertThat(result.getErrorMessage()).isNull();
  }

  // Tests de Múltiples Signatures JPEG (ALTA PRIORIDAD)
  @Test
  @DisplayName("validateImageFile - Debe validar JPEG con signature E0")
  void validateImageFile_shouldValidateJpegWithE0Signature() throws IOException {
    // Given
    MultipartFile file = mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getSize()).thenReturn(1024L);
    when(file.getOriginalFilename()).thenReturn("test.jpeg");
    when(file.getContentType()).thenReturn("image/jpeg");

    // Header válido de JPEG con signature E0
    byte[] jpegE0Header = { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0 };
    byte[] validContent = "valid jpeg content".getBytes();
    byte[] combinedContent = new byte[jpegE0Header.length + validContent.length];
    System.arraycopy(jpegE0Header, 0, combinedContent, 0, jpegE0Header.length);
    System.arraycopy(validContent, 0, combinedContent, jpegE0Header.length, validContent.length);

    when(file.getInputStream()).thenReturn(new ByteArrayInputStream(combinedContent));
    when(file.getBytes()).thenReturn(combinedContent);

    // When
    FileValidator.ValidationResult result = fileValidator.validateImageFile(file);

    // Then
    assertThat(result.isValid()).isTrue();
    assertThat(result.getErrorMessage()).isNull();
  }

  @Test
  @DisplayName("validateImageFile - Debe validar JPEG con signature E1")
  void validateImageFile_shouldValidateJpegWithE1Signature() throws IOException {
    // Given
    MultipartFile file = mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getSize()).thenReturn(1024L);
    when(file.getOriginalFilename()).thenReturn("test.jpeg");
    when(file.getContentType()).thenReturn("image/jpeg");

    // Header válido de JPEG con signature E1
    byte[] jpegE1Header = { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE1 };
    byte[] validContent = "valid jpeg content".getBytes();
    byte[] combinedContent = new byte[jpegE1Header.length + validContent.length];
    System.arraycopy(jpegE1Header, 0, combinedContent, 0, jpegE1Header.length);
    System.arraycopy(validContent, 0, combinedContent, jpegE1Header.length, validContent.length);

    when(file.getInputStream()).thenReturn(new ByteArrayInputStream(combinedContent));
    when(file.getBytes()).thenReturn(combinedContent);

    // When
    FileValidator.ValidationResult result = fileValidator.validateImageFile(file);

    // Then
    assertThat(result.isValid()).isTrue();
    assertThat(result.getErrorMessage()).isNull();
  }

  @Test
  @DisplayName("validateImageFile - Debe validar JPEG con signature E8")
  void validateImageFile_shouldValidateJpegWithE8Signature() throws IOException {
    // Given
    MultipartFile file = mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getSize()).thenReturn(1024L);
    when(file.getOriginalFilename()).thenReturn("test.jpeg");
    when(file.getContentType()).thenReturn("image/jpeg");

    // Header válido de JPEG con signature E8
    byte[] jpegE8Header = { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE8 };
    byte[] validContent = "valid jpeg content".getBytes();
    byte[] combinedContent = new byte[jpegE8Header.length + validContent.length];
    System.arraycopy(jpegE8Header, 0, combinedContent, 0, jpegE8Header.length);
    System.arraycopy(validContent, 0, combinedContent, jpegE8Header.length, validContent.length);

    when(file.getInputStream()).thenReturn(new ByteArrayInputStream(combinedContent));
    when(file.getBytes()).thenReturn(combinedContent);

    // When
    FileValidator.ValidationResult result = fileValidator.validateImageFile(file);

    // Then
    assertThat(result.isValid()).isTrue();
    assertThat(result.getErrorMessage()).isNull();
  }

  // Tests de validateFileSignature Edge Cases (ALTA PRIORIDAD)
  @Test
  @DisplayName("validateImageFile - Debe permitir extensión sin signature definida")
  void validateImageFile_shouldAllowExtensionWithoutDefinedSignature() throws IOException {
    // Given
    MultipartFile file = mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getSize()).thenReturn(1024L);
    when(file.getOriginalFilename()).thenReturn("test.bmp"); // BMP no está en FILE_SIGNATURES
    when(file.getContentType()).thenReturn("image/bmp");

    byte[] bmpContent = "BMP content".getBytes();
    when(file.getInputStream()).thenReturn(new ByteArrayInputStream(bmpContent));
    when(file.getBytes()).thenReturn(bmpContent);

    // When
    FileValidator.ValidationResult result = fileValidator.validateImageFile(file);

    // Then
    // Debería fallar por extensión no permitida, no por signature
    assertThat(result.isValid()).isFalse();
    assertThat(result.getErrorMessage()).contains("Extensión de archivo no permitida");
  }

  @Test
  @DisplayName("validateImageFile - Debe fallar cuando signature es más larga que file header")
  void validateImageFile_shouldFailWhenSignatureIsLongerThanFileHeader() throws IOException {
    // Given
    MultipartFile file = mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getSize()).thenReturn(1024L);
    when(file.getOriginalFilename()).thenReturn("test.png");
    when(file.getContentType()).thenReturn("image/png");

    // Archivo muy pequeño (PNG signature requiere 8 bytes)
    byte[] shortContent = { (byte) 0x89, 0x50 }; // Solo 2 bytes
    when(file.getInputStream()).thenReturn(new ByteArrayInputStream(shortContent));
    when(file.getBytes()).thenReturn(shortContent);

    // When
    FileValidator.ValidationResult result = fileValidator.validateImageFile(file);

    // Then
    assertThat(result.isValid()).isFalse();
    assertThat(result.getErrorMessage()).contains("El contenido del archivo no coincide con su extensión");
  }

  // ========== MEDIA PRIORIDAD - TESTS IMPORTANTES ==========

  // Tests de containsMaliciousContent con diferentes patrones (MEDIA PRIORIDAD)
  @Test
  @DisplayName("validateImageFile - Debe detectar diferentes patrones maliciosos")
  void validateImageFile_shouldDetectVariousMaliciousPatterns() throws IOException {
    String[] maliciousPatterns = {
        "javascript:alert('xss')",
        "eval(malicious_code)",
        "exec(rm -rf /)",
        "cmd.exe /c dir",
        "powershell -command",
        "../../etc/passwd",
        "file://system/file",
        "data:text/html,<script>",
        "vbscript:msgbox('evil')"
    };

    for (String pattern : maliciousPatterns) {
      // Given
      MultipartFile file = mock(MultipartFile.class);
      when(file.isEmpty()).thenReturn(false);
      when(file.getSize()).thenReturn(1024L);
      when(file.getOriginalFilename()).thenReturn("test.jpg");
      when(file.getContentType()).thenReturn("image/jpeg");

      // Header válido de JPEG con contenido malicioso
      byte[] jpegHeader = { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0 };
      byte[] maliciousContent = pattern.getBytes();
      byte[] combinedContent = new byte[jpegHeader.length + maliciousContent.length];
      System.arraycopy(jpegHeader, 0, combinedContent, 0, jpegHeader.length);
      System.arraycopy(maliciousContent, 0, combinedContent, jpegHeader.length, maliciousContent.length);

      when(file.getInputStream()).thenReturn(new ByteArrayInputStream(combinedContent));
      when(file.getBytes()).thenReturn(combinedContent);

      // When
      FileValidator.ValidationResult result = fileValidator.validateImageFile(file);

      // Then
      assertThat(result.isValid()).isFalse();
      assertThat(result.getErrorMessage()).contains("contenido potencialmente malicioso");
    }
  }

  // Tests de getFileExtension Edge Cases (MEDIA PRIORIDAD)
  @Test
  @DisplayName("getFileExtension - Debe manejar filename sin punto")
  void getFileExtension_shouldHandleFilenameWithoutDot() {
    // When & Then
    MultipartFile file = mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getSize()).thenReturn(1024L);
    when(file.getOriginalFilename()).thenReturn("filename_without_extension");

    FileValidator.ValidationResult result = fileValidator.validateImageFile(file);

    assertThat(result.isValid()).isFalse();
    assertThat(result.getErrorMessage()).contains("Extensión de archivo no permitida");
  }

  @Test
  @DisplayName("getFileExtension - Debe manejar multiple dots en filename")
  void getFileExtension_shouldHandleMultipleDotsInFilename() throws IOException {
    // Given
    MultipartFile file = mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getSize()).thenReturn(1024L);
    when(file.getOriginalFilename()).thenReturn("file.name.with.multiple.dots.jpg");
    when(file.getContentType()).thenReturn("image/jpeg");

    // Header válido de JPEG
    byte[] jpegHeader = { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0 };
    byte[] validContent = "valid content".getBytes();
    byte[] combinedContent = new byte[jpegHeader.length + validContent.length];
    System.arraycopy(jpegHeader, 0, combinedContent, 0, jpegHeader.length);
    System.arraycopy(validContent, 0, combinedContent, jpegHeader.length, validContent.length);

    when(file.getInputStream()).thenReturn(new ByteArrayInputStream(combinedContent));
    when(file.getBytes()).thenReturn(combinedContent);

    // When
    FileValidator.ValidationResult result = fileValidator.validateImageFile(file);

    // Then
    // Debería usar la última extensión (.jpg)
    assertThat(result.isValid()).isTrue();
  }

}