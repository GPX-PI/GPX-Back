# Usar una imagen base de OpenJDK 17 con Alpine para mejor rendimiento
FROM openjdk:17-jdk-alpine

# Instalar curl para health checks
RUN apk add --no-cache curl

# Crear un usuario no root para seguridad
RUN addgroup -g 1000 appgroup && adduser -D -u 1000 -G appgroup appuser

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar los archivos de Maven para aprovechar el cache de Docker
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Dar permisos de ejecución al wrapper de Maven
RUN chmod +x mvnw

# Descargar dependencias (esto se cachea si el pom.xml no cambia)
RUN ./mvnw dependency:go-offline

# Copiar el código fuente
COPY src src

# Construir la aplicación
RUN ./mvnw clean package -DskipTests

# Crear directorio para uploads
RUN mkdir -p uploads && chown appuser:appgroup uploads

# Cambiar al usuario no root
USER appuser

# Exponer el puerto (Render usa la variable PORT)
EXPOSE $PORT

# Variable de entorno para el perfil de Spring
ENV SPRING_PROFILES_ACTIVE=render

# Comando para ejecutar la aplicación
# Usar la variable PORT de Render para el servidor
CMD ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar target/GPX-0.0.1-SNAPSHOT.jar"] 