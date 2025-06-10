package com.udea.GPX.service;

import com.udea.GPX.model.User;
import com.udea.GPX.repository.IUserRepository;
import com.udea.GPX.util.InputSanitizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private PasswordService passwordService;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Optional<User> getUserById(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Asegurar que authProvider tenga un valor por defecto para usuarios existentes
            if (user.getAuthProvider() == null || user.getAuthProvider().trim().isEmpty()) {
                user.setAuthProvider("LOCAL");
                // Guardar la actualización
                try {
                    userRepository.save(user);
                } catch (Exception e) {
                    logger.warn("Error al actualizar authProvider para usuario {}: {}", id, e.getMessage());
                }
            }
        }
        return userOpt;
    }

    public User createUser(User user) {
        // Validar email duplicado
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            String sanitizedEmail = InputSanitizer.sanitizeEmail(user.getEmail());
            User existingUser = findByEmail(sanitizedEmail);
            if (existingUser != null) {
                throw new IllegalArgumentException("El email ya está en uso");
            }
            user.setEmail(sanitizedEmail);
        }

        // Validar y hashear contraseña si está presente
        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
            if (!passwordService.isPasswordValid(user.getPassword())) {
                throw new IllegalArgumentException(passwordService.getPasswordValidationMessage(user.getPassword()));
            }
            user.setPassword(passwordService.hashPassword(user.getPassword()));
        }

        // Sanitizar otros campos
        if (user.getFirstName() != null) {
            user.setFirstName(InputSanitizer.sanitizeName(user.getFirstName()));
        }
        if (user.getLastName() != null) {
            user.setLastName(InputSanitizer.sanitizeName(user.getLastName()));
        }
        if (user.getIdentification() != null) {
            user.setIdentification(InputSanitizer.sanitizeIdentification(user.getIdentification()));
        }
        if (user.getPhone() != null) {
            user.setPhone(InputSanitizer.sanitizePhone(user.getPhone()));
        }

        return userRepository.save(user);
    }

    public User updateUser(Long id, User updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validar email duplicado si se está actualizando
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().trim().isEmpty()) {
            String newEmail = InputSanitizer.sanitizeEmail(updatedUser.getEmail());

            // Verificar que el email no esté ya en uso por otro usuario
            User existingUser = findByEmail(newEmail);
            if (existingUser != null && !existingUser.getId().equals(id)) {
                throw new IllegalArgumentException("El email ya está en uso por otro usuario");
            }

            user.setEmail(newEmail);
        }

        // Solo actualizar campos que no sean nulos o que específicamente se quieran
        // actualizar - Con sanitización
        if (updatedUser.getFirstName() != null) {
            user.setFirstName(InputSanitizer.sanitizeName(updatedUser.getFirstName()));
        }
        if (updatedUser.getLastName() != null) {
            user.setLastName(InputSanitizer.sanitizeName(updatedUser.getLastName()));
        }
        if (updatedUser.getIdentification() != null) {
            user.setIdentification(InputSanitizer.sanitizeIdentification(updatedUser.getIdentification()));
        }
        if (updatedUser.getPhone() != null) {
            user.setPhone(InputSanitizer.sanitizePhone(updatedUser.getPhone()));
        }
        if (updatedUser.getRole() != null) {
            user.setRole(InputSanitizer.sanitizeText(updatedUser.getRole()));
        }
        if (updatedUser.getBirthdate() != null) {
            user.setBirthdate(updatedUser.getBirthdate());
        }
        if (updatedUser.getTypeOfId() != null) {
            user.setTypeOfId(InputSanitizer.sanitizeText(updatedUser.getTypeOfId()));
        }
        if (updatedUser.getTeamName() != null) {
            user.setTeamName(InputSanitizer.sanitizeText(updatedUser.getTeamName()));
        }
        if (updatedUser.getEps() != null) {
            user.setEps(InputSanitizer.sanitizeText(updatedUser.getEps()));
        }
        if (updatedUser.getRh() != null) {
            user.setRh(InputSanitizer.sanitizeText(updatedUser.getRh()));
        }
        if (updatedUser.getEmergencyPhone() != null) {
            user.setEmergencyPhone(InputSanitizer.sanitizePhone(updatedUser.getEmergencyPhone()));
        }
        if (updatedUser.getAlergies() != null) {
            user.setAlergies(InputSanitizer.sanitizeText(updatedUser.getAlergies()));
        }
        if (updatedUser.getWikiloc() != null) {
            user.setWikiloc(InputSanitizer.sanitizeUrl(updatedUser.getWikiloc()));
        }
        if (updatedUser.getInsurance() != null) {
            user.setInsurance(updatedUser.getInsurance()); // Archivos no se sanitizan
        }
        if (updatedUser.getTerrapirata() != null) {
            user.setTerrapirata(InputSanitizer.sanitizeUrl(updatedUser.getTerrapirata()));
        }
        if (updatedUser.getInstagram() != null) {
            user.setInstagram(InputSanitizer.sanitizeUrl(updatedUser.getInstagram()));
        }
        if (updatedUser.getFacebook() != null) {
            user.setFacebook(InputSanitizer.sanitizeUrl(updatedUser.getFacebook()));
        }
        if (updatedUser.getPicture() != null) {
            user.setPicture(updatedUser.getPicture()); // Archivos no se sanitizan
        }

        // Para el campo admin, manejarlo de manera especial porque es un primitivo
        // boolean
        // Solo actualizarlo si explícitamente se está enviando
        // Por ahora, mantenemos el valor existente a menos que específicamente se
        // quiera cambiar
        // user.setAdmin(updatedUser.isAdmin()); // Comentado para evitar sobrescritura
        // accidental

        return userRepository.save(user);
    }

    public User updateUserProfile(Long id, User updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validar email duplicado si se está actualizando
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().trim().isEmpty()) {
            String newEmail = InputSanitizer.sanitizeEmail(updatedUser.getEmail());

            // Verificar que el email no esté ya en uso por otro usuario
            User existingUser = findByEmail(newEmail);
            if (existingUser != null && !existingUser.getId().equals(id)) {
                throw new IllegalArgumentException("El email ya está en uso por otro usuario");
            }

            user.setEmail(newEmail);
        }

        // Solo actualizar campos de datos, NO tocar campos de archivos (picture,
        // insurance) - Con sanitización
        if (updatedUser.getFirstName() != null) {
            user.setFirstName(InputSanitizer.sanitizeName(updatedUser.getFirstName()));
        }
        if (updatedUser.getLastName() != null) {
            user.setLastName(InputSanitizer.sanitizeName(updatedUser.getLastName()));
        }
        if (updatedUser.getIdentification() != null) {
            user.setIdentification(InputSanitizer.sanitizeIdentification(updatedUser.getIdentification()));
        }
        if (updatedUser.getPhone() != null) {
            user.setPhone(InputSanitizer.sanitizePhone(updatedUser.getPhone()));
        }
        if (updatedUser.getRole() != null) {
            user.setRole(InputSanitizer.sanitizeText(updatedUser.getRole()));
        }
        if (updatedUser.getBirthdate() != null) {
            user.setBirthdate(updatedUser.getBirthdate());
        }
        if (updatedUser.getTypeOfId() != null) {
            user.setTypeOfId(InputSanitizer.sanitizeText(updatedUser.getTypeOfId()));
        }
        if (updatedUser.getTeamName() != null) {
            user.setTeamName(InputSanitizer.sanitizeText(updatedUser.getTeamName()));
        }
        if (updatedUser.getEps() != null) {
            user.setEps(InputSanitizer.sanitizeText(updatedUser.getEps()));
        }
        if (updatedUser.getRh() != null) {
            user.setRh(InputSanitizer.sanitizeText(updatedUser.getRh()));
        }
        if (updatedUser.getEmergencyPhone() != null) {
            user.setEmergencyPhone(InputSanitizer.sanitizePhone(updatedUser.getEmergencyPhone()));
        }
        if (updatedUser.getAlergies() != null) {
            user.setAlergies(InputSanitizer.sanitizeText(updatedUser.getAlergies()));
        }
        if (updatedUser.getWikiloc() != null) {
            user.setWikiloc(InputSanitizer.sanitizeUrl(updatedUser.getWikiloc()));
        }
        if (updatedUser.getTerrapirata() != null) {
            user.setTerrapirata(InputSanitizer.sanitizeUrl(updatedUser.getTerrapirata()));
        }
        if (updatedUser.getInstagram() != null) {
            user.setInstagram(InputSanitizer.sanitizeUrl(updatedUser.getInstagram()));
        }
        if (updatedUser.getFacebook() != null) {
            user.setFacebook(InputSanitizer.sanitizeUrl(updatedUser.getFacebook()));
        }

        // NOTA: NO actualizar picture ni insurance aquí
        // Esos se manejan en endpoints específicos

        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Asegurar que authProvider tenga un valor por defecto para usuarios existentes
            if (user.getAuthProvider() == null || user.getAuthProvider().trim().isEmpty()) {
                user.setAuthProvider("LOCAL");
                // Guardar la actualización
                try {
                    userRepository.save(user);
                } catch (Exception e) {
                    logger.warn("Error al actualizar authProvider para email {}: {}", email, e.getMessage());
                }
            }
            return user;
        }
        return null;
    }

    public boolean checkPassword(User user, String rawPassword) {
        if (user.getPassword() == null || rawPassword == null) {
            return false;
        }

        // Verificar si la contraseña ya está hasheada
        if (passwordService.isBCryptHash(user.getPassword())) {
            // Usar verificación BCrypt
            return passwordService.verifyPassword(rawPassword, user.getPassword());
        } else {
            // Para compatibilidad con contraseñas existentes en texto plano
            // TEMPORAL: en producción se debe migrar todas las contraseñas
            boolean isValid = user.getPassword().equals(rawPassword);

            // Si la verificación es exitosa, aprovechar para hashear la contraseña
            if (isValid) {
                try {
                    user.setPassword(passwordService.hashPassword(rawPassword));
                    userRepository.save(user);
                    logger.debug("Contraseña actualizada a BCrypt para usuario: {}", user.getId());
                } catch (Exception e) {
                    logger.error("Error al actualizar hash de contraseña para usuario {}: {}", user.getId(),
                            e.getMessage(), e);
                }
            }

            return isValid;
        }
    }

    /**
     * Cambia la contraseña del usuario
     */
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar contraseña actual
        if (!checkPassword(user, currentPassword)) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }

        // Validar nueva contraseña
        if (!passwordService.isPasswordValid(newPassword)) {
            throw new IllegalArgumentException(passwordService.getPasswordValidationMessage(newPassword));
        }

        // Verificar que la nueva contraseña sea diferente
        if (passwordService.verifyPassword(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("La nueva contraseña debe ser diferente a la actual");
        }

        // Hashear y guardar nueva contraseña
        user.setPassword(passwordService.hashPassword(newPassword));
        userRepository.save(user);
    }

}
