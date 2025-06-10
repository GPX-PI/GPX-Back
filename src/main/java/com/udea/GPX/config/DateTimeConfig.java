package com.udea.GPX.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import jakarta.annotation.PostConstruct;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * Configuración para manejo consistente de fechas y timezones
 */
@Configuration
public class DateTimeConfig {

  /**
   * Configurar timezone por defecto de la aplicación
   */
  @PostConstruct
  public void configureTimezone() {
    // Configurar timezone por defecto a UTC para consistencia
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
  }

  /**
   * Configurar ObjectMapper para serialización consistente de fechas
   */
  @Bean
  @Primary
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();

    // Registrar módulo para Java Time API
    JavaTimeModule javaTimeModule = new JavaTimeModule();

    // Configurar formatos específicos para fechas
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    javaTimeModule.addSerializer(java.time.LocalDate.class,
        new LocalDateSerializer(dateFormatter));
    javaTimeModule.addSerializer(java.time.LocalDateTime.class,
        new LocalDateTimeSerializer(dateTimeFormatter));

    mapper.registerModule(javaTimeModule);

    // Deshabilitar timestamps para usar formato ISO
    mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    return mapper;
  }
}