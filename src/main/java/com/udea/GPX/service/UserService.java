package com.udea.gpx.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.udea.gpx.model.User;
import com.udea.gpx.repository.IUserRepository;
import com.udea.gpx.util.InputSanitizer;
import com.udea.gpx.constants.AppConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final IUserRepository userRepository;
    private final PasswordService passwordService;

    // Constructor injection (no @Autowired needed)
    public UserService(IUserRepository userRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

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

    public User createUser(User user) { // Validar email duplicado
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            String sanitizedEmail = InputSanitizer.sanitizeEmail(user.getEmail());
            User existingUser = findByEmail(sanitizedEmail);
            if (existingUser != null) {
                throw new IllegalArgumentException(AppConstants.Messages.EMAIL_YA_EN_USO);
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
                .orElseThrow(() -> new RuntimeException(AppConstants.Messages.USUARIO_NO_ENCONTRADO));

        updateEmailIfProvided(user, updatedUser.getEmail(), id);
        updateBasicFields(user, updatedUser);
        updateContactFields(user, updatedUser);
        updateMedicalFields(user, updatedUser);
        updateSocialFields(user, updatedUser);
        updateFileFields(user, updatedUser);

        return userRepository.save(user);
    }

    private void updateEmailIfProvided(User user, String newEmail, Long userId) {
        if (newEmail != null && !newEmail.trim().isEmpty()) {
            String sanitizedEmail = InputSanitizer.sanitizeEmail(newEmail);
            User existingUser = findByEmail(sanitizedEmail);
            if (existingUser != null && !existingUser.getId().equals(userId)) {
                throw new IllegalArgumentException(AppConstants.Messages.EMAIL_YA_EN_USO_POR_OTRO_USUARIO);
            }
            user.setEmail(sanitizedEmail);
        }
    }

    private void updateBasicFields(User user, User updatedUser) {
        if (updatedUser.getFirstName() != null) {
            user.setFirstName(InputSanitizer.sanitizeName(updatedUser.getFirstName()));
        }
        if (updatedUser.getLastName() != null) {
            user.setLastName(InputSanitizer.sanitizeName(updatedUser.getLastName()));
        }
        if (updatedUser.getIdentification() != null) {
            user.setIdentification(InputSanitizer.sanitizeIdentification(updatedUser.getIdentification()));
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
    }

    private void updateContactFields(User user, User updatedUser) {
        if (updatedUser.getPhone() != null) {
            user.setPhone(InputSanitizer.sanitizePhone(updatedUser.getPhone()));
        }
        if (updatedUser.getEmergencyPhone() != null) {
            user.setEmergencyPhone(InputSanitizer.sanitizePhone(updatedUser.getEmergencyPhone()));
        }
    }

    private void updateMedicalFields(User user, User updatedUser) {
        if (updatedUser.getEps() != null) {
            user.setEps(InputSanitizer.sanitizeText(updatedUser.getEps()));
        }
        if (updatedUser.getRh() != null) {
            user.setRh(InputSanitizer.sanitizeText(updatedUser.getRh()));
        }
        if (updatedUser.getAlergies() != null) {
            user.setAlergies(InputSanitizer.sanitizeText(updatedUser.getAlergies()));
        }
    }

    private void updateSocialFields(User user, User updatedUser) {
        if (updatedUser.getWikiloc() != null) {
            user.setWikiloc(InputSanitizer.sanitizeSocialField(updatedUser.getWikiloc()));
        }
        if (updatedUser.getTerrapirata() != null) {
            user.setTerrapirata(InputSanitizer.sanitizeSocialField(updatedUser.getTerrapirata()));
        }
        if (updatedUser.getInstagram() != null) {
            user.setInstagram(InputSanitizer.sanitizeSocialField(updatedUser.getInstagram()));
        }
        if (updatedUser.getFacebook() != null) {
            user.setFacebook(InputSanitizer.sanitizeSocialField(updatedUser.getFacebook()));
        }
    }

    private void updateFileFields(User user, User updatedUser) {
        // Para archivos, siempre actualizar (incluye null para eliminar)
        if (updatedUser.getInsurance() != null) {
            user.setInsurance(updatedUser.getInsurance()); // Archivos no se sanitizan, permite null
        }
        if (updatedUser.getPicture() != null) {
            user.setPicture(updatedUser.getPicture()); // Archivos no se sanitizan, permite null
        }
    }

    public User updateUserProfile(Long id, User updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(AppConstants.Messages.USUARIO_NO_ENCONTRADO));

        updateEmailForProfile(user, updatedUser.getEmail(), id);
        updateBasicFields(user, updatedUser);
        updateContactFields(user, updatedUser);
        updateMedicalFields(user, updatedUser);
        updateSocialFields(user, updatedUser);

        // NOTA: NO actualizar picture ni insurance aquí
        // Esos se manejan en endpoints específicos

        return userRepository.save(user);
    }

    private void updateEmailForProfile(User user, String newEmail, Long userId) {
        if (newEmail != null && !newEmail.trim().isEmpty()) {
            String sanitizedEmail = InputSanitizer.sanitizeEmail(newEmail);
            User existingUser = findByEmail(sanitizedEmail);
            if (existingUser != null && !existingUser.getId().equals(userId)) {
                throw new IllegalArgumentException(AppConstants.Messages.EMAIL_YA_EN_USO_POR_OTRO_USUARIO);
            }
            user.setEmail(sanitizedEmail);
        }
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
                    logger.warn("Error al actualizar authProvider para usuario por email: {}", e.getMessage());
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
                .orElseThrow(() -> new RuntimeException(AppConstants.Messages.USUARIO_NO_ENCONTRADO));

        // Verificar contraseña actual
        if (!checkPassword(user, currentPassword)) {
            throw new IllegalArgumentException(AppConstants.Messages.PASSWORD_ACTUAL_INCORRECTA);
        }

        // Validar nueva contraseña
        if (!passwordService.isPasswordValid(newPassword)) {
            throw new IllegalArgumentException(passwordService.getPasswordValidationMessage(newPassword));
        }

        // Verificar que la nueva contraseña sea diferente
        if (passwordService.verifyPassword(newPassword, user.getPassword())) {
            throw new IllegalArgumentException(AppConstants.Messages.PASSWORD_NUEVA_DEBE_SER_DIFERENTE);
        }

        // Hashear y guardar nueva contraseña
        user.setPassword(passwordService.hashPassword(newPassword));
        userRepository.save(user);
    }

    // ========== MÉTODOS PARA GESTIÓN DE URLs DE ARCHIVOS (igual que EventService)
    // ==========

    /**
     * Actualiza la URL de la foto de perfil del usuario
     * NO aplica sanitización - igual que EventService.updateEventPictureUrl()
     */
    public User updateUserPictureUrl(Long id, String pictureUrl) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(AppConstants.Messages.USUARIO_NO_ENCONTRADO));

        // Validaciones de seguridad adicionales
        if (pictureUrl != null && !pictureUrl.trim().isEmpty()) {
            String trimmedUrl = pictureUrl.trim();

            // Límite de longitud para prevenir ataques
            if (trimmedUrl.length() > 2048) {
                throw new IllegalArgumentException("URL demasiado larga (máximo 2048 caracteres)");
            } // Log para auditoría de seguridad - sin exponer la URL completa
            logger.info("Actualizando URL de imagen del usuario {} (longitud: {} caracteres)", id, trimmedUrl.length());
            user.setPicture(trimmedUrl);
        } else {
            user.setPicture(null);
            logger.info("Eliminando imagen del usuario {}", id);
        }

        return userRepository.save(user);
    }

    /**
     * Actualiza la URL del documento de seguro del usuario
     * NO aplica sanitización - consistente con updateUserPictureUrl()
     */
    public User updateUserInsuranceUrl(Long id, String insuranceUrl) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(AppConstants.Messages.USUARIO_NO_ENCONTRADO));

        // Validaciones de seguridad adicionales
        if (insuranceUrl != null && !insuranceUrl.trim().isEmpty()) {
            String trimmedUrl = insuranceUrl.trim();

            // Límite de longitud para prevenir ataques
            if (trimmedUrl.length() > 2048) {
                throw new IllegalArgumentException("URL demasiado larga (máximo 2048 caracteres)");
            } // Log para auditoría de seguridad - sin exponer la URL completa
            logger.info("Actualizando URL de seguro del usuario {} (longitud: {} caracteres)", id, trimmedUrl.length());
            user.setInsurance(trimmedUrl);
        } else {
            user.setInsurance(null);
            logger.info("Eliminando seguro del usuario {}", id);
        }

        return userRepository.save(user);
    }

    /**
     * Elimina el documento de seguro del usuario
     * Método específico para eliminar el seguro - más directo y confiable
     */
    public User removeUserInsurance(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(AppConstants.Messages.USUARIO_NO_ENCONTRADO));

        logger.info("Eliminando documento de seguro del usuario {}", id);
        user.setInsurance(null);
        return userRepository.save(user);
    }

}
