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
    @DisplayName("verifyToken - Debe verificar token válido")
    void verifyToken_shouldVerifyValidToken() {
        // Given
        Map<String, String> requestData = new HashMap<>();
        requestData.put("token", "valid-jwt-token");

        when(tokenService.isTokenBlacklisted("valid-jwt-token")).thenReturn(false);

        // When
        ResponseEntity<Map<String, Object>> response = authTokenController.verifyToken(requestData);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        Map<String, Object> responseBody = response.getBody();
        assertThat(responseBody.get("valid")).isEqualTo(true);
        assertThat(responseBody.get("blacklisted")).isEqualTo(false);

        verify(tokenService).isTokenBlacklisted("valid-jwt-token");
    }

    @Test
    @DisplayName("cleanupTokens - Debe limpiar tokens expirados exitosamente")
    void cleanupTokens_shouldCleanupSuccessfully() {
        // Given
        doNothing().when(tokenService).cleanupExpiredTokens();

        // When
        ResponseEntity<Map<String, Object>> response = authTokenController.cleanupTokens();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        Map<String, Object> responseBody = response.getBody();
        assertThat(responseBody.get("message")).isEqualTo("Limpieza de tokens completada");

        verify(tokenService).cleanupExpiredTokens();
    }
}