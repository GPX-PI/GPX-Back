# =========================================
# DOCKERFILE OPTIMIZADO - gpx RACING API
# =========================================

# ========== STAGE 1: BUILD ==========
FROM openjdk:21-jdk-slim AS build

# Metadata
LABEL maintainer="gpx Racing Team"
LABEL description="gpx Racing Event Management API"
LABEL version="1.0.0"

# Crear usuario no-root para build
RUN addgroup --system spring && adduser --system spring --ingroup spring

# Instalar dependencias necesarias para build
RUN apt-get update && apt-get install -y \
    maven \
    && rm -rf /var/lib/apt/lists/*

# Establecer directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración Maven primero (para cache)
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .

# Hacer ejecutable el wrapper de Maven
RUN chmod +x ./mvnw

# Descargar dependencias (se cachea si pom.xml no cambia)
RUN ./mvnw dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar aplicación (omitir tests para build más rápido)
RUN ./mvnw clean package -DskipTests

# ========== STAGE 2: RUNTIME ==========
FROM eclipse-temurin:21-jre-alpine AS runtime

# Instalar herramientas útiles para debugging
RUN apt-get update && apt-get install -y \
    curl \
    netcat-traditional \
    && rm -rf /var/lib/apt/lists/*

# Crear usuario no-root para runtime
RUN addgroup --system spring && adduser --system spring --ingroup spring

# Crear directorios necesarios
RUN mkdir -p /app/uploads/profiles /app/uploads/events /app/uploads/insurance /app/logs
RUN chown -R spring:spring /app

# Establecer directorio de trabajo
WORKDIR /app

# Cambiar a usuario no-root
USER spring:spring

# Copiar JAR desde stage de build
COPY --from=build --chown=spring:spring /app/target/*.jar app.jar

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SERVER_PORT=8080

# Exponer puerto
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Comando de inicio
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"] 