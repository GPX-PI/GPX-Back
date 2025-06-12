package com.udea.gpx.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Component;

import com.udea.gpx.model.User;
import com.udea.gpx.service.UserService;

@Component
public class AuthUtils {
  private final UserService userService;

  public AuthUtils(UserService userService) {
    this.userService = userService;
  }

  /**
   * Obtiene el usuario autenticado actual, manejando tanto usuarios OAuth2 como
   * locales
   * 
   * @return User object o null si no hay usuario autenticado
   */
  public User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      return null;
    }

    Object principal = authentication.getPrincipal();

    // Si es un usuario local (JWT)
    if (principal instanceof User user) {
      return user;
    }

    // Si es un usuario OAuth2 (Google)
    if (principal instanceof DefaultOidcUser oidcUser) {
      String email = oidcUser.getEmail();

      if (email != null) {
        return userService.findByEmail(email);
      }
    }

    return null;
  }

  /**
   * Verifica si el usuario actual es administrador
   * 
   * @return true si es admin, false en caso contrario
   */
  public boolean isCurrentUserAdmin() {
    User user = getCurrentUser();
    return user != null && user.isAdmin();
  }

  /**
   * Obtiene el ID del usuario actual
   * 
   * @return Long ID del usuario o null si no hay usuario autenticado
   */
  public Long getCurrentUserId() {
    User user = getCurrentUser();
    return user != null ? user.getId() : null;
  }

  /**
   * Verifica si el usuario actual es el mismo que el userId dado o es admin
   *
   * @param userId ID del usuario a comparar
   * @return true si es el mismo usuario o es admin
   */
  public boolean isCurrentUserOrAdmin(Long userId) {
    if (isCurrentUserAdmin()) {
      return true;
    }
    Long currentUserId = getCurrentUserId();
    return currentUserId != null && currentUserId.equals(userId);
  }
}