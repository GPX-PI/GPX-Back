package com.udea.gpx.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import com.udea.gpx.JwtUtil;
import com.udea.gpx.model.User;
import com.udea.gpx.service.OAuth2Service;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/oauth2")
public class OAuth2Controller {

    // Constants for OAuth2 attributes
    private static final String ATTR_EMAIL = "email";
    private static final String ATTR_PICTURE = "picture";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_SUB = "sub";

    // Constants for response keys
    private static final String RESPONSE_EMAIL = "email";

    private final OAuth2Service oauth2Service;
    private final JwtUtil jwtUtil;
    private final String frontendRedirectUrl;

    public OAuth2Controller(OAuth2Service oauth2Service, JwtUtil jwtUtil,
            @Value("${app.oauth2.frontend-redirect-url:https://gpx-back.onrender.com/login/oauth2-redirect}") String frontendRedirectUrl) {
        this.oauth2Service = oauth2Service;
        this.jwtUtil = jwtUtil;
        this.frontendRedirectUrl = frontendRedirectUrl;
    }

    @GetMapping("/success")
    public RedirectView oauth2LoginSuccess(@AuthenticationPrincipal OAuth2User oauth2User) {
        try {
            // Verificar si oauth2User es null
            if (oauth2User == null) {
                String errorUrl = frontendRedirectUrl + "?error=oauth2_failed&message=" +
                        "Credenciales de Google no configuradas. Revisa GOOGLE_CLIENT_ID y GOOGLE_CLIENT_SECRET";
                return new RedirectView(errorUrl);
            }

            // Procesar usuario OAuth2
            User user = oauth2Service.processOAuth2User(oauth2User);

            // Generar JWT token
            String token = jwtUtil.generateToken(user.getId(), user.isAdmin());

            // Verificar si el perfil está completo
            boolean profileComplete = oauth2Service.isProfileComplete(user);

            // Redireccionar al frontend con el token y estado del perfil
            String firstName = (user.getFirstName() != null ? user.getFirstName() : "");

            // Limpiar parámetros para evitar caracteres inválidos
            firstName = firstName.replaceAll("[\\r\\n\\t]", "").trim();

            // Obtener la foto de perfil (puede ser null)
            String picture = (user.getPicture() != null ? user.getPicture() : "");

            String redirectUrl = frontendRedirectUrl +
                    "?token=" + token +
                    "&userId=" + user.getId() +
                    "&admin=" + user.isAdmin() +
                    "&provider=google" +
                    "&profileComplete=" + profileComplete +
                    "&firstName=" + java.net.URLEncoder.encode(firstName, "UTF-8") +
                    "&picture=" + java.net.URLEncoder.encode(picture, "UTF-8");

            return new RedirectView(redirectUrl);
        } catch (Exception e) {
            // En caso de error, redireccionar con mensaje de error
            String errorUrl = frontendRedirectUrl + "?error=oauth2_failed&message=" + e.getMessage();
            return new RedirectView(errorUrl);
        }
    }

    @GetMapping("/user-info")
    public ResponseEntity<Map<String, Object>> getCurrentUserInfo(@AuthenticationPrincipal OAuth2User oauth2User) {
        if (oauth2User == null) {
            return ResponseEntity.badRequest().build();
        }
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put(ATTR_NAME, oauth2User.getAttribute(ATTR_NAME));
        userInfo.put(ATTR_EMAIL, oauth2User.getAttribute(ATTR_EMAIL));
        userInfo.put(ATTR_PICTURE, oauth2User.getAttribute(ATTR_PICTURE));
        userInfo.put(ATTR_SUB, oauth2User.getAttribute(ATTR_SUB));

        return ResponseEntity.ok(userInfo);
    }

    @GetMapping("/profile-status")
    public ResponseEntity<Map<String, Object>> getProfileStatus(@AuthenticationPrincipal OAuth2User oauth2User) {
        if (oauth2User == null) {
            return ResponseEntity.badRequest().build();
        }

        // Procesar usuario OAuth2 para obtener datos actualizados
        User user = oauth2Service.processOAuth2User(oauth2User);

        Map<String, Object> response = new HashMap<>();
        response.put("isComplete", oauth2Service.isProfileComplete(user));
        response.put("missingFields", oauth2Service.getMissingProfileFields(user));
        response.put("userId", user.getId());
        response.put("firstName", user.getFirstName());
        response.put("lastName", user.getLastName());
        response.put(RESPONSE_EMAIL, user.getEmail());
        response.put(ATTR_PICTURE, user.getPicture());

        return ResponseEntity.ok(response);
    }
}