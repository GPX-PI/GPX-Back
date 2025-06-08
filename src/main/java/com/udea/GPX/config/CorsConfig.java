package com.udea.GPX.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${cors.allowed-origins:http://localhost:3000}")
    private String allowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Configurar orígenes permitidos - limpiar espacios y caracteres especiales
        String cleanedOrigins = allowedOrigins.replaceAll("@", "").trim();
        System.out.println("🌐 CORS - Orígenes configurados: " + cleanedOrigins);

        if (cleanedOrigins.equals("*")) {
            configuration.setAllowedOriginPatterns(List.of("*"));
        } else {
            List<String> origins = Arrays.asList(cleanedOrigins.split(","));
            // Limpiar cada origen individualmente
            List<String> cleanOrigins = origins.stream()
                    .map(String::trim)
                    .map(origin -> origin.replaceAll("@", ""))
                    .toList();
            configuration.setAllowedOrigins(cleanOrigins);
            System.out.println("🌐 CORS - Orígenes limpiados: " + cleanOrigins);
        }

        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));

        // Headers permitidos
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization", "Content-Type", "X-Requested-With", "accept",
                "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"));

        // Headers expuestos
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization", "Content-Type"));

        // Permitir credenciales
        configuration.setAllowCredentials(true);

        // Tiempo de cache para preflight
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}