# Workflows de SonarCloud para Warp

Este directorio contiene workflows de Warp para automatizar el análisis de código con SonarCloud.

## 📁 Workflows Disponibles

### 1. `sonarcloud-coverage.yaml`
**Propósito:** Ejecuta análisis completo de cobertura en SonarCloud

**¿Cuándo usar?**
- Tu proyecto ya está configurado para SonarCloud
- Tienes `sonar-project.properties` configurado
- JaCoCo está configurado en tu `pom.xml`

**Uso:**
```bash
# Configurar token (una sola vez)
$env:SONAR_TOKEN="tu_token_de_sonarcloud"

# Ejecutar análisis
.warp/sonarcloud-coverage.ps1
```

### 2. `sonarcloud-setup.yaml`
**Propósito:** Ayuda a configurar SonarCloud desde cero

**¿Cuándo usar?**
- Es la primera vez que configuras SonarCloud en el proyecto
- No estás seguro si tu proyecto está configurado correctamente
- Quieres verificar la configuración existente

**Uso:**
```bash
.warp/sonarcloud-setup.ps1
```

## 🚀 Configuración Rápida

### Paso 1: Verificar configuración
```bash
.warp/sonarcloud-setup.ps1
```

### Paso 2: Configurar token
1. Ve a https://sonarcloud.io/account/security/
2. Genera un nuevo token
3. Configura la variable de entorno:
```bash
$env:SONAR_TOKEN="tu_token_aqui"
```

### Paso 3: Ejecutar análisis
```bash
.warp/sonarcloud-coverage.ps1
```

## ⚙️ Configuración de Proyecto

### Para proyectos Maven

Asegúrate de que tu `pom.xml` incluya:

```xml
<!-- Plugin JaCoCo para cobertura -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.10</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>

<!-- Plugin SonarCloud -->
<plugin>
    <groupId>org.sonarsource.scanner.maven</groupId>
    <artifactId>sonar-maven-plugin</artifactId>
    <version>3.10.0.2594</version>
</plugin>
```

### Archivo `sonar-project.properties`

Crea este archivo en la raíz del proyecto:

```properties
# Información del proyecto
sonar.organization=tu-organizacion
sonar.projectKey=tu-proyecto-key
sonar.projectName=Nombre del Proyecto
sonar.projectVersion=1.0

# Directorios
sonar.sources=src/main/java
sonar.tests=src/test/java
sonar.java.binaries=target/classes

# Cobertura
sonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml

# Exclusiones (opcional)
sonar.exclusions=**/*Application.java,**/config/**
sonar.coverage.exclusions=**/*Application.java,**/config/**
```

## 🔧 Variables de Entorno

| Variable | Requerida | Descripción |
|----------|-----------|-------------|
| `SONAR_TOKEN` | ✅ Sí | Token de autenticación de SonarCloud |
| `SONAR_ORGANIZATION` | ❌ No | Organización (se puede definir en `sonar-project.properties`) |
| `SONAR_PROJECT_KEY` | ❌ No | Clave del proyecto (se puede definir en `sonar-project.properties`) |

## 🎯 Casos de Uso

### Desarrollo diario
```bash
# Análisis rápido antes de hacer commit
.warp/sonarcloud-coverage.ps1
```

### Configuración inicial
```bash
# 1. Verificar configuración
.warp/sonarcloud-setup.ps1

# 2. Seguir las instrucciones mostradas
# 3. Ejecutar análisis
.warp/sonarcloud-coverage.ps1
```

### Debugging de configuración
```bash
# Si algo no funciona, ejecuta setup para diagnosticar
.warp/sonarcloud-setup.ps1
```

## 📊 Qué incluye el análisis

- ✅ **Cobertura de código** (JaCoCo)
- ✅ **Calidad de código** (bugs, vulnerabilidades, code smells)
- ✅ **Métricas de mantenibilidad**
- ✅ **Duplicación de código**
- ✅ **Análisis de seguridad**
- ✅ **Deuda técnica**

## 🔗 Enlaces útiles

- [SonarCloud Dashboard](https://sonarcloud.io/projects)
- [Documentación SonarCloud](https://docs.sonarcloud.io/)
- [Configuración Maven](https://docs.sonarcloud.io/advanced-setup/ci-based-analysis/sonarscanner-for-maven/)
- [Configuración JaCoCo](https://docs.sonarcloud.io/enriching/test-coverage/java-test-coverage/)

## 🤝 Reutilización en otros proyectos

Estos workflows son **genéricos** y se pueden copiar a otros proyectos:

1. Copia la carpeta `.warp/workflows/` a tu nuevo proyecto
2. Ajusta las rutas en `sonar-project.properties` si es necesario
3. Configura el token de SonarCloud
4. ¡Listo para usar!

## 💡 Tips

- **Guarda el token**: El `SONAR_TOKEN` se puede guardar en tu perfil de PowerShell para no tener que configurarlo cada vez
- **Workflows modulares**: Puedes usar solo el workflow de setup si solo quieres verificar la configuración
- **Personalización**: Los workflows se pueden modificar según las necesidades específicas de tu proyecto

---

**¿Tienes problemas?** Ejecuta `.warp/run-workflow.ps1 -WorkflowName sonarcloud-setup` para diagnóstico automático.

