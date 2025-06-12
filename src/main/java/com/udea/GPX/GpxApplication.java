package com.udea.gpx;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.File;

@SpringBootApplication
public class GpxApplication {

    public static void main(String[] args) {
        // Cargar variables de entorno desde archivos .env
        loadEnvironmentVariables();

        SpringApplication.run(GpxApplication.class, args);
    }

    private static void loadEnvironmentVariables() {
        try {
            // Intentar cargar .env.dev primero, luego .env como fallback
            File envDevFile = new File(".env.dev");
            File envFile = new File(".env");

            Dotenv dotenv = null;
            if (envDevFile.exists()) {
                dotenv = Dotenv.configure().filename(".env.dev").load();
            } else if (envFile.exists()) {
                dotenv = Dotenv.configure().filename(".env").load();
            }

            // Establecer variables como propiedades del sistema para Spring Boot
            if (dotenv != null) {
                dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
            }
        } catch (Exception e) {
            // Continuar sin errores si no se pueden cargar las variables
            // Las variables pueden estar configuradas de otras maneras (sistema, Docker,
            // etc.)
        }
    }

    // CORS configuration moved to CorsConfig.java

}