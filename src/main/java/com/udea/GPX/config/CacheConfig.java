package com.udea.GPX.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de cache para mejorar el rendimiento de consultas frecuentes
 */
@Configuration
@EnableCaching
public class CacheConfig {

  @Bean
  public CacheManager cacheManager() {
    return new ConcurrentMapCacheManager(
        "events", // Cache para eventos
        "categories", // Cache para categorías
        "classifications", // Cache para clasificaciones
        "currentEvents", // Cache para eventos actuales
        "pastEvents", // Cache para eventos pasados
        "stageResults", // Cache para resultados de etapas
        "eventCategories" // Cache para categorías por evento
    );
  }
}