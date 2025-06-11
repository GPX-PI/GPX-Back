package com.udea.gpx.service;

import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.udea.gpx.model.User;
import com.udea.gpx.repository.IUserRepository;

import java.util.Optional;

@Service
public class OAuth2Service {

    private final IUserRepository userRepository;

    // Constructor injection (no @Autowired needed)
    public OAuth2Service(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User processOAuth2User(OAuth2User oauth2User) {
        String email = oauth2User.getAttribute("email");
        String googleId = extractGoogleId(oauth2User);
        String name = oauth2User.getAttribute("name");
        String picture = oauth2User.getAttribute("picture");

        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            return updateExistingUser(existingUser.get(), googleId, picture);
        } else {
            return createNewGoogleUser(email, googleId, name, picture);
        }
    }

    private String extractGoogleId(OAuth2User oauth2User) {
        String googleId = oauth2User.getAttribute("sub");
        // Fallback: Si googleId es null, usar 'id' en lugar de 'sub'
        return googleId != null ? googleId : oauth2User.getAttribute("id");
    }

    private User updateExistingUser(User user, String googleId, String picture) {
        // Actualizar información de Google si no tiene googleId
        if (user.getGoogleId() == null) {
            user.setGoogleId(googleId);
            user.setAuthProvider("GOOGLE");
            if (user.getPicture() == null || user.getPicture().isEmpty()) {
                user.setPicture(picture);
            }
            return userRepository.save(user);
        }
        return user;
    }

    private User createNewGoogleUser(String email, String googleId, String name, String picture) {
        User newUser = new User();

        // Datos proporcionados por Google
        newUser.setEmail(email);
        newUser.setGoogleId(googleId);
        newUser.setAuthProvider("GOOGLE");
        newUser.setPicture(picture != null ? picture : "");
        newUser.setPassword(null); // Explícitamente null para OAuth2

        setUserNames(newUser, name);

        // Configuración por defecto
        newUser.setAdmin(false);

        return userRepository.save(newUser);
    }

    private void setUserNames(User user, String fullName) {
        if (fullName != null && !fullName.trim().isEmpty()) {
            String[] nameParts = fullName.trim().split("\\s+", 2);

            // Truncar firstName si es muy largo (máximo 50 caracteres)
            String firstName = nameParts[0];
            if (firstName.length() > 50) {
                firstName = firstName.substring(0, 50);
            }
            user.setFirstName(firstName);

            // Procesar lastName si existe
            if (nameParts.length > 1) {
                String lastName = nameParts[1];
                if (lastName.length() > 50) {
                    lastName = lastName.substring(0, 50);
                }
                user.setLastName(lastName);
            }
        } else {
            // Usar valores por defecto si no hay nombre
            user.setFirstName("Usuario");
            user.setLastName("Google");
        }
    }

    public User findUserByGoogleId(String googleId) {
        return userRepository.findByGoogleId(googleId).orElse(null);
    }

    /**
     * Verifica si el perfil del usuario está completo
     * Un perfil completo requiere: identification, phone, role
     */
    public boolean isProfileComplete(User user) {
        return user.getIdentification() != null && !user.getIdentification().trim().isEmpty() &&
                user.getPhone() != null && !user.getPhone().trim().isEmpty() &&
                user.getRole() != null && !user.getRole().trim().isEmpty();
    }

    /**
     * Obtiene la información que falta en el perfil
     */
    public String[] getMissingProfileFields(User user) {
        java.util.List<String> missingFields = new java.util.ArrayList<>();

        if (user.getIdentification() == null || user.getIdentification().trim().isEmpty()) {
            missingFields.add("identification");
        }
        if (user.getPhone() == null || user.getPhone().trim().isEmpty()) {
            missingFields.add("phone");
        }
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            missingFields.add("role");
        }

        return missingFields.toArray(new String[0]);
    }
}