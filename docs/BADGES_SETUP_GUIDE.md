# 🏆 Guía de Configuración de Insignias (Badges)

## 📊 Insignias Configuradas

Este proyecto incluye múltiples badges para mostrar la calidad del software. Aquí te explico cómo configurar cada uno:

## 🔧 **1. GitHub Actions (CI/CD)**

### Badge Automático ✅

```markdown
[![CI/CD Pipeline](https://github.com/TU-USUARIO/TU-REPO/actions/workflows/ci.yml/badge.svg)](https://github.com/TU-USUARIO/TU-REPO/actions/workflows/ci.yml)
```

**Configuración**: Ya incluido en `.github/workflows/ci.yml` - Funciona automáticamente

## 📈 **2. Codecov (Cobertura de Código)**

### Configuración Requerida:

1. Ve a [codecov.io](https://codecov.io/)
2. Conecta tu cuenta de GitHub
3. Agrega tu repositorio
4. Copia tu token

### Agregar Secret en GitHub:

```bash
# En GitHub repo > Settings > Secrets and variables > Actions
CODECOV_TOKEN = "tu-token-de-codecov"
```

### Badge:

```markdown
[![codecov](https://codecov.io/gh/gpx-PI/gpx-Back/graph/badge.svg?token=J6HXKK6S0H)](https://codecov.io/gh/gpx-PI/gpx-Back)
```

## 🔍 **3. SonarCloud (Análisis de Calidad)**

### Configuración:

1. Ve a [sonarcloud.io](https://sonarcloud.io/)
2. Conecta tu GitHub
3. Importa tu repositorio
4. Obtén tu token y clave del proyecto

### Agregar Secrets:

```bash
SONAR_TOKEN = "tu-token-de-sonarcloud"
```

### Agregar al workflow (ejemplo):

```yaml
- name: 🔍 SonarCloud Scan
  uses: SonarSource/sonarcloud-github-action@master
  env:
    GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
    SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
```

### Badges SonarCloud:

```markdown
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=TU-PROYECTO&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=TU-PROYECTO)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=TU-PROYECTO&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=TU-PROYECTO)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=TU-PROYECTO&metric=maintainability_rating)](https://sonarcloud.io/summary/new_code?id=TU-PROYECTO)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=TU-PROYECTO&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=TU-PROYECTO)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=TU-PROYECTO&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=TU-PROYECTO)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=TU-PROYECTO&metric=bugs)](https://sonarcloud.io/summary/new_code?id=TU-PROYECTO)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=TU-PROYECTO&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=TU-PROYECTO)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=TU-PROYECTO&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=TU-PROYECTO)
```

## 🛠️ **4. Badges de Tecnología** (Estáticos) ✅

```markdown
[![Java](https://img.shields.io/badge/java-17-blue.svg)](https://openjdk.java.net/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.9-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.9+-red.svg)](https://maven.apache.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-blue.svg)](https://www.postgresql.org/)
[![JWT](https://img.shields.io/badge/JWT-0.11.5-orange.svg)](https://jwt.io/)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)
```

## 📡 **5. Uptime Robot (Monitoreo de Uptime)**

### Configuración:

1. Ve a [uptimerobot.com](https://uptimerobot.com/)
2. Crea una cuenta gratuita
3. Agrega tu URL de aplicación para monitoreo
4. Crea una "Status Page" pública
5. Obtén tu monitor ID

### Badges:

```markdown
[![Uptime Robot status](https://img.shields.io/uptimerobot/status/m794325736-YOUR-MONITOR-ID)](https://stats.uptimerobot.com/YOUR-STATUS-PAGE)
[![Uptime Robot ratio (30 days)](https://img.shields.io/uptimerobot/ratio/m794325736-YOUR-MONITOR-ID)](https://stats.uptimerobot.com/YOUR-STATUS-PAGE)
[![Website](https://img.shields.io/website?url=https%3A//project-gpx.vercel.app)](https://project-gpx.vercel.app)
```

## 📦 **6. Badges de GitHub** (Automáticos) ✅

```markdown
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/TU-USUARIO/TU-REPO)](https://github.com/TU-USUARIO/TU-REPO/releases)
[![GitHub last commit](https://img.shields.io/github/last-commit/TU-USUARIO/TU-REPO)](https://github.com/TU-USUARIO/TU-REPO/commits/main)
[![GitHub issues](https://img.shields.io/github/issues/TU-USUARIO/TU-REPO)](https://github.com/TU-USUARIO/TU-REPO/issues)
[![GitHub pull requests](https://img.shields.io/github/issues-pr/TU-USUARIO/TU-REPO)](https://github.com/TU-USUARIO/TU-REPO/pulls)
[![GitHub contributors](https://img.shields.io/github/contributors/TU-USUARIO/TU-REPO)](https://github.com/TU-USUARIO/TU-REPO/graphs/contributors)
```

## 🔄 **7. Badges Adicionales Recomendados**

### Docker (si usas Docker):

```markdown
[![Docker Image Size](https://img.shields.io/docker/image-size/TU-USUARIO/TU-REPO)](https://hub.docker.com/r/TU-USUARIO/TU-REPO)
[![Docker Pulls](https://img.shields.io/docker/pulls/TU-USUARIO/TU-REPO)](https://hub.docker.com/r/TU-USUARIO/TU-REPO)
```

### Dependabot (Automático):

```markdown
[![Dependabot Status](https://api.dependabot.com/badges/status?host=github&repo=TU-USUARIO/TU-REPO)](https://dependabot.com)
```

### Snyk (Seguridad):

```markdown
[![Known Vulnerabilities](https://snyk.io/test/github/TU-USUARIO/TU-REPO/badge.svg)](https://snyk.io/test/github/TU-USUARIO/TU-REPO)
```

## 📋 **Checklist de Configuración**

### ✅ Inmediatos (Sin configuración):

- [x] GitHub Actions CI/CD
- [x] Badges de tecnología
- [x] Badges de GitHub (issues, commits, etc.)
- [x] Website status

### 🔧 Requieren Configuración:

- [ ] Codecov (cobertura de código)
- [ ] SonarCloud (calidad de código)
- [ ] Uptime Robot (monitoreo de uptime)
- [ ] Snyk (seguridad)

### 🎯 Opcionales Avanzados:

- [ ] Docker Hub badges
- [ ] Dependabot
- [ ] Custom badges con shields.io

## 🎨 **Personalización de Badges**

Para crear badges personalizados, usa [shields.io](https://shields.io/):

```markdown
[![Custom Badge](https://img.shields.io/badge/Custom-Message-color.svg)](https://tu-enlace.com)
```

## 🔗 **Enlaces de Servicios**

| Servicio         | URL                      | Propósito                 |
| ---------------- | ------------------------ | ------------------------- |
| **Codecov**      | https://codecov.io/      | Cobertura de código       |
| **SonarCloud**   | https://sonarcloud.io/   | Análisis de calidad       |
| **Uptime Robot** | https://uptimerobot.com/ | Monitoreo de uptime       |
| **Snyk**         | https://snyk.io/         | Seguridad de dependencias |
| **Shields.io**   | https://shields.io/      | Badges personalizados     |

## 📝 **Notas Importantes**

1. **Reemplaza** `TU-USUARIO/TU-REPO` con tu información real
2. **Configura los secrets** en GitHub antes de usar servicios externos
3. **Algunos badges requieren** que hagas push después de la configuración
4. **Monitorea** que todos los badges funcionen después de la configuración
5. **Considera la privacidad** - algunos servicios requieren repos públicos

¡Con estas insignias tu proyecto mostrará profesionalismo y transparencia en la calidad del código! 🚀
