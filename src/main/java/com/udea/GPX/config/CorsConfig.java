package com.udea.gpx.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

import com.udea.gpx.constants.AppConstants;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    private static final Logger logger = LoggerFactory.getLogger(CorsConfig.class);

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Value("${cors.allowed-origins:}")
    private List<String> customAllowedOrigins;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {

                boolean isProduction = "prod".equals(activeProfile);
                boolean isTest = "test".equals(activeProfile);

                String[] allowedOrigins = determineAllowedOrigins(isProduction, isTest);
                String[] allowedHeaders = determineAllowedHeaders(isProduction);
                long maxAge = determineMaxAge(isProduction);

                logger.info("🔧 Configurando CORS para perfil: {} (¿Es producción? {})", activeProfile, isProduction);

                registry.addMapping("/**")
                        .allowedOrigins(allowedOrigins)
                        .allowedMethods(AppConstants.Security.ALLOWED_METHODS)
                        .allowedHeaders(allowedHeaders)
                        .exposedHeaders(AppConstants.Security.EXPOSED_HEADERS)
                        .allowCredentials(true)
                        .maxAge(maxAge);

                logger.info("✅ CORS configurado con {} orígenes permitidos para perfil {}",
                        allowedOrigins.length, activeProfile);

                if (logger.isDebugEnabled()) {
                    logger.debug("Orígenes CORS permitidos: {}", Arrays.toString(allowedOrigins));
                }
            }
        };
    }

    /**
     * Determina los orígenes permitidos según el ambiente
     */
    private String[] determineAllowedOrigins(boolean isProduction, boolean isTest) {
        if (isTest) {
            // Para tests: permitir localhost en varios puertos
            return new String[] {
                    "http://localhost:3000",
                    "http://localhost:8080",
                    "http://127.0.0.1:3000",
                    "http://127.0.0.1:8080"
            };
        }

        if (isProduction) {
            // Usar orígenes custom si están definidos, sino usar por defecto
            if (customAllowedOrigins != null && !customAllowedOrigins.isEmpty()) {
                logger.info("🔒 Usando orígenes CORS personalizados para producción");
                return customAllowedOrigins.toArray(new String[0]);
            }
            return AppConstants.Security.PRODUCTION_ORIGINS;
        } else {
            // Desarrollo: más permisivo
            return AppConstants.Security.DEVELOPMENT_ORIGINS;
        }
    }

    /**
     * Determina los headers permitidos según el ambiente
     */
    private String[] determineAllowedHeaders(boolean isProduction) {
        if (isProduction) {
            // Producción: headers específicos por seguridad
            return AppConstants.Security.ALLOWED_HEADERS;
        } else {
            // Desarrollo: permitir todos los headers
            return new String[] { "*" };
        }
    }

    /**
     * Determina el tiempo de cache según el ambiente
     */
    private long determineMaxAge(boolean isProduction) {
        return isProduction ? AppConstants.Security.CORS_MAX_AGE_PRODUCTION
                : AppConstants.Security.CORS_MAX_AGE_DEVELOPMENT;
    }
}