package com.udea.GPX.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import static org.assertj.core.api.Assertions.*;

@DisplayName("InputSanitizer Tests")
class InputSanitizerTest {

  @Test
  @DisplayName("sanitizeText - Debe remover scripts maliciosos")
  void sanitizeText_shouldRemoveMaliciousScripts() {
    // Given
    String maliciousInput = "<script>alert('XSS')</script>Hello World";

    // When
    String result = InputSanitizer.sanitizeText(maliciousInput);

    // Then
    assertThat(result).isEqualTo("Hello World");
    assertThat(result).doesNotContain("<script>");
    assertThat(result).doesNotContain("alert");
  }

  @Test
  @DisplayName("sanitizeText - Debe remover tags HTML")
  void sanitizeText_shouldRemoveHtmlTags() {
    // Given
    String htmlInput = "<div>Safe</div><iframe>evil</iframe><p>Also safe</p>";

    // When
    String result = InputSanitizer.sanitizeText(htmlInput);

    // Then
    assertThat(result).isEqualTo("SafeevilAlso safe");
    assertThat(result).doesNotContain("<div>");
    assertThat(result).doesNotContain("<iframe>");
  }

  @Test
  @DisplayName("sanitizeText - Debe detectar javascript con alert como SQL injection")
  void sanitizeText_shouldDetectJavaScriptWithAlertAsSqlInjection() {
    // Given
    String jsInput = "javascript:alert('XSS')"; // When & Then
    assertThatThrownBy(() -> InputSanitizer.sanitizeText(jsInput))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("XSS");
  }

  @Test
  @DisplayName("sanitizeText - Debe manejar entrada nula")
  void sanitizeText_shouldHandleNullInput() {
    // When & Then
    assertThat(InputSanitizer.sanitizeText(null)).isNull();
  }

  @Test
  @DisplayName("sanitizeText - Debe limpiar HTML básico sin palabras peligrosas")
  void sanitizeText_shouldCleanBasicHtmlWithoutDangerousWords() {
    // Given
    String htmlInput = "<div>Hello <b>World</b></div>";

    // When
    String result = InputSanitizer.sanitizeText(htmlInput);

    // Then
    assertThat(result).isEqualTo("Hello World");
    assertThat(result).doesNotContain("<div>");
    assertThat(result).doesNotContain("<b>");
  }

  @Test
  @DisplayName("sanitizeText - Debe preservar texto limpio")
  void sanitizeText_shouldPreserveCleanText() {
    // Given
    String cleanInput = "Hello World 123";

    // When
    String result = InputSanitizer.sanitizeText(cleanInput);

    // Then
    assertThat(result).isEqualTo("Hello World 123");
  }

  @Test
  @DisplayName("sanitizeText - Debe lanzar excepción para SQL injection")
  void sanitizeText_shouldThrowExceptionForSqlInjection() {
    // Given
    String sqlInput = "'; DROP TABLE users; --";

    // When & Then
    assertThatThrownBy(() -> InputSanitizer.sanitizeText(sqlInput))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("SQL injection");
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "SELECT * FROM users",
      "union select password",
      "INSERT INTO table",
      "DELETE FROM events"
  })
  @DisplayName("sanitizeText - Debe detectar patrones SQL injection")
  void sanitizeText_shouldDetectSqlInjectionPatterns(String sqlInput) {
    // When & Then
    assertThatThrownBy(() -> InputSanitizer.sanitizeText(sqlInput))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("SQL injection");
  }

  @Test
  @DisplayName("sanitizeText - JavaScript void se limpia sin error")
  void sanitizeText_shouldCleanJavaScriptVoid() {
    // Given
    String jsInput = "javascript:void(0)";

    // When
    String result = InputSanitizer.sanitizeText(jsInput);

    // Then
    assertThat(result).isEqualTo("void(0)");
  }

  @Test
  @DisplayName("sanitizeText - iframes se limpian sin error")
  void sanitizeText_shouldCleanIframes() {
    // Given
    String iframeInput = "<iframe>content</iframe>";

    // When
    String result = InputSanitizer.sanitizeText(iframeInput);

    // Then
    assertThat(result).isEqualTo("content");
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "Mixed SELECT content with script",
      "Some union attack here",
      "Text with INSERT malicious"
  })
  @DisplayName("sanitizeText - Debe detectar texto con palabras SQL mezcladas")
  void sanitizeText_shouldDetectMixedSqlContent(String mixedSqlInput) {
    // When & Then
    assertThatThrownBy(() -> InputSanitizer.sanitizeText(mixedSqlInput))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("SQL injection");
  }

  @Test
  @DisplayName("sanitizeText - Debe detectar path traversal")
  void sanitizeText_shouldDetectPathTraversal() {
    // Given
    String pathTraversal = "../../../etc/passwd";

    // When & Then
    assertThatThrownBy(() -> InputSanitizer.sanitizeText(pathTraversal))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("path traversal");
  }

  @Test
  @DisplayName("sanitizeEmail - Debe sanitizar email válido")
  void sanitizeEmail_shouldSanitizeValidEmail() {
    // Given
    String validEmail = "USER@EXAMPLE.COM";

    // When
    String result = InputSanitizer.sanitizeEmail(validEmail);

    // Then
    assertThat(result).isEqualTo("user@example.com");
  }

  @Test
  @DisplayName("sanitizeEmail - Debe rechazar formato inválido")
  void sanitizeEmail_shouldRejectInvalidFormat() {
    // Given
    String invalidEmail = "notanemail"; // When & Then
    assertThatThrownBy(() -> InputSanitizer.sanitizeEmail(invalidEmail))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("El email debe tener un formato válido");
  }

  @Test
  @DisplayName("sanitizeEmail - Debe manejar entrada nula")
  void sanitizeEmail_shouldHandleNullInput() {
    // When & Then
    assertThat(InputSanitizer.sanitizeEmail(null)).isNull();
  }

  @Test
  @DisplayName("sanitizeName - Debe aceptar nombres válidos")
  void sanitizeName_shouldAcceptValidNames() {
    // Given
    String validName = "José María O'Connor-Smith";

    // When
    String result = InputSanitizer.sanitizeName(validName);

    // Then
    assertThat(result).isEqualTo(validName);
  }

  @Test
  @DisplayName("sanitizeName - Debe rechazar caracteres inválidos")
  void sanitizeName_shouldRejectInvalidCharacters() {
    // Given
    String invalidName = "John123@evil.com";

    // When & Then
    assertThatThrownBy(() -> InputSanitizer.sanitizeName(invalidName))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("caracteres no válidos");
  }

  @Test
  @DisplayName("sanitizeUrl - Debe aceptar URLs HTTP válidas")
  void sanitizeUrl_shouldAcceptValidHttpUrls() {
    // Given
    String validUrl = "https://example.com/path/file.html";

    // When
    String result = InputSanitizer.sanitizeUrl(validUrl);

    // Then
    assertThat(result).isEqualTo(validUrl);
  }

  @Test
  @DisplayName("sanitizeUrl - Debe rechazar protocolos no permitidos")
  void sanitizeUrl_shouldRejectInvalidProtocols() {
    // Given
    String invalidUrl = "ftp://evil.com";

    // When & Then
    assertThatThrownBy(() -> InputSanitizer.sanitizeUrl(invalidUrl))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("protocolo no permitido");
  }

  @Test
  @DisplayName("sanitizePhone - Debe aceptar teléfonos válidos")
  void sanitizePhone_shouldAcceptValidPhones() {
    // Given
    String validPhone = "+57 (123) 456-7890";

    // When
    String result = InputSanitizer.sanitizePhone(validPhone);

    // Then
    assertThat(result).isEqualTo(validPhone);
  }

  @Test
  @DisplayName("sanitizePhone - Debe rechazar caracteres inválidos")
  void sanitizePhone_shouldRejectInvalidCharacters() {
    // Given
    String invalidPhone = "123-ABC-456";

    // When & Then
    assertThatThrownBy(() -> InputSanitizer.sanitizePhone(invalidPhone))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("caracteres no válidos");
  }

  @Test
  @DisplayName("sanitizeIdentification - Debe aceptar ID alfanumérica")
  void sanitizeIdentification_shouldAcceptAlphanumericId() {
    // Given
    String validId = "ABC123456789";

    // When
    String result = InputSanitizer.sanitizeIdentification(validId);

    // Then
    assertThat(result).isEqualTo(validId);
  }

  @Test
  @DisplayName("sanitizeIdentification - Debe rechazar caracteres especiales")
  void sanitizeIdentification_shouldRejectSpecialCharacters() {
    // Given
    String invalidId = "123-456-789";

    // When & Then
    assertThatThrownBy(() -> InputSanitizer.sanitizeIdentification(invalidId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("caracteres no válidos");
  }
}