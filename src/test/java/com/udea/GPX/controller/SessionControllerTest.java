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
    @DisplayName("invalidateSession - Debe fallar con sessionId inválido")
    void invalidateSession_shouldFailWithInvalidSessionId() {
        // Given
        String invalidSessionId = "invalid@session#id";
        // No se necesita stub para authentication.getName() porque el test falla antes

        // When
        ResponseEntity<Map<String, String>> response = sessionController.invalidateSession(invalidSessionId,
                authentication);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("Formato de sessionId inválido");

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
        ResponseEntity<Map<String, String>> response = sessionController.invalidateAllSessions(authentication);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("message")).isEqualTo("Todas las sesiones han sido invalidadas exitosamente");

        verify(tokenService).invalidateAllUserTokens(1L);
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
        assertThat(info.get("activeSessions")).isEqualTo(0);
        assertThat(info.get("maxConcurrentSessions")).isEqualTo(5);
        assertThat(info.get("lastActivity")).isNull();

        verify(tokenService).getActiveSessions(1L);
    }
}