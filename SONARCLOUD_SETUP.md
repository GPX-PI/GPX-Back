# SonarCloud Configuration Guide

Este documento describe cómo configurar SonarCloud para el proyecto GPX Spring Boot Application.

## Configuración Inicial

### 1. Configuración de SonarCloud

1. Ve a [SonarCloud.io](https://sonarcloud.io) e inicia sesión con tu cuenta de GitHub
2. Crea una nueva organización o usa una existente
3. Importa tu repositorio de GitHub
4. Obtén tu token de SonarCloud desde tu perfil > Security

### 2. Configuración de GitHub Secrets

Agrega los siguientes secrets en tu repositorio de GitHub (Settings > Secrets and variables > Actions):

```
SONAR_TOKEN=tu-token-de-sonarcloud
```

### 3. Actualizar Configuración del Proyecto

Edita el archivo `sonar-project.properties` y actualiza:

```properties
sonar.organization=tu-organizacion-en-sonarcloud
sonar.projectKey=tu-organizacion_nombre-del-proyecto
```

También actualiza el workflow `.github/workflows/sonarcloud.yml` con los valores correctos:

```yaml
-Dsonar.projectKey=tu-organizacion_nombre-del-proyecto \
-Dsonar.organization=tu-organizacion \
```

## Características Configuradas

### Cobertura de Código

- **Reporte JaCoCo**: Configurado para generar reportes XML
- **Ubicación**: `target/site/jacoco/jacoco.xml`
- **Exclusiones**: Archivos de configuración, entidades, DTOs y excepciones

### Análisis de Calidad

- **Quality Gate**: Configurado para esperar resultados
- **Duplicación**: Mínimo 50 tokens para detectar duplicados
- **Versión Java**: Java 17

### Exclusiones

- `**/*Application.java` - Clase principal de Spring Boot
- `**/config/**` - Clases de configuración
- `**/entity/**` - Entidades JPA
- `**/dto/**` - Data Transfer Objects
- `**/exception/**` - Clases de excepción personalizadas

## Workflows Configurados

### SonarCloud Analysis (`sonarcloud.yml`)

- **Trigger**: Push a main/develop, Pull Requests
- **Acciones**:
  1. Checkout del código
  2. Configuración de Java 17
  3. Cache de dependencias Maven y SonarCloud
  4. Ejecución de tests con cobertura
  5. Análisis de SonarCloud
  6. Verificación del Quality Gate

## Comandos Locales

### Ejecutar análisis local

```bash
mvn clean test jacoco:report sonar:sonar \
  -Dsonar.projectKey=tu-organizacion_nombre-del-proyecto \
  -Dsonar.organization=tu-organizacion \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.login=tu-token-de-sonarcloud
```

### Solo generar cobertura

```bash
mvn clean test jacoco:report
```

## Métricas Monitoreadas

- **Cobertura de código**: Objetivo mínimo 80%
- **Duplicación**: Máximo 3%
- **Mantenibilidad**: Rating A
- **Confiabilidad**: Rating A
- **Seguridad**: Rating A
- **Security Hotspots**: 100% revisados

## Solución de Problemas

### Error de autenticación

- Verifica que el `SONAR_TOKEN` esté configurado correctamente
- Asegúrate de que el token tenga permisos para el proyecto

### Fallo en Quality Gate

- Revisa las métricas que fallan en el dashboard de SonarCloud
- Ajusta el código según las recomendaciones
- Considera ajustar las configuraciones si son demasiado estrictas

### Problemas de cobertura

- Verifica que JaCoCo esté generando el reporte correctamente
- Revisa las exclusiones en `sonar-project.properties`
- Asegúrate de que los tests se ejecuten antes del análisis

## Enlaces Útiles

- [SonarCloud Documentation](https://docs.sonarcloud.io/)
- [SonarCloud Maven Plugin](https://docs.sonarqube.org/latest/analysis/scan/sonarscanner-for-maven/)
- [JaCoCo Maven Plugin](https://www.jacoco.org/jacoco/trunk/doc/maven.html)
