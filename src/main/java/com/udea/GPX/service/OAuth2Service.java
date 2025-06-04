package com.udea.GPX.service;

import com.udea.GPX.model.User;
import com.udea.GPX.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OAuth2Service {

    @Autowired
    private IUserRepository userRepository;

    public User processOAuth2User(OAuth2User oauth2User) {
        String email = oauth2User.getAttribute("email");
        String googleId = oauth2User.getAttribute("sub");
        String name = oauth2User.getAttribute("name");
        String picture = oauth2User.getAttribute("picture");

        // Fallback: Si googleId es null, usar 'id' en lugar de 'sub'
        if (googleId == null) {
            googleId = oauth2User.getAttribute("id");
        }

        // Buscar usuario existente por email o googleId
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
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
        } else {
            // Crear nuevo usuario con datos mínimos de Google
            User newUser = new User();

            // Datos proporcionados por Google
            newUser.setEmail(email);
            newUser.setGoogleId(googleId);
            newUser.setAuthProvider("GOOGLE");
            newUser.setPicture(picture != null ? picture : "");
            newUser.setPassword(null); // Explícitamente null para OAuth2

            // Procesar nombre: intentar separar firstName y lastName
            if (name != null && !name.trim().isEmpty()) {
                String[] nameParts = name.trim().split("\\s+", 2);

                // Truncar firstName si es muy largo (máximo 50 caracteres)
                String firstName = nameParts[0];
                if (firstName.length() > 50) {
                    firstName = firstName.substring(0, 50);
                }
                newUser.setFirstName(firstName);

                // Procesar lastName si existe
                if (nameParts.length > 1) {
                    String lastName = nameParts[1];
                    if (lastName.length() > 50) {
                        lastName = lastName.substring(0, 50);
                    }
                    newUser.setLastName(lastName);
                }
            } else {
                // Usar valores por defecto si no hay nombre
                newUser.setFirstName("Usuario");
                newUser.setLastName("Google");
            }

            // Configuración por defecto
            newUser.setAdmin(false);

            // Los demás campos se dejan null o con valores por defecto
            // El usuario podrá completar su perfil después del registro

            return userRepository.save(newUser);
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