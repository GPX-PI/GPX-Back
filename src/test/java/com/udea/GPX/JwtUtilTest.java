package com.udea.gpx;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

@DisplayName("JwtUtil Tests")
class JwtUtilTest {

  private JwtUtil jwtUtil;

  @BeforeEach
  void setUp() {
    jwtUtil = new JwtUtil();
    // Configurar secret de prueba
    ReflectionTestUtils.setField(jwtUtil, "SECRET_KEY", "mySecretKeyForTestingPurposesWithMinimum32Characters");
    ReflectionTestUtils.setField(jwtUtil, "EXPIRATION_TIME", 3600L); // 1 hora
  }

  @Test
  @DisplayName("Debe generar token válido")
  void shouldGenerateValidToken() {
    // Given
    Long userId = 123L;
    boolean isAdmin = false;

    // When
    String token = jwtUtil.generateToken(userId, isAdmin);

    // Then
    assertThat(token).isNotNull();
    assertThat(token).isNotEmpty();
    assertThat(token.split("\\.")).hasSize(3); // JWT tiene 3 partes
  }

  @Test
  @DisplayName("Debe extraer userId del token")
  void shouldExtractUserIdFromToken() {
    // Given
    Long userId = 123L;
    boolean isAdmin = false;
    String token = jwtUtil.generateToken(userId, isAdmin);

    // When
    Long extractedUserId = jwtUtil.extractUserId(token);

    // Then
    assertThat(extractedUserId).isEqualTo(userId);
  }

  @Test
  @DisplayName("Debe extraer isAdmin del token")
  void shouldExtractIsAdminFromToken() {
    // Given
    Long userId = 123L;
    boolean isAdmin = true;
    String token = jwtUtil.generateToken(userId, isAdmin);

    // When
    boolean extractedIsAdmin = jwtUtil.extractIsAdmin(token);

    // Then
    assertThat(extractedIsAdmin).isEqualTo(isAdmin);
  }

  @Test
  @DisplayName("Debe extraer todos los claims")
  void shouldExtractAllClaims() {
    // Given
    Long userId = 123L;
    boolean isAdmin = true;
    String token = jwtUtil.generateToken(userId, isAdmin);

    // When
    Claims claims = jwtUtil.extractAllClaims(token);

    // Then
    assertThat(claims).isNotNull();
    assertThat(claims.get("userId")).isEqualTo(userId.intValue()); // Jackson convierte a Integer
    assertThat(claims.get("admin")).isEqualTo(isAdmin);
    assertThat(claims.getSubject()).isEqualTo(userId.toString());
    assertThat(claims.getExpiration()).isAfter(new java.util.Date());
  }

  @Test
  @DisplayName("Debe validar token válido")
  void shouldValidateValidToken() {
    // Given
    Long userId = 123L;
    boolean isAdmin = false;
    String token = jwtUtil.generateToken(userId, isAdmin);

    // When
    Boolean isValid = jwtUtil.validateToken(token);

    // Then
    assertThat(isValid).isTrue();
  }

  @Test
  @DisplayName("Debe rechazar token expirado")
  void shouldRejectExpiredToken() {
    // Given
    jwtUtil = new JwtUtil();
    ReflectionTestUtils.setField(jwtUtil, "SECRET_KEY", "mySecretKeyForTestingPurposesWithMinimum32Characters");
    ReflectionTestUtils.setField(jwtUtil, "EXPIRATION_TIME", -1L); // Expiración negativa

    Long userId = 123L;
    boolean isAdmin = false;
    String expiredToken = jwtUtil.generateToken(userId, isAdmin);

    // When
    Boolean isValid = jwtUtil.validateToken(expiredToken);

    // Then
    assertThat(isValid).isFalse();
  }

  @Test
  @DisplayName("Debe manejar token malformado")
  void shouldHandleMalformedToken() {
    // Given
    String malformedToken = "malformed.token.here";

    // When
    Boolean isValid = jwtUtil.validateToken(malformedToken);

    // Then
    assertThat(isValid).isFalse();
  }

  @Test
  @DisplayName("Debe manejar token nulo")
  void shouldHandleNullToken() {
    // When
    Boolean isValid = jwtUtil.validateToken(null);

    // Then
    assertThat(isValid).isFalse();
  }

  @Test
  @DisplayName("Debe generar tokens únicos para mismo usuario")
  void shouldGenerateUniqueTokensForSameUser() throws InterruptedException {
    // Given
    Long userId = 123L;
    boolean isAdmin = false;

    // When
    String token1 = jwtUtil.generateToken(userId, isAdmin);
    Thread.sleep(1000); // Esperar 1 segundo para diferentes timestamps
    String token2 = jwtUtil.generateToken(userId, isAdmin);

    // Then
    assertThat(token1).isNotEqualTo(token2);
  }

  @Test
  @DisplayName("Debe generar tokens diferentes para admin y no admin")
  void shouldGenerateDifferentTokensForAdminAndNonAdmin() {
    // Given
    Long userId = 123L;

    // When
    String adminToken = jwtUtil.generateToken(userId, true);
    String userToken = jwtUtil.generateToken(userId, false);

    // Then
    assertThat(adminToken).isNotEqualTo(userToken);
    assertThat(jwtUtil.extractIsAdmin(adminToken)).isTrue();
    assertThat(jwtUtil.extractIsAdmin(userToken)).isFalse();
  }

  @Test
  @DisplayName("Debe manejar diferentes userIds")
  void shouldHandleDifferentUserIds() {
    // Given
    Long userId1 = 123L;
    Long userId2 = 456L;
    boolean isAdmin = false;

    // When
    String token1 = jwtUtil.generateToken(userId1, isAdmin);
    String token2 = jwtUtil.generateToken(userId2, isAdmin);

    // Then
    assertThat(jwtUtil.extractUserId(token1)).isEqualTo(userId1);
    assertThat(jwtUtil.extractUserId(token2)).isEqualTo(userId2);
    assertThat(token1).isNotEqualTo(token2);
  }

  @Test
  @DisplayName("Debe manejar extractUserId con token inválido")
  void shouldHandleExtractUserIdWithInvalidToken() {
    // Given
    String invalidToken = "invalid.token.here";

    // When & Then
    assertThatThrownBy(() -> jwtUtil.extractUserId(invalidToken))
        .isInstanceOf(Exception.class);
  }

  @Test
  @DisplayName("Debe manejar extractIsAdmin con token inválido")
  void shouldHandleExtractIsAdminWithInvalidToken() {
    // Given
    String invalidToken = "invalid.token.here";

    // When & Then
    assertThatThrownBy(() -> jwtUtil.extractIsAdmin(invalidToken))
        .isInstanceOf(Exception.class);
  }
}