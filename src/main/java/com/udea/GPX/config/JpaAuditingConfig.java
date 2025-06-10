package com.udea.GPX.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Configuración de auditoría JPA para rastrear cambios en entidades
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfig {

  @Bean
  public AuditorAware<String> auditorProvider() {
    return new AuditorAwareImpl();
  }

  public static class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      if (authentication == null || !authentication.isAuthenticated()) {
        return Optional.of("system");
      }

      // Si el principal es un User (autenticación JWT)
      if (authentication.getPrincipal() instanceof com.udea.GPX.model.User) {
        com.udea.GPX.model.User user = (com.udea.GPX.model.User) authentication.getPrincipal();
        return Optional.of(user.getEmail());
      }

      // Si es autenticación OAuth2
      if (authentication.getPrincipal() instanceof org.springframework.security.oauth2.core.user.OAuth2User) {
        org.springframework.security.oauth2.core.user.OAuth2User oauth2User = (org.springframework.security.oauth2.core.user.OAuth2User) authentication
            .getPrincipal();
        String email = oauth2User.getAttribute("email");
        return Optional.ofNullable(email != null ? email : "oauth2-user");
      }

      // Fallback para otros tipos de autenticación
      return Optional.of(authentication.getName());
    }
  }
}