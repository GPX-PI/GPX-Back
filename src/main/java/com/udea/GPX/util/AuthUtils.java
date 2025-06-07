package com.udea.GPX.util;

import com.udea.GPX.model.User;
import com.udea.GPX.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Component;

@Component
public class AuthUtils {

  @Autowired
  private UserService userService;

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
    if (principal instanceof User) {
      return (User) principal;
    }

    // Si es un usuario OAuth2 (Google)
    if (principal instanceof DefaultOidcUser) {
      DefaultOidcUser oidcUser = (DefaultOidcUser) principal;
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
}