package com.udea.gpx.validation;

import com.udea.gpx.util.InputSanitizer;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validador que aplica sanitización automática usando InputSanitizer
 */
public class SanitizedValidator implements ConstraintValidator<Sanitized, String> {

  private Sanitized.SanitizationType sanitizationType;
  private boolean allowNull;

  @Override
  public void initialize(Sanitized constraintAnnotation) {
    if (constraintAnnotation == null) {
      // Default values if annotation is null
      this.sanitizationType = Sanitized.SanitizationType.TEXT;
      this.allowNull = false;
    } else {
      this.sanitizationType = constraintAnnotation.value();
      this.allowNull = constraintAnnotation.allowNull();
    }
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    // Si es null y se permite, es válido
    if (value == null) {
      return allowNull;
    }

    try {
      // Aplicar sanitización según el tipo
      switch (sanitizationType) {
        case EMAIL:
          InputSanitizer.sanitizeEmail(value);
          break;
        case NAME:
          InputSanitizer.sanitizeName(value);
          break;
        case URL:
          InputSanitizer.sanitizeUrl(value);
          break;
        case PHONE:
          InputSanitizer.sanitizePhone(value);
          break;
        case IDENTIFICATION:
          InputSanitizer.sanitizeIdentification(value);
          break;
        case TEXT:
        default:
          InputSanitizer.sanitizeText(value);
          break;
      } // Si la sanitización no lanza excepción, el valor es válido
      return true;

    } catch (IllegalArgumentException e) {
      // Personalizar el mensaje de error
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate(e.getMessage())
          .addConstraintViolation();
      return false;
    } catch (Exception e) {
      // Manejar cualquier otra excepción inesperada
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate(e.getMessage())
          .addConstraintViolation();
      return false;
    }
  }
}