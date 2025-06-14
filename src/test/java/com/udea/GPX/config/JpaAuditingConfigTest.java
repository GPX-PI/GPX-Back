package com.udea.gpx.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.domain.AuditorAware;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import com.udea.gpx.model.User;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JpaAuditingConfig Tests")
class JpaAuditingConfigTest {

    private JpaAuditingConfig jpaAuditingConfig;

    @BeforeEach
    void setUp() {
        jpaAuditingConfig = new JpaAuditingConfig();
    }

    @Test
    @DisplayName("JpaAuditingConfig debe estar anotado como Configuration")
    void testJpaAuditingConfigIsConfiguration() {
        assertTrue(JpaAuditingConfig.class.isAnnotationPresent(
                org.springframework.context.annotation.Configuration.class));
    }

    @Test
    @DisplayName("JpaAuditingConfig debe estar anotado como EnableJpaAuditing")
    void testJpaAuditingConfigIsEnableJpaAuditing() {
        assertTrue(JpaAuditingConfig.class.isAnnotationPresent(EnableJpaAuditing.class));
    }

    @Test
    @DisplayName("JpaAuditingConfig debe poder ser instanciado")
    void testJpaAuditingConfigCanBeInstantiated() {
        assertNotNull(jpaAuditingConfig);
    }

    @Test
    @DisplayName("auditorProvider debe retornar una instancia de AuditorAwareImpl")
    void auditorProvider_shouldReturnAuditorAwareInstance() {
        AuditorAware<String> auditorAware = jpaAuditingConfig.auditorProvider();
        assertNotNull(auditorAware);
        assertTrue(auditorAware instanceof JpaAuditingConfig.AuditorAwareImpl);
    }

    @Test
    @DisplayName("AuditorAwareImpl debe retornar 'system' si no hay autenticación")
    void auditorAwareImpl_shouldReturnSystemIfNoAuthentication() {
        JpaAuditingConfig.AuditorAwareImpl auditorAware = new JpaAuditingConfig.AuditorAwareImpl();
        // Simular contexto sin autenticación
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
        Optional<String> auditor = auditorAware.getCurrentAuditor();
        assertTrue(auditor.isPresent());
        assertEquals("system", auditor.get());
    }

    @Test
    @DisplayName("AuditorAwareImpl debe retornar el email del usuario JWT autenticado")
    void auditorAwareImpl_shouldReturnUserEmailIfJwtUser() {
        // Mock de User autenticado
        User user = new User();
        user.setEmail("jwtuser@test.com");
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        JpaAuditingConfig.AuditorAwareImpl auditorAware = new JpaAuditingConfig.AuditorAwareImpl();
        Optional<String> auditor = auditorAware.getCurrentAuditor();
        assertTrue(auditor.isPresent());
        assertEquals("jwtuser@test.com", auditor.get());
    }

    @Test
    @DisplayName("AuditorAwareImpl debe retornar el email del usuario OAuth2 autenticado")
    void auditorAwareImpl_shouldReturnOAuth2Email() {
        OAuth2User oauth2User = mock(OAuth2User.class);
        when(oauth2User.getAttribute("email")).thenReturn("oauth2user@test.com");
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(oauth2User);
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        JpaAuditingConfig.AuditorAwareImpl auditorAware = new JpaAuditingConfig.AuditorAwareImpl();
        Optional<String> auditor = auditorAware.getCurrentAuditor();
        assertTrue(auditor.isPresent());
        assertEquals("oauth2user@test.com", auditor.get());
    }

    @Test
    @DisplayName("AuditorAwareImpl debe retornar el nombre del principal como fallback")
    void auditorAwareImpl_shouldReturnPrincipalNameAsFallback() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("otroPrincipal");
        when(authentication.getName()).thenReturn("principalName");
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        JpaAuditingConfig.AuditorAwareImpl auditorAware = new JpaAuditingConfig.AuditorAwareImpl();
        Optional<String> auditor = auditorAware.getCurrentAuditor();
        assertTrue(auditor.isPresent());
        assertEquals("principalName", auditor.get());
    }
}