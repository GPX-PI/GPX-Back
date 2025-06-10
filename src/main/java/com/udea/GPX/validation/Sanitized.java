package com.udea.GPX.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Anotación para marcar campos que deben ser sanitizados automáticamente
 */
@Documented
@Constraint(validatedBy = SanitizedValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Sanitized {

  String message() default "El campo contiene caracteres no permitidos";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  /**
   * Tipo de sanitización a aplicar
   */
  SanitizationType value() default SanitizationType.TEXT;

  /**
   * Si debe permitir valores nulos
   */
  boolean allowNull() default true;

  enum SanitizationType {
    TEXT, // Sanitización general de texto
    EMAIL, // Sanitización específica para emails
    NAME, // Sanitización para nombres (solo letras, espacios, etc.)
    URL, // Sanitización para URLs
    PHONE, // Sanitización para teléfonos
    IDENTIFICATION // Sanitización para identificaciones
  }
}