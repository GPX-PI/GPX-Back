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
import org.springframework.security.core.Authentication;

import com.udea.gpx.service.TokenService;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SessionController Tests")
class SessionControllerTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private SessionController sessionController;

    @BeforeEach
    void setUp() {
        // Configuración básica para todos los tests
    }

    // ========== GET ACTIVE SESSIONS TESTS ==========

    @Test
    @DisplayName("getActiveSessions - Debe retornar sesiones activas exitosamente")
    void getActiveSessions_shouldReturnActiveSessionsSuccessfully() {
        // Given
        when(authentication.getName()).thenReturn("1");
        // Mock simplificado sin constructor específico
        when(tokenService.getActiveSessions(1L)).thenReturn(List.of());

        // When
        ResponseEntity<List<SessionController.SessionInfo>> response = sessionController
                .getActiveSessions(authentication);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        verify(tokenService).getActiveSessions(1L);
    }

    @Test
    @DisplayName("getActiveSessions - Debe manejar userId inválido")
    void getActiveSessions_shouldHandleInvalidUserId() {
        // Given
        when(authentication.getName()).thenReturn("invalid-id");

        // When & Then
        assertThatThrownBy(() -> sessionController.getActiveSessions(authentication))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ID de usuario inválido");

        verify(tokenService, never()).getActiveSessions(any());
    }

    // ========== INVALIDATE SESSION TESTS ==========

    @Test
    @DisplayName("invalidateSession - Debe invalidar sesión exitosamente")
    void invalidateSession_shouldInvalidateSessionSuccessfully() {
        // Given
        String sessionId = "valid-session-123";
        when(authentication.getName()).thenReturn("1");
        // Simular una sesión existente
        TokenService.SessionInfo mockSession = mock(TokenService.SessionInfo.class);
        when(mockSession.getSessionId()).thenReturn(sessionId);
        when(tokenService.getActiveSessions(1L)).thenReturn(List.of(mockSession));
        doNothing().when(tokenService).invalidateSession(sessionId);

        // When
        ResponseEntity<Map<String, String>> response = sessionController.invalidateSession(sessionId, authentication);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("message")).isEqualTo("Sesión invalidada exitosamente");

        verify(tokenService).getActiveSessions(1L);
        verify(tokenService).invalidateSession(sessionId);
    }

    @Test
    @DisplayName("invalidateSession - Debe retornar 404 si la sesión no pertenece al usuario")
    void invalidateSession_shouldReturn404WhenSessionDoesNotBelongToUser() {
        // Given
        String sessionId = "valid-session-123";
        when(authentication.getName()).thenReturn("1");
        // Simular que no hay sesiones para el usuario (lista vacía)
        when(tokenService.getActiveSessions(1L)).thenReturn(List.of());

        // When
        ResponseEntity<Map<String, String>> response = sessionController.invalidateSession(sessionId, authentication);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        verify(tokenService).getActiveSessions(1L);
        verify(tokenService, never()).invalidateSession(anyString());
    }

    @Test
    @DisplayName("invalidateSession - Debe fallar con sessionId inválido")
    void invalidateSession_shouldFailWithInvalidSessionId() {
        // Given
        String invalidSessionId = "invalid@session#id";
        // No se necesita stub para authentication.getName() porque el test falla antes

        // When
        ResponseEntity<Map<String, String>> response = sessionController.invalidateSession(invalidSessionId,
                authentication); // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull()
                .containsEntry("error", "Formato de sessionId inválido");

        verify(tokenService, never()).getActiveSessions(any());
        verify(tokenService, never()).invalidateSession(any());
    }

    @Test
    @DisplayName("invalidateSession - Debe manejar userId inválido")
    void invalidateSession_shouldHandleInvalidUserId() {
        // Given
        String sessionId = "valid-session-123";
        when(authentication.getName()).thenReturn("invalid-id");

        // When & Then
        assertThatThrownBy(() -> sessionController.invalidateSession(sessionId, authentication))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ID de usuario inválido");

        verify(tokenService, never()).getActiveSessions(any());
        verify(tokenService, never()).invalidateSession(any());
    }

    // ========== INVALIDATE ALL SESSIONS TESTS ==========

    @Test
    @DisplayName("invalidateAllSessions - Debe invalidar todas las sesiones exitosamente")
    void invalidateAllSessions_shouldInvalidateAllSessionsSuccessfully() {
        // Given
        when(authentication.getName()).thenReturn("1");
        doNothing().when(tokenService).invalidateAllUserTokens(1L);

        // When
        ResponseEntity<Map<String, String>> response = sessionController.invalidateAllSessions(authentication); // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull()
                .containsEntry("message", "Todas las sesiones han sido invalidadas exitosamente");

        verify(tokenService).invalidateAllUserTokens(1L);
    }

    @Test
    @DisplayName("invalidateAllSessions - Debe manejar userId inválido")
    void invalidateAllSessions_shouldHandleInvalidUserId() {
        // Given
        when(authentication.getName()).thenReturn("invalid-id");

        // When & Then
        assertThatThrownBy(() -> sessionController.invalidateAllSessions(authentication))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ID de usuario inválido");

        verify(tokenService, never()).invalidateAllUserTokens(any());
    }

    // ========== GET CURRENT SESSION INFO TESTS ==========

    @Test
    @DisplayName("getCurrentSessionInfo - Debe retornar información de sesión exitosamente")
    void getCurrentSessionInfo_shouldReturnSessionInfoSuccessfully() {
        // Given
        when(authentication.getName()).thenReturn("1");
        when(tokenService.getActiveSessions(1L)).thenReturn(List.of());

        // When
        ResponseEntity<Map<String, Object>> response = sessionController.getCurrentSessionInfo(authentication);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        Map<String, Object> info = response.getBody();
        assertThat(info)
                .containsEntry("activeSessions", 0)
                .containsEntry("maxConcurrentSessions", 5)
                .containsEntry("lastActivity", null);

        verify(tokenService).getActiveSessions(1L);
    }

    @Test
    @DisplayName("getCurrentSessionInfo - Debe manejar userId inválido")
    void getCurrentSessionInfo_shouldHandleInvalidUserId() {
        // Given
        when(authentication.getName()).thenReturn("invalid-id");

        // When & Then
        assertThatThrownBy(() -> sessionController.getCurrentSessionInfo(authentication))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ID de usuario inválido");

        verify(tokenService, never()).getActiveSessions(any());
    }

    @Test
    @DisplayName("getCurrentSessionInfo - Debe manejar sesiones con actividad")
    void getCurrentSessionInfo_shouldHandleSessionsWithActivity() {
        // Given
        when(authentication.getName()).thenReturn("1");

        // Crear una sesión con actividad
        TokenService.SessionInfo mockSession = mock(TokenService.SessionInfo.class);
        when(mockSession.getLastActivity()).thenReturn(java.time.Instant.now());
        when(tokenService.getActiveSessions(1L)).thenReturn(List.of(mockSession));

        // When
        ResponseEntity<Map<String, Object>> response = sessionController.getCurrentSessionInfo(authentication);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        Map<String, Object> info = response.getBody();
        assertThat(info)
                .containsEntry("activeSessions", 1)
                .containsEntry("maxConcurrentSessions", 5)
                .containsKey("lastActivity");
        assertThat(info.get("lastActivity")).isNotNull();

        verify(tokenService).getActiveSessions(1L);
    }

    @Test
    @DisplayName("getSafeUserId - Debe manejar nombre de usuario vacío")
    void getSafeUserId_shouldHandleEmptyUsername() {
        // Given
        when(authentication.getName()).thenReturn("");

        // When & Then
        assertThatThrownBy(() -> sessionController.getActiveSessions(authentication))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Usuario no válido");

        verify(tokenService, never()).getActiveSessions(any());
    }

    @Test
    @DisplayName("getSafeUserId - Debe manejar nombre de usuario nulo")
    void getSafeUserId_shouldHandleNullUsername() {
        // Given
        when(authentication.getName()).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> sessionController.getActiveSessions(authentication))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Usuario no válido");

        verify(tokenService, never()).getActiveSessions(any());
    }

    // ========== SESSION INFO TESTS ==========

    @Test
    @DisplayName("SessionInfo - Debe exponer correctamente getters")
    void sessionInfo_shouldExposeGettersCorrectly() {
        // Given
        String sessionId = "test-session-id";
        java.time.Instant createdAt = java.time.Instant.now().minusSeconds(3600);
        java.time.Instant lastActivity = java.time.Instant.now();
        String userAgent = "Mozilla/5.0 Test Browser";
        String ipAddress = "192.168.1.1";

        // When
        SessionController.SessionInfo sessionInfo = new SessionController.SessionInfo(
                sessionId, createdAt, lastActivity, userAgent, ipAddress);

        // Then
        assertThat(sessionInfo.getSessionId()).isEqualTo(sessionId);
        assertThat(sessionInfo.getCreatedAt()).isEqualTo(createdAt);
        assertThat(sessionInfo.getLastActivity()).isEqualTo(lastActivity);
        assertThat(sessionInfo.getUserAgent()).isEqualTo(userAgent);
        assertThat(sessionInfo.getIpAddress()).isEqualTo(ipAddress);
    }

    @Test
    @DisplayName("getActiveSessions - Debe convertir SessionInfo correctamente")
    void getActiveSessions_shouldConvertSessionInfoCorrectly() {
        // Given
        String sessionId = "test-session-id";
        java.time.Instant createdAt = java.time.Instant.now().minusSeconds(3600);
        java.time.Instant lastActivity = java.time.Instant.now();
        String userAgent = "Mozilla/5.0 Test Browser";
        String ipAddress = "192.168.1.1";

        // Crear un mock de SessionInfo del servicio
        TokenService.SessionInfo mockServiceSessionInfo = mock(TokenService.SessionInfo.class);
        when(mockServiceSessionInfo.getSessionId()).thenReturn(sessionId);
        when(mockServiceSessionInfo.getCreatedAt()).thenReturn(createdAt);
        when(mockServiceSessionInfo.getLastActivity()).thenReturn(lastActivity);
        when(mockServiceSessionInfo.getUserAgent()).thenReturn(userAgent);
        when(mockServiceSessionInfo.getIpAddress()).thenReturn(ipAddress);

        when(authentication.getName()).thenReturn("1");
        when(tokenService.getActiveSessions(1L)).thenReturn(List.of(mockServiceSessionInfo));

        // When
        ResponseEntity<List<SessionController.SessionInfo>> response = sessionController
                .getActiveSessions(authentication);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull().hasSize(1);

        SessionController.SessionInfo dto = response.getBody().get(0);
        assertThat(dto.getSessionId()).isEqualTo(sessionId);
        assertThat(dto.getCreatedAt()).isEqualTo(createdAt);
        assertThat(dto.getLastActivity()).isEqualTo(lastActivity);
        assertThat(dto.getUserAgent()).isEqualTo(userAgent);
        assertThat(dto.getIpAddress()).isEqualTo(ipAddress);
    }
}