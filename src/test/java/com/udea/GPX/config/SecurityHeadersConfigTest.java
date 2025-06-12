package com.udea.gpx.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SecurityHeadersConfig Tests")
class SecurityHeadersConfigTest {

    private SecurityHeadersConfig securityHeadersConfig;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        securityHeadersConfig = new SecurityHeadersConfig();
    }

    @Test
    @DisplayName("securityHeadersFilter debe crear OncePerRequestFilter")
    void testSecurityHeadersFilterCreatesFilter() {
        OncePerRequestFilter filter = securityHeadersConfig.securityHeadersFilter();

        assertNotNull(filter);
        assertTrue(filter instanceof OncePerRequestFilter);
        assertTrue(filter instanceof SecurityHeadersConfig.SecurityHeadersFilter);
    }

    @Test
    @DisplayName("SecurityHeadersFilter debe agregar todos los headers de seguridad básicos")
    void testSecurityHeadersFilterAddsBasicSecurityHeaders() throws ServletException, IOException {
        SecurityHeadersConfig.SecurityHeadersFilter filter = new SecurityHeadersConfig.SecurityHeadersFilter();

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        // Verificar headers de seguridad básicos
        assertEquals("max-age=31536000; includeSubDomains; preload",
                response.getHeader("Strict-Transport-Security"));
        assertEquals("nosniff", response.getHeader("X-Content-Type-Options"));
        assertEquals("DENY", response.getHeader("X-Frame-Options"));
        assertEquals("1; mode=block", response.getHeader("X-XSS-Protection"));
        assertEquals("strict-origin-when-cross-origin", response.getHeader("Referrer-Policy"));
        assertEquals("require-corp", response.getHeader("Cross-Origin-Embedder-Policy"));
        assertEquals("same-origin", response.getHeader("Cross-Origin-Opener-Policy"));
        assertEquals("same-origin", response.getHeader("Cross-Origin-Resource-Policy"));
        assertEquals("", response.getHeader("Server"));
    }

    @Test
    @DisplayName("SecurityHeadersFilter debe agregar Content Security Policy")
    void testSecurityHeadersFilterAddsCSP() throws ServletException, IOException {
        SecurityHeadersConfig.SecurityHeadersFilter filter = new SecurityHeadersConfig.SecurityHeadersFilter();

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        String csp = response.getHeader("Content-Security-Policy");
        assertNotNull(csp);
        assertTrue(csp.contains("default-src 'self'"));
        assertTrue(csp.contains("script-src 'self' 'unsafe-inline' 'unsafe-eval'"));
        assertTrue(csp.contains("https://accounts.google.com"));
        assertTrue(csp.contains("https://apis.google.com"));
        assertTrue(csp.contains("style-src 'self' 'unsafe-inline'"));
        assertTrue(csp.contains("https://fonts.googleapis.com"));
        assertTrue(csp.contains("font-src 'self' https://fonts.gstatic.com"));
        assertTrue(csp.contains("img-src 'self' data: https:"));
        assertTrue(csp.contains("connect-src 'self'"));
        assertTrue(csp.contains("frame-src 'self'"));
        assertTrue(csp.contains("form-action 'self'"));
        assertTrue(csp.contains("upgrade-insecure-requests"));
    }

    @Test
    @DisplayName("SecurityHeadersFilter debe agregar Permissions Policy")
    void testSecurityHeadersFilterAddsPermissionsPolicy() throws ServletException, IOException {
        SecurityHeadersConfig.SecurityHeadersFilter filter = new SecurityHeadersConfig.SecurityHeadersFilter();

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        String permissionsPolicy = response.getHeader("Permissions-Policy");
        assertNotNull(permissionsPolicy);
        assertTrue(permissionsPolicy.contains("accelerometer=()"));
        assertTrue(permissionsPolicy.contains("camera=()"));
        assertTrue(permissionsPolicy.contains("geolocation=()"));
        assertTrue(permissionsPolicy.contains("gyroscope=()"));
        assertTrue(permissionsPolicy.contains("magnetometer=()"));
        assertTrue(permissionsPolicy.contains("microphone=()"));
        assertTrue(permissionsPolicy.contains("payment=()"));
        assertTrue(permissionsPolicy.contains("usb=()"));
    }

    @Test
    @DisplayName("SecurityHeadersFilter debe agregar headers de cache control para endpoints sensibles")
    void testSecurityHeadersFilterAddsCacheControlForSensitiveEndpoints() throws ServletException, IOException {
        SecurityHeadersConfig.SecurityHeadersFilter filter = new SecurityHeadersConfig.SecurityHeadersFilter();

        // Test para endpoint de login
        MockHttpServletRequest loginRequest = new MockHttpServletRequest();
        loginRequest.setRequestURI("/api/users/login");
        MockHttpServletResponse loginResponse = new MockHttpServletResponse();

        filter.doFilterInternal(loginRequest, loginResponse, filterChain);

        assertEquals("no-cache, no-store, must-revalidate",
                loginResponse.getHeader("Cache-Control"));
        assertEquals("no-cache", loginResponse.getHeader("Pragma"));
        assertEquals("0", loginResponse.getHeader("Expires"));
    }

    @Test
    @DisplayName("SecurityHeadersFilter debe agregar cache control para endpoint de perfil")
    void testSecurityHeadersFilterAddsCacheControlForProfile() throws ServletException, IOException {
        SecurityHeadersConfig.SecurityHeadersFilter filter = new SecurityHeadersConfig.SecurityHeadersFilter();

        MockHttpServletRequest profileRequest = new MockHttpServletRequest();
        profileRequest.setRequestURI("/api/users/profile");
        MockHttpServletResponse profileResponse = new MockHttpServletResponse();

        filter.doFilterInternal(profileRequest, profileResponse, filterChain);

        assertEquals("no-cache, no-store, must-revalidate",
                profileResponse.getHeader("Cache-Control"));
        assertEquals("no-cache", profileResponse.getHeader("Pragma"));
        assertEquals("0", profileResponse.getHeader("Expires"));
    }

    @Test
    @DisplayName("SecurityHeadersFilter debe agregar cache control para endpoints OAuth2")
    void testSecurityHeadersFilterAddsCacheControlForOAuth2() throws ServletException, IOException {
        SecurityHeadersConfig.SecurityHeadersFilter filter = new SecurityHeadersConfig.SecurityHeadersFilter();

        MockHttpServletRequest oauthRequest = new MockHttpServletRequest();
        oauthRequest.setRequestURI("/oauth2/callback");
        MockHttpServletResponse oauthResponse = new MockHttpServletResponse();

        filter.doFilterInternal(oauthRequest, oauthResponse, filterChain);

        assertEquals("no-cache, no-store, must-revalidate",
                oauthResponse.getHeader("Cache-Control"));
        assertEquals("no-cache", oauthResponse.getHeader("Pragma"));
        assertEquals("0", oauthResponse.getHeader("Expires"));
    }

    @Test
    @DisplayName("SecurityHeadersFilter debe agregar cache control para endpoints actuator")
    void testSecurityHeadersFilterAddsCacheControlForActuator() throws ServletException, IOException {
        SecurityHeadersConfig.SecurityHeadersFilter filter = new SecurityHeadersConfig.SecurityHeadersFilter();

        MockHttpServletRequest actuatorRequest = new MockHttpServletRequest();
        actuatorRequest.setRequestURI("/actuator/health");
        MockHttpServletResponse actuatorResponse = new MockHttpServletResponse();

        filter.doFilterInternal(actuatorRequest, actuatorResponse, filterChain);

        assertEquals("no-cache, no-store, must-revalidate",
                actuatorResponse.getHeader("Cache-Control"));
        assertEquals("no-cache", actuatorResponse.getHeader("Pragma"));
        assertEquals("0", actuatorResponse.getHeader("Expires"));
    }

    @Test
    @DisplayName("SecurityHeadersFilter NO debe agregar cache control para endpoints normales")
    void testSecurityHeadersFilterDoesNotAddCacheControlForNormalEndpoints() throws ServletException, IOException {
        SecurityHeadersConfig.SecurityHeadersFilter filter = new SecurityHeadersConfig.SecurityHeadersFilter();

        MockHttpServletRequest normalRequest = new MockHttpServletRequest();
        normalRequest.setRequestURI("/api/events");
        MockHttpServletResponse normalResponse = new MockHttpServletResponse();

        filter.doFilterInternal(normalRequest, normalResponse, filterChain);

        assertNull(normalResponse.getHeader("Cache-Control"));
        assertNull(normalResponse.getHeader("Pragma"));
        assertNull(normalResponse.getHeader("Expires"));
    }

    @Test
    @DisplayName("SecurityHeadersFilter debe llamar al siguiente filtro en la cadena")
    void testSecurityHeadersFilterCallsNextFilter() throws ServletException, IOException {
        SecurityHeadersConfig.SecurityHeadersFilter filter = new SecurityHeadersConfig.SecurityHeadersFilter();

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Clase debe estar anotada como Configuration")
    void testConfigurationAnnotation() {
        assertTrue(SecurityHeadersConfig.class.isAnnotationPresent(
                org.springframework.context.annotation.Configuration.class));
    }

    @Test
    @DisplayName("securityHeadersFilter debe estar anotado como Bean")
    void testBeanAnnotation() throws NoSuchMethodException {
        assertTrue(SecurityHeadersConfig.class
                .getMethod("securityHeadersFilter")
                .isAnnotationPresent(org.springframework.context.annotation.Bean.class));
    }

    @Test
    @DisplayName("SecurityHeadersFilter debe extender OncePerRequestFilter")
    void testSecurityHeadersFilterExtendsOncePerRequestFilter() {
        assertTrue(OncePerRequestFilter.class.isAssignableFrom(
                SecurityHeadersConfig.SecurityHeadersFilter.class));
    }

    @Test
    @DisplayName("SecurityHeadersFilter debe ser clase estática")
    void testSecurityHeadersFilterIsStatic() {
        assertTrue(java.lang.reflect.Modifier.isStatic(
                SecurityHeadersConfig.SecurityHeadersFilter.class.getModifiers()));
    }

    @Test
    @DisplayName("SecurityHeadersFilter debe ser clase pública")
    void testSecurityHeadersFilterIsPublic() {
        assertTrue(java.lang.reflect.Modifier.isPublic(
                SecurityHeadersConfig.SecurityHeadersFilter.class.getModifiers()));
    }

    @Test
    @DisplayName("SecurityHeadersFilter debe manejar requests nulos sin errores")
    void testSecurityHeadersFilterHandlesNullRequestGracefully() throws ServletException, IOException {
        SecurityHeadersConfig.SecurityHeadersFilter filter = new SecurityHeadersConfig.SecurityHeadersFilter();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(null);
        MockHttpServletResponse response = new MockHttpServletResponse();

        // No debería lanzar excepción
        assertDoesNotThrow(() -> {
            filter.doFilterInternal(request, response, filterChain);
        });

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Todos los headers de seguridad deben tener valores no vacíos")
    void testAllSecurityHeadersHaveNonEmptyValues() throws ServletException, IOException {
        SecurityHeadersConfig.SecurityHeadersFilter filter = new SecurityHeadersConfig.SecurityHeadersFilter();

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        // Verificar que todos los headers importantes están presentes y no vacíos
        assertNotNull(response.getHeader("Strict-Transport-Security"));
        assertNotNull(response.getHeader("Content-Security-Policy"));
        assertNotNull(response.getHeader("X-Content-Type-Options"));
        assertNotNull(response.getHeader("X-Frame-Options"));
        assertNotNull(response.getHeader("X-XSS-Protection"));
        assertNotNull(response.getHeader("Referrer-Policy"));
        assertNotNull(response.getHeader("Permissions-Policy"));
        assertNotNull(response.getHeader("Cross-Origin-Embedder-Policy"));
        assertNotNull(response.getHeader("Cross-Origin-Opener-Policy"));
        assertNotNull(response.getHeader("Cross-Origin-Resource-Policy"));

        // Server header debe estar presente (aunque vacío para ocultar información)
        assertTrue(response.getHeaderNames().contains("Server"));
    }

    @Test
    @DisplayName("Multiple llamadas al filtro deben ser consistentes")
    void testMultipleFilterCallsAreConsistent() throws ServletException, IOException {
        SecurityHeadersConfig.SecurityHeadersFilter filter = new SecurityHeadersConfig.SecurityHeadersFilter();

        // Primera llamada
        MockHttpServletRequest request1 = new MockHttpServletRequest();
        MockHttpServletResponse response1 = new MockHttpServletResponse();
        filter.doFilterInternal(request1, response1, filterChain);

        // Segunda llamada
        MockHttpServletRequest request2 = new MockHttpServletRequest();
        MockHttpServletResponse response2 = new MockHttpServletResponse();
        filter.doFilterInternal(request2, response2, filterChain);

        // Los headers deben ser idénticos
        assertEquals(response1.getHeader("Strict-Transport-Security"),
                response2.getHeader("Strict-Transport-Security"));
        assertEquals(response1.getHeader("Content-Security-Policy"),
                response2.getHeader("Content-Security-Policy"));
        assertEquals(response1.getHeader("X-Content-Type-Options"),
                response2.getHeader("X-Content-Type-Options"));
    }
}