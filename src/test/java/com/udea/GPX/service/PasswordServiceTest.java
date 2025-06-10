package com.udea.GPX.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.*;

@DisplayName("PasswordService Tests")
class PasswordServiceTest {

    private PasswordService passwordService;

    @BeforeEach
    void setUp() {
        passwordService = new PasswordService();
    }

    // ========== HASH PASSWORD TESTS ==========

    @Test
    @DisplayName("hashPassword - Debe hashear contraseña válida")
    void hashPassword_shouldHashValidPassword() {
        // Given
        String rawPassword = "password123";

        // When
        String hashedPassword = passwordService.hashPassword(rawPassword);

        // Then
        assertThat(hashedPassword).isNotNull();
        assertThat(hashedPassword).isNotEqualTo(rawPassword);
        assertThat(hashedPassword).startsWith("$2a$12$");
    }

    @Test
    @DisplayName("hashPassword - Debe lanzar excepción si password es nula")
    void hashPassword_shouldThrowExceptionIfPasswordIsNull() {
        // When & Then
        assertThatThrownBy(() -> passwordService.hashPassword(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("La contraseña no puede estar vacía");
    }

    @Test
    @DisplayName("hashPassword - Debe lanzar excepción si password está vacía")
    void hashPassword_shouldThrowExceptionIfPasswordIsEmpty() {
        // When & Then
        assertThatThrownBy(() -> passwordService.hashPassword("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("La contraseña no puede estar vacía");
    }

    @Test
    @DisplayName("hashPassword - Debe trimear espacios en blanco")
    void hashPassword_shouldTrimWhitespace() {
        // Given
        String rawPassword = "  password123  ";

        // When
        String hashedPassword = passwordService.hashPassword(rawPassword);

        // Then
        assertThat(hashedPassword).isNotNull();
        assertThat(passwordService.verifyPassword("password123", hashedPassword)).isTrue();
    }

    // ========== VERIFY PASSWORD TESTS ==========

    @Test
    @DisplayName("verifyPassword - Debe verificar contraseña correcta")
    void verifyPassword_shouldVerifyCorrectPassword() {
        // Given
        String rawPassword = "password123";
        String hashedPassword = passwordService.hashPassword(rawPassword);

        // When
        boolean result = passwordService.verifyPassword(rawPassword, hashedPassword);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("verifyPassword - Debe rechazar contraseña incorrecta")
    void verifyPassword_shouldRejectIncorrectPassword() {
        // Given
        String rawPassword = "password123";
        String wrongPassword = "wrongpassword";
        String hashedPassword = passwordService.hashPassword(rawPassword);

        // When
        boolean result = passwordService.verifyPassword(wrongPassword, hashedPassword);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("verifyPassword - Debe retornar false si rawPassword es nula")
    void verifyPassword_shouldReturnFalseIfRawPasswordIsNull() {
        // Given
        String hashedPassword = passwordService.hashPassword("password123");

        // When
        boolean result = passwordService.verifyPassword(null, hashedPassword);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("verifyPassword - Debe retornar false si hashedPassword es nula")
    void verifyPassword_shouldReturnFalseIfHashedPasswordIsNull() {
        // When
        boolean result = passwordService.verifyPassword("password123", null);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("verifyPassword - Debe trimear espacios en blanco")
    void verifyPassword_shouldTrimWhitespace() {
        // Given
        String rawPassword = "password123";
        String hashedPassword = passwordService.hashPassword(rawPassword);

        // When
        boolean result = passwordService.verifyPassword("  password123  ", hashedPassword);

        // Then
        assertThat(result).isTrue();
    }

    // ========== PASSWORD VALIDATION TESTS ==========

    @Test
    @DisplayName("isPasswordValid - Debe validar contraseña correcta")
    void isPasswordValid_shouldValidateCorrectPassword() {
        // When
        boolean result = passwordService.isPasswordValid("password123");

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isPasswordValid - Debe rechazar contraseña nula")
    void isPasswordValid_shouldRejectNullPassword() {
        // When
        boolean result = passwordService.isPasswordValid(null);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isPasswordValid - Debe rechazar contraseña vacía")
    void isPasswordValid_shouldRejectEmptyPassword() {
        // When
        boolean result = passwordService.isPasswordValid("   ");

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isPasswordValid - Debe rechazar contraseña muy corta")
    void isPasswordValid_shouldRejectTooShortPassword() {
        // When
        boolean result = passwordService.isPasswordValid("pass1");

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isPasswordValid - Debe rechazar contraseña muy larga")
    void isPasswordValid_shouldRejectTooLongPassword() {
        // Given
        String longPassword = "a".repeat(100) + "1".repeat(30); // 130 caracteres

        // When
        boolean result = passwordService.isPasswordValid(longPassword);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isPasswordValid - Debe rechazar contraseña sin letras")
    void isPasswordValid_shouldRejectPasswordWithoutLetters() {
        // When
        boolean result = passwordService.isPasswordValid("12345678");

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isPasswordValid - Debe rechazar contraseña sin números")
    void isPasswordValid_shouldRejectPasswordWithoutNumbers() {
        // When
        boolean result = passwordService.isPasswordValid("password");

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isPasswordValid - Debe validar contraseña en el límite mínimo")
    void isPasswordValid_shouldValidatePasswordAtMinimumLimit() {
        // When
        boolean result = passwordService.isPasswordValid("abcdefg1");

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isPasswordValid - Debe validar contraseña en el límite máximo")
    void isPasswordValid_shouldValidatePasswordAtMaximumLimit() {
        // Given
        String maxPassword = "a".repeat(127) + "1"; // 128 caracteres exactos

        // When
        boolean result = passwordService.isPasswordValid(maxPassword);

        // Then
        assertThat(result).isTrue();
    }

    // ========== PASSWORD VALIDATION MESSAGE TESTS ==========

    @Test
    @DisplayName("getPasswordValidationMessage - Debe retornar mensaje para contraseña válida")
    void getPasswordValidationMessage_shouldReturnMessageForValidPassword() {
        // When
        String message = passwordService.getPasswordValidationMessage("password123");

        // Then
        assertThat(message).isEqualTo("Contraseña válida");
    }

    @Test
    @DisplayName("getPasswordValidationMessage - Debe retornar mensaje para contraseña nula")
    void getPasswordValidationMessage_shouldReturnMessageForNullPassword() {
        // When
        String message = passwordService.getPasswordValidationMessage(null);

        // Then
        assertThat(message).isEqualTo("La contraseña es requerida");
    }

    @Test
    @DisplayName("getPasswordValidationMessage - Debe retornar mensaje para contraseña vacía")
    void getPasswordValidationMessage_shouldReturnMessageForEmptyPassword() {
        // When
        String message = passwordService.getPasswordValidationMessage("   ");

        // Then
        assertThat(message).isEqualTo("La contraseña es requerida");
    }

    @Test
    @DisplayName("getPasswordValidationMessage - Debe retornar mensaje para contraseña muy corta")
    void getPasswordValidationMessage_shouldReturnMessageForTooShortPassword() {
        // When
        String message = passwordService.getPasswordValidationMessage("short");

        // Then
        assertThat(message).isEqualTo("La contraseña debe tener al menos 8 caracteres");
    }

    @Test
    @DisplayName("getPasswordValidationMessage - Debe retornar mensaje para contraseña muy larga")
    void getPasswordValidationMessage_shouldReturnMessageForTooLongPassword() {
        // Given
        String longPassword = "a".repeat(130);

        // When
        String message = passwordService.getPasswordValidationMessage(longPassword);

        // Then
        assertThat(message).isEqualTo("La contraseña no puede tener más de 128 caracteres");
    }

    @Test
    @DisplayName("getPasswordValidationMessage - Debe retornar mensaje para contraseña sin letras")
    void getPasswordValidationMessage_shouldReturnMessageForPasswordWithoutLetters() {
        // When
        String message = passwordService.getPasswordValidationMessage("12345678");

        // Then
        assertThat(message).isEqualTo("La contraseña debe contener al menos una letra");
    }

    @Test
    @DisplayName("getPasswordValidationMessage - Debe retornar mensaje para contraseña sin números")
    void getPasswordValidationMessage_shouldReturnMessageForPasswordWithoutNumbers() {
        // When
        String message = passwordService.getPasswordValidationMessage("passwordonly");

        // Then
        assertThat(message).isEqualTo("La contraseña debe contener al menos un número");
    }

    // ========== BCRYPT HASH DETECTION TESTS ==========

    @Test
    @DisplayName("isBCryptHash - Debe detectar hash BCrypt 2a")
    void isBCryptHash_shouldDetectBCryptHash2a() {
        // Given
        String bcryptHash = "$2a$12$hashedpassword";

        // When
        boolean result = passwordService.isBCryptHash(bcryptHash);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isBCryptHash - Debe detectar hash BCrypt 2b")
    void isBCryptHash_shouldDetectBCryptHash2b() {
        // Given
        String bcryptHash = "$2b$12$hashedpassword";

        // When
        boolean result = passwordService.isBCryptHash(bcryptHash);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isBCryptHash - Debe detectar hash BCrypt 2y")
    void isBCryptHash_shouldDetectBCryptHash2y() {
        // Given
        String bcryptHash = "$2y$12$hashedpassword";

        // When
        boolean result = passwordService.isBCryptHash(bcryptHash);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isBCryptHash - Debe rechazar contraseña en texto plano")
    void isBCryptHash_shouldRejectPlainTextPassword() {
        // When
        boolean result = passwordService.isBCryptHash("plainpassword");

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isBCryptHash - Debe rechazar hash nulo")
    void isBCryptHash_shouldRejectNullHash() {
        // When
        boolean result = passwordService.isBCryptHash(null);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isBCryptHash - Debe detectar hash real generado por el servicio")
    void isBCryptHash_shouldDetectRealGeneratedHash() {
        // Given
        String hashedPassword = passwordService.hashPassword("password123");

        // When
        boolean result = passwordService.isBCryptHash(hashedPassword);

        // Then
        assertThat(result).isTrue();
    }
} 