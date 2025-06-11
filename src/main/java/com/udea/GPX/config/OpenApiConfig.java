package com.udea.gpx.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * Configuración avanzada de OpenAPI 3 para documentación completa de la API
 * REST
 * del sistema gpx Racing Event Management
 * 
 * Documentación disponible en: /swagger-ui/index.html
 * OpenAPI JSON: /v3/api-docs
 */
@Configuration
public class OpenApiConfig {

        @Value("${app.api.version:1.0.0}")
        private String apiVersion;

        @Value("${app.api.title:gpx Racing API}")
        private String apiTitle;

        @Value("${app.api.description:API para el sistema de gestión de eventos de carreras gpx}")
        private String apiDescription;

        @Value("${server.port:8080}")
        private String serverPort;

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .info(apiInfo())
                                .servers(serverList())
                                .tags(apiTags())
                                .components(securityComponents())
                                .addSecurityItem(securityRequirement());
        }

        private Info apiInfo() {
                return new Info()
                                .title(apiTitle)
                                .description(apiDescription + "\n\n" +
                                                "## Funcionalidades principales:\n" +
                                                "- **Gestión de Usuarios**: Registro, autenticación JWT, perfiles\n" +
                                                "- **Gestión de Eventos**: Creación, actualización, categorías\n" +
                                                "- **Gestión de Vehículos**: Registro, datos técnicos, seguros\n" +
                                                "- **Resultados de Etapas**: Tiempos, GPS, clasificaciones\n" +
                                                "- **Autenticación**: JWT + OAuth2 Google\n" +
                                                "- **Archivos**: Upload de imágenes y documentos\n\n" +
                                                "## Autenticación:\n" +
                                                "La API usa JWT Bearer tokens. Obtén un token con `/api/users/login` " +
                                                "o OAuth2 Google, luego inclúyelo en el header: `Authorization: Bearer {token}`")
                                .version(apiVersion)
                                .contact(new Contact()
                                                .name("Equipo gpx Racing")
                                                .email("gpx@udea.edu.co")
                                                .url("https://github.com/udea/gpx-racing"))
                                .license(new License()
                                                .name("MIT License")
                                                .url("https://opensource.org/licenses/MIT"));
        }

        private List<Server> serverList() {
                return Arrays.asList(
                                new Server()
                                                .url("http://localhost:" + serverPort)
                                                .description("Servidor de desarrollo local"),
                                new Server()
                                                .url("https://gpx-api.render.com")
                                                .description("Servidor de producción (Render)"),
                                new Server()
                                                .url("https://staging-gpx-api.render.com")
                                                .description("Servidor de staging"));
        }

        private List<Tag> apiTags() {
                return Arrays.asList(
                                new Tag().name("Autenticación")
                                                .description("Endpoints para login, registro y gestión de tokens JWT"),
                                new Tag().name("Usuarios")
                                                .description("Gestión de perfiles de usuario, datos personales y roles"),
                                new Tag().name("Eventos")
                                                .description("Creación y gestión de eventos de carreras gpx"),
                                new Tag().name("Categorías")
                                                .description("Gestión de categorías de eventos"),
                                new Tag().name("Vehículos")
                                                .description("Registro y gestión de vehículos de competencia"),
                                new Tag().name("Etapas")
                                                .description("Gestión de etapas de eventos"),
                                new Tag().name("Resultados")
                                                .description("Registro de tiempos y resultados de etapas"),
                                new Tag().name("Archivos")
                                                .description("Upload y gestión de imágenes y documentos"),
                                new Tag().name("OAuth2")
                                                .description("Autenticación con Google OAuth2"),
                                new Tag().name("Administración")
                                                .description("Endpoints administrativos y de monitoreo"));
        }

        private Components securityComponents() {
                return new Components()
                                .addSecuritySchemes("Bearer Authentication",
                                                new SecurityScheme()
                                                                .type(SecurityScheme.Type.HTTP)
                                                                .scheme("bearer")
                                                                .bearerFormat("JWT")
                                                                .description("Token JWT obtenido del endpoint de login. "
                                                                                +
                                                                                "Formato: `Bearer {token}`"));
        }

        private SecurityRequirement securityRequirement() {
                return new SecurityRequirement().addList("Bearer Authentication");
        }
}