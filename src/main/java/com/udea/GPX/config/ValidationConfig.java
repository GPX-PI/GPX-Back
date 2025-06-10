package com.udea.GPX.config;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 🛡️ CONFIGURACIÓN GLOBAL DE VALIDACIÓN Y SANITIZACIÓN
 * 
 * Activa la validación automática en:
 * - @RequestBody con @Valid
 * - @RequestParam con anotaciones de validación
 * - Métodos de servicio con @Validated
 */
@Configuration
public class ValidationConfig implements WebMvcConfigurer {

  /**
   * Configurar el validador por defecto para Bean Validation (JSR-303)
   * Permite que @Sanitized funcione automáticamente con @Valid
   */
  @Bean
  public Validator validator() {
    return Validation.buildDefaultValidatorFactory().getValidator();
  }

  /**
   * Habilitar validación a nivel de método
   * Permite usar @Validated en clases de servicio
   */
  @Bean
  public MethodValidationPostProcessor methodValidationPostProcessor() {
    return new MethodValidationPostProcessor();
  }
}