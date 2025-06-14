package com.udea.gpx.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.udea.gpx.service.TokenService;
import com.udea.gpx.service.TokenService.TokenPair;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthTokenController Tests")
class AuthTokenControllerTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private AuthTokenController authTokenController;

    @BeforeEach
    void setUp() {
        // Configuración básica para todos los tests
    }

    // ========== REFRESH TOKEN TESTS ==========

    @Test
    @DisplayName("refreshToken - Debe refrescar token exitosamente")
    void refreshToken_shouldRefreshSuccessfully() {
        // Given
        Map<String, String> requestData = new HashMap<>();
        requestData.put("refreshToken", "valid-refresh-token");

        TokenPair newTokenPair = new TokenPair("new-access-token", "new-refresh-token");
        when(tokenService.refreshAccessToken("valid-refresh-token")).thenReturn(newTokenPair);

        // When
        ResponseEntity<Map<String, Object>> response = authTokenController.refreshToken(requestData);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        Map<String, Object> responseBody = response.getBody();
        assertThat(responseBody.get("accessToken")).isEqualTo("new-access-token");
        assertThat(responseBody.get("refreshToken")).isEqualTo("new-refresh-token");
        assertThat(responseBody.get("message")).isEqualTo("Token refrescado exitosamente");

        verify(tokenService).refreshAccessToken("valid-refresh-token");
    }

    @Test
    @DisplayName("refreshToken - Debe fallar cuando refresh token es null")
    void refreshToken_shouldFailWhenRefreshTokenIsNull() {
        // Given
        Map<String, String> requestData = new HashMap<>();
        requestData.put("refreshToken", null);

        // When
        ResponseEntity<Map<String, Object>> response = authTokenController.refreshToken(requestData);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();

        Map<String, Object> responseBody = response.getBody();
        assertThat(responseBody.get("error")).isEqualTo("Refresh token requerido");

        verify(tokenService, never()).refreshAccessToken(any());
    }

    @Test
    @DisplayName("refreshToken - Debe fallar cuando refresh token está vacío")
    void refreshToken_shouldFailWhenRefreshTokenIsEmpty() {
        // Given
        Map<String, String> requestData = new HashMap<>();
        requestData.put("refreshToken", "   ");

        // When
        ResponseEntity<Map<String, Object>> response = authTokenController.refreshToken(requestData);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();

        Map<String, Object> responseBody = response.getBody();
        assertThat(responseBody.get("error")).isEqualTo("Refresh token requerido");

        verify(tokenService, never()).refreshAccessToken(any());
    }

    @Test
    @DisplayName("refreshToken - Debe fallar cuando refresh token es inválido")
    void refreshToken_shouldFailWhenRefreshTokenIsInvalid() {
        // Given
        Map<String, String> requestData = new HashMap<>();
        requestData.put("refreshToken", "invalid-refresh-token");

        when(tokenService.refreshAccessToken("invalid-refresh-token"))
                .thenThrow(new IllegalArgumentException("Refresh token inválido"));

        // When
        ResponseEntity<Map<String, Object>> response = authTokenController.refreshToken(requestData);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();

        Map<String, Object> responseBody = response.getBody();
        assertThat(responseBody.get("error")).isEqualTo("Refresh token inválido");

        verify(tokenService).refreshAccessToken("invalid-refresh-token");
    }

    // ========== LOGOUT TESTS ==========

    @Test
    @DisplayName("logout - Debe hacer logout exitosamente")
    void logout_shouldLogoutSuccessfully() {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-jwt-token");
        doNothing().when(tokenService).invalidateToken("valid-jwt-token");

        // When
        ResponseEntity<Map<String, Object>> response = authTokenController.logout(request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        Map<String, Object> responseBody = response.getBody();
        assertThat(responseBody.get("message")).isEqualTo("Logout exitoso");

        verify(tokenService).invalidateToken("valid-jwt-token");
    }

    @Test
    @DisplayName("logout - Debe invalidar token exitosamente")
    void logout_shouldInvalidateTokenSuccessfully() {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        doNothing().when(tokenService).invalidateToken(anyString());

        // When
        ResponseEntity<Map<String, Object>> response = authTokenController.logout(request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("message")).isEqualTo("Logout exitoso");

        verify(tokenService).invalidateToken("valid-token");
    }

    @Test
    @DisplayName("logout - Debe fallar cuando no hay token")
    void logout_shouldFailWhenNoToken() {
        // Given
        when(request.getHeader("Authorization")).thenReturn(null);

        // When
        ResponseEntity<Map<String, Object>> response = authTokenController.logout(request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();

        Map<String, Object> responseBody = response.getBody();
        assertThat(responseBody.get("error")).isEqualTo("Token no encontrado");

        verify(tokenService, never()).invalidateToken(any());
    }

    @Test
    @DisplayName("logout - Debe fallar cuando no hay token de autorización")
    void logout_shouldFailWhenNoAuthorizationToken() {
        // Given
        when(request.getHeader("Authorization")).thenReturn(null);

        // When
        ResponseEntity<Map<String, Object>> response = authTokenController.logout(request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("Token no encontrado");

        verify(tokenService, never()).invalidateToken(any());
    }

    @Test
    @DisplayName("logout - Debe fallar cuando el formato del token es incorrecto")
    void logout_shouldFailWhenTokenFormatIsIncorrect() {
        // Given
        when(request.getHeader("Authorization")).thenReturn("InvalidFormat");

        // When
        ResponseEntity<Map<String, Object>> response = authTokenController.logout(request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("Token no encontrado");

        verify(tokenService, never()).invalidateToken(any());
    }

    // ========== LOGOUT ALL TESTS ==========

    @Test
    @DisplayName("logoutAll - Debe invalidar todos los tokens exitosamente")
    void logoutAll_shouldInvalidateAllTokensSuccessfully() {
        // Given
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("userId", 123L);

        doNothing().when(tokenService).invalidateAllUserTokens(anyLong());

        // When
        ResponseEntity<Map<String, Object>> response = authTokenController.logoutAll(requestData);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("message")).isEqualTo("Todas las sesiones cerradas exitosamente");

        verify(tokenService).invalidateAllUserTokens(123L);
    }

    @Test
    @DisplayName("logoutAll - Debe fallar cuando no se proporciona userId")
    void logoutAll_shouldFailWhenUserIdIsNotProvided() {
        // Given
        Map<String, Object> requestData = new HashMap<>();

        // When
        ResponseEntity<Map<String, Object>> response = authTokenController.logoutAll(requestData);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("ID de usuario requerido");

        verify(tokenService, never()).invalidateAllUserTokens(anyLong());
    }

    @Test
    @DisplayName("logoutAll - Debe fallar cuando userId no es un número válido")
    void logoutAll_shouldFailWhenUserIdIsNotValid() {
        // Given
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("userId", "not-a-number");

        // When
        ResponseEntity<Map<String, Object>> response = authTokenController.logoutAll(requestData);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("ID de usuario inválido");

        verify(tokenService, never()).invalidateAllUserTokens(anyLong());
    }

    // ========== VERIFY TOKEN TESTS ==========

    @Test
    @DisplayName("verifyToken - Debe verificar token válido exitosamente")
    void verifyToken_shouldVerifyValidTokenSuccessfully() {
        // Given
        Map<String, String> requestData = new HashMap<>();
        requestData.put("token", "valid-token");

        when(tokenService.isTokenBlacklisted("valid-token")).thenReturn(false);

        // When
        ResponseEntity<Map<String, Object>> response = authTokenController.verifyToken(requestData);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("valid")).isEqualTo(true);
        assertThat(response.getBody().get("blacklisted")).isEqualTo(false);

        verify(tokenService).isTokenBlacklisted("valid-token");
    }

    @Test
    @DisplayName("verifyToken - Debe verificar token en blacklist")
    void verifyToken_shouldVerifyBlacklistedToken() {
        // Given
        Map<String, String> requestData = new HashMap<>();
        requestData.put("token", "blacklisted-token");

        when(tokenService.isTokenBlacklisted("blacklisted-token")).thenReturn(true);

        // When
        ResponseEntity<Map<String, Object>> response = authTokenController.verifyToken(requestData);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("valid")).isEqualTo(false);
        assertThat(response.getBody().get("blacklisted")).isEqualTo(true);

        verify(tokenService).isTokenBlacklisted("blacklisted-token");
    }

    // ========== CLEANUP TOKENS TESTS ==========

    @Test
    @DisplayName("cleanupTokens - Debe limpiar tokens expirados exitosamente")
    void cleanupTokens_shouldCleanupExpiredTokensSuccessfully() {
        // Given
        doNothing().when(tokenService).cleanupExpiredTokens();

        // When
        ResponseEntity<Map<String, Object>> response = authTokenController.cleanupTokens();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsEntry("message", "Limpieza de tokens completada");

        verify(tokenService).cleanupExpiredTokens();
    }

    @Test
    @DisplayName("cleanupTokens - Debe manejar errores durante la limpieza")
    void cleanupTokens_shouldHandleErrorsDuringCleanup() {
        // Given
        doThrow(new RuntimeException("Error durante limpieza")).when(tokenService).cleanupExpiredTokens();

        // When
        ResponseEntity<Map<String, Object>> response = authTokenController.cleanupTokens();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsEntry("error", "Error durante limpieza");

        verify(tokenService).cleanupExpiredTokens();
    }
}