# 🏁 gpx Racing Event Management System

<!-- Badges profesionales y organizados -->
<p align="center">
  <a href="https://github.com/gpx-PI/gpx-Back/actions/workflows/ci.yml">
    <img src="https://github.com/gpx-PI/gpx-Back/actions/workflows/ci.yml/badge.svg" alt="CI/CD Pipeline"/>
  </a>
  <a href="https://codecov.io/gh/gpx-PI/gpx-Back">
    <img src="https://codecov.io/gh/gpx-PI/gpx-Back/graph/badge.svg?token=J6HXKK6S0H" alt="codecov"/>
  </a>
  <a href="https://sonarcloud.io/summary/new_code?id=gpx-PI_gpx-Back">
    <img src="https://sonarcloud.io/api/project_badges/measure?project=gpx-PI_gpx-Back&metric=alert_status" alt="Quality Gate Status"/>
  </a>
  <a href="https://github.com/gpx-PI/gpx-Back/commits/main">
    <img src="https://img.shields.io/github/last-commit/gpx-PI/gpx-Back" alt="Last Commit"/>
  </a>
  <a href="https://openjdk.java.net/projects/jdk/17/">
    <img src="https://img.shields.io/badge/java-17-blue.svg" alt="Java"/>
  </a>
  <a href="https://spring.io/projects/spring-boot">
    <img src="https://img.shields.io/badge/Spring%20Boot-3.4.4-brightgreen.svg" alt="Spring Boot"/>
  </a>
  <a href="LICENSE">
    <img src="https://img.shields.io/badge/license-MIT-green.svg" alt="License"/>
  </a>
</p>

<!-- Badges adicionales de monitoreo y estadísticas -->
<p align="center">
  <a href="https://project-gpx.vercel.app">
    <img src="https://img.shields.io/website?url=https%3A//project-gpx.vercel.app" alt="Website Status"/>
  </a>
  <a href="https://github.com/gpx-PI/gpx-Back/commits/main">
    <img src="https://img.shields.io/github/commit-activity/m/gpx-PI/gpx-Back" alt="Commit Activity"/>
  </a>
  <a href="https://github.com/gpx-PI/gpx-Back/issues">
    <img src="https://img.shields.io/github/issues/gpx-PI/gpx-Back" alt="GitHub issues"/>
  </a>
</p>

## 🚀 **Demo en Vivo**

🌐 **Aplicación Desplegada**: [https://project-gpx.vercel.app](https://project-gpx.vercel.app)

> ⏱️ **Nota**: La primera carga puede tomar unos momentos debido a que el backend y la base de datos están desplegados en **Render** (plan gratuito), lo cual causa un "cold start" inicial.

## 📋 Descripción

Sistema de gestión de eventos de carreras desarrollado con **Spring Boot 3.4.4** y **Java 17**. Permite administrar eventos, vehículos, usuarios, categorías y resultados de etapas de carreras. Incluye autenticación dual (tradicional y OAuth2), gestión de archivos y un completo sistema de roles y permisos.

## ✨ Características Principales

- 🔐 **Autenticación Dual**: JWT tradicional y OAuth2 con Google
- 👥 **Gestión de Usuarios**: CRUD completo con roles de administrador
- 🚗 **Gestión de Vehículos**: Registro y administración de vehículos por usuario
- 🏆 **Gestión de Eventos**: Creación y administración de eventos de carrera
- 🏁 **Gestión de Etapas**: Control de etapas con neutralización
- 📊 **Resultados y Clasificaciones**: Cálculo automático de tiempos y clasificaciones
- 📁 **Gestión de Archivos**: Subida segura de imágenes y documentos
- 🔒 **Seguridad Avanzada**: JWT, validaciones y control de acceso por roles
- 📱 **API RESTful**: Endpoints completos para integración frontend
- 🧪 **Testing Completo**: Cobertura exhaustiva de tests unitarios e integración

## 🛠️ Stack Tecnológico

### Backend Framework

- **Spring Boot 3.4.4**
- **Java 17**
- **Spring Security** (JWT + OAuth2)
- **Spring Data JPA**
- **Spring Validation**

### Base de Datos

- **PostgreSQL** (Producción - Render)
- **MySQL 8.0** (Desarrollo local)
- **H2** (Testing)

### Seguridad y Autenticación

- **JWT (JSON Web Tokens)** - JJWT 0.11.5
- **OAuth2 Client** - Google Authentication
- **Spring Security** - Autorización y filtros

### Testing

- **JUnit 5** (Jupiter)
- **Mockito 4.0.0** - Mocking framework
- **Spring Boot Test** - Testing de integración
- **WebFlux** - Testing reactivo

### Herramientas de Desarrollo

- **Maven** - Gestión de dependencias
- **Spring Boot Actuator** - Monitoreo y métricas
- **Dotenv** - Gestión de variables de entorno

## 🏗️ Arquitectura

```
src/main/java/com/udea/gpx/
├── controller/         # Capa de presentación (API REST)
├── service/            # Lógica de negocio
├── repository/         # Acceso a datos (JPA)
├── model/              # Entidades del dominio
├── config/             # Configuraciones
├── dto/                # Data Transfer Objects
├── util/               # Utilidades y helpers
├── SecurityConfig.java # Configuración de seguridad
├── JwtUtil.java        # Utilidades JWT
└── GpxApplication.java # Clase principal
```

## 🗄️ **Modelo de Datos**

### 📊 **Entidades Principales**

| Entidad           | Descripción               | Campos Clave                                                      |
| ----------------- | ------------------------- | ----------------------------------------------------------------- |
| **User**          | Usuarios del sistema      | `id`, `username`, `email`, `isAdmin`, `picture`, `insurance`      |
| **Vehicle**       | Vehículos de los usuarios | `id`, `name`, `licensePlate`, `soat`, `category`                  |
| **Event**         | Eventos de carreras       | `id`, `name`, `description`, `startDate`, `endDate`, `pictureUrl` |
| **Stage**         | Etapas de eventos         | `id`, `name`, `description`, `eventId`, `isNeutralized`           |
| **StageResult**   | Resultados por etapa      | `id`, `vehicleId`, `stageId`, `startTime`, `endTime`, `penalties` |
| **Category**      | Categorías de vehículos   | `id`, `name`, `description`, `color`                              |
| **EventVehicle**  | Relación evento-vehículo  | `eventId`, `vehicleId`, `registrationDate`                        |
| **EventCategory** | Relación evento-categoría | `eventId`, `categoryId`                                           |

### 🔗 **Relaciones Clave**

- `User` 1:N `Vehicle` (Un usuario puede tener múltiples vehículos)
- `Event` 1:N `Stage` (Un evento tiene múltiples etapas)
- `Event` M:N `Vehicle` (Relación evento-vehículo participante)
- `Event` M:N `Category` (Eventos por categorías)
- `Vehicle` + `Stage` → `StageResult` (Resultados por vehículo y etapa)

## 📊 **Diagramas de Arquitectura**

### 🎯 **Visualización Completa de Diagramas**

Para una experiencia de visualización completa y optimizada de todos los diagramas del sistema, consulta:

**📋 [DIAGRAMS.md](./DIAGRAMS.md)** - Archivo dedicado con todos los diagramas interactivos

| 🎯 Característica         | 📊 Detalles                                         |
| ------------------------- | --------------------------------------------------- |
| **🔢 Total de Diagramas** | 12+ diagramas completos con iconos descriptivos     |
| **🎨 Calidad Visual**     | Fuentes optimizadas (10-14px) y colores codificados |
| **📱 Compatibilidad**     | GitHub, Codespaces, Mermaid Live, y zoom nativo     |
| **🧩 Cobertura Completa** | Arquitectura, autenticación, OAuth2, datos, testing |
| **🔍 Interactividad**     | Múltiples opciones de visualización y navegación    |

### 🚀 **Acceso Rápido a Diagramas**

| 🔗 Diagrama                 | 📝 Descripción                                 |
| --------------------------- | ---------------------------------------------- |
| **🏗️ Arquitectura Sistema** | Capas MVC, servicios, controladores, seguridad |
| **🔐 Flujo Autenticación**  | JWT tradicional + OAuth2 Google completo       |
| **🗄️ Modelo Base de Datos** | ERD con 8 entidades y relaciones detalladas    |
| **📁 Sistema de Archivos**  | Validaciones, almacenamiento, y seguridad      |
| **🧪 Arquitectura Testing** | Pirámide de tests con cobertura completa       |

> 💡 **Recomendación**: Usa el **[archivo de diagramas dedicado](./DIAGRAMS.md)** para mejor experiencia de zoom y navegación.

## 📚 API Endpoints

### 🔐 Autenticación

| Método | Endpoint                       | Descripción              | Autorización |
| ------ | ------------------------------ | ------------------------ | ------------ |
| `POST` | `/api/users/login`             | Login tradicional        | Público      |
| `POST` | `/api/users/simple-register`   | Registro simplificado    | Público      |
| `GET`  | `/api/users/oauth2/login-url`  | URL para OAuth2 Google   | Público      |
| `GET`  | `/oauth2/authorization/google` | Iniciar flujo OAuth2     | Público      |
| `GET`  | `/api/oauth2/success`          | Callback OAuth2 exitoso  | Público      |
| `GET`  | `/api/oauth2/profile-status`   | Estado del perfil OAuth2 | Auth         |
| `POST` | `/api/users/logout`            | Logout unificado         | Auth         |
| `GET`  | `/api/users/validate-token`    | Validar token JWT        | Auth         |

### 👥 Gestión de Usuarios

| Método   | Endpoint                           | Descripción                       | Autorización |
| -------- | ---------------------------------- | --------------------------------- | ------------ |
| `GET`    | `/api/users`                       | Listar todos los usuarios         | Admin        |
| `GET`    | `/api/users/{id}`                  | Obtener usuario por ID            | Owner/Admin  |
| `PUT`    | `/api/users/{id}`                  | Actualizar usuario (con archivos) | Owner/Admin  |
| `PUT`    | `/api/users/{id}/profile`          | Actualizar perfil (JSON)          | Owner/Admin  |
| `POST`   | `/api/users/{id}/complete-profile` | Completar perfil básico           | Owner/Admin  |
| `PUT`    | `/api/users/{id}/picture`          | Actualizar foto por URL           | Owner/Admin  |
| `DELETE` | `/api/users/{id}/insurance`        | Eliminar seguro médico            | Owner/Admin  |
| `PUT`    | `/api/users/{id}/change-password`  | Cambiar contraseña                | Owner/Admin  |
| `PATCH`  | `/api/users/{id}/admin`            | Alternar rol administrador        | Admin        |
| `GET`    | `/api/users/admins`                | Listar administradores            | Admin        |
| `GET`    | `/api/users/check-email`           | Verificar email disponible        | Público      |

### 🚗 Gestión de Vehículos

| Método   | Endpoint                   | Descripción                | Autorización |
| -------- | -------------------------- | -------------------------- | ------------ |
| `GET`    | `/api/vehicles`            | Listar todos los vehículos | Auth         |
| `GET`    | `/api/vehicles/{id}`       | Obtener vehículo por ID    | Auth         |
| `POST`   | `/api/vehicles`            | Crear nuevo vehículo       | Auth         |
| `PUT`    | `/api/vehicles/{id}`       | Actualizar vehículo        | Owner/Admin  |
| `DELETE` | `/api/vehicles/{id}`       | Eliminar vehículo          | Owner/Admin  |
| `GET`    | `/api/vehicles/bycategory` | Vehículos por categoría    | Auth         |
| `GET`    | `/api/vehicles/byuser`     | Vehículos por usuario      | Auth         |

### 🏆 Gestión de Eventos

| Método   | Endpoint                      | Descripción              | Autorización |
| -------- | ----------------------------- | ------------------------ | ------------ |
| `GET`    | `/api/events`                 | Listar todos los eventos | Público      |
| `GET`    | `/api/events/{id}`            | Obtener evento por ID    | Público      |
| `GET`    | `/api/events/current`         | Eventos actuales         | Público      |
| `GET`    | `/api/events/past`            | Eventos pasados          | Público      |
| `POST`   | `/api/events`                 | Crear nuevo evento       | Admin        |
| `PUT`    | `/api/events/{id}`            | Actualizar evento        | Admin        |
| `DELETE` | `/api/events/{id}`            | Eliminar evento          | Admin        |
| `PUT`    | `/api/events/{id}/picture`    | Subir imagen (archivo)   | Admin        |
| `PUT`    | `/api/events/{id}/picture`    | Actualizar imagen (URL)  | Admin        |
| `DELETE` | `/api/events/{id}/picture`    | Eliminar imagen          | Admin        |
| `GET`    | `/api/events/{id}/categories` | Categorías del evento    | Público      |

### 🏁 Gestión de Etapas

| Método | Endpoint                        | Descripción             | Autorización |
| ------ | ------------------------------- | ----------------------- | ------------ |
| `GET`  | `/api/stages`                   | Listar todas las etapas | Público      |
| `GET`  | `/api/stages/{id}`              | Obtener etapa por ID    | Público      |
| `GET`  | `/api/stages/byevent/{eventId}` | Etapas por evento       | Público      |

### 📊 Resultados de Etapas

| Método   | Endpoint                                           | Descripción            | Autorización |
| -------- | -------------------------------------------------- | ---------------------- | ------------ |
| `GET`    | `/api/stageresults`                                | Todos los resultados   | Público      |
| `GET`    | `/api/stageresults/{id}`                           | Resultado por ID       | Público      |
| `POST`   | `/api/stageresults`                                | Crear resultado        | Admin        |
| `PUT`    | `/api/stageresults/{id}`                           | Actualizar resultado   | Admin        |
| `DELETE` | `/api/stageresults/{id}`                           | Eliminar resultado     | Admin        |
| `GET`    | `/api/stageresults/clasificacion`                  | Clasificaciones        | Público      |
| `GET`    | `/api/stageresults/bystagerange`                   | Resultados por rango   | Público      |
| `POST`   | `/api/stageresults/update-elapsed-times/{eventId}` | Actualizar tiempos     | Admin        |
| `PUT`    | `/api/stageresults/penalizacion/{id}`              | Aplicar penalizaciones | Admin        |

### 🏷️ Gestión de Categorías

| Método   | Endpoint               | Descripción              | Autorización |
| -------- | ---------------------- | ------------------------ | ------------ |
| `GET`    | `/api/categories`      | Listar categorías        | Público      |
| `GET`    | `/api/categories/{id}` | Obtener categoría por ID | Público      |
| `POST`   | `/api/categories`      | Crear categoría          | Admin        |
| `PUT`    | `/api/categories/{id}` | Actualizar categoría     | Admin        |
| `DELETE` | `/api/categories/{id}` | Eliminar categoría       | Admin        |

### 🔗 Relaciones Evento-Vehículo

| Método | Endpoint                               | Descripción          | Autorización |
| ------ | -------------------------------------- | -------------------- | ------------ |
| `GET`  | `/api/eventvehicles`                   | Listar relaciones    | Auth         |
| `GET`  | `/api/eventvehicles/{id}`              | Relación por ID      | Auth         |
| `GET`  | `/api/eventvehicles/byevent/{eventId}` | Vehículos por evento | Auth         |

### 🔗 Relaciones Evento-Categoría

| Método | Endpoint                    | Descripción       | Autorización |
| ------ | --------------------------- | ----------------- | ------------ |
| `GET`  | `/api/eventcategories`      | Listar relaciones | Público      |
| `GET`  | `/api/eventcategories/{id}` | Relación por ID   | Público      |

## ⚙️ Configuración y Instalación

### 📋 Prerrequisitos

- **Java 17** o superior
- **Maven 3.8+**
- **PostgreSQL 12+** (o MySQL 8.0 para desarrollo)
- **Git**

### 🔧 Variables de Entorno

Crear archivo `.env` en la raíz del proyecto:

```env
# Base de Datos
DB_URL=jdbc:postgresql://localhost:5432/gpx_db
DB_USERNAME=tu_usuario
DB_PASSWORD=tu_password

# JWT Configuration
JWT_SECRET=tu-clave-secreta-super-larga-y-segura-para-jwt-tokens

# Google OAuth2
GOOGLE_CLIENT_ID=tu-google-client-id
GOOGLE_CLIENT_SECRET=tu-google-client-secret
GOOGLE_OAUTH2_REDIRECT_URI=http://localhost:8080/login/oauth2/code/google

# Frontend
FRONTEND_REDIRECT_URL=http://localhost:3000/

# CORS
CORS_ALLOWED_ORIGINS=http://localhost:3000

# Puerto del servidor
PORT=8080
```

### 🚀 Instalación y Ejecución

1. **Clonar el repositorio**

```bash
git clone <repository-url>
cd gpx-backend
```

2. **Configurar base de datos**

```sql
CREATE DATABASE gpx_db;
```

3. **Instalar dependencias**

```bash
mvn clean install
```

4. **Ejecutar la aplicación**

```bash
mvn spring-boot:run
```

5. **Acceder a la API**

```
http://localhost:8080/api
```

## 🧪 Testing

### Ejecutar todos los tests

```bash
mvn test
```

### Ejecutar tests específicos

```bash
mvn test -Dtest=UserControllerTests
mvn test -Dtest=EventControllerTests
```

### Cobertura de Tests

- **9 archivos de test** completos
- **Tests unitarios** para todos los controladores
- **Tests de integración** para flujos completos
- **Mocking** de dependencias con Mockito
- **Validación** de endpoints y autenticación

## 📁 Gestión de Archivos

### Tipos de archivo soportados

- **Imágenes**: JPG, JPEG, PNG, GIF, WebP
- **Documentos**: PDF (para seguros médicos)

### Validaciones de seguridad

- ✅ Validación de tipo MIME
- ✅ Validación de extensión
- ✅ Límite de tamaño (10MB)
- ✅ Eliminación de archivos anteriores
- ✅ Estructura de directorios segura

### Estructura de almacenamiento

```
uploads/
├── profiles/          # Fotos de perfil
├── events/           # Imágenes de eventos
└── insurance/        # Documentos de seguro
```

## 🔒 Seguridad

### Autenticación

- **JWT Tokens** con expiración configurable
- **OAuth2** integración con Google
- **Filtros de seguridad** personalizados
- **Validación** de tokens en cada request

### Autorización

- **Roles**: Usuario estándar y Administrador
- **Permisos granulares** por endpoint
- **Validación de propietario** para recursos privados
- **CORS** configurado para frontend

### Validaciones

- **Bean Validation** (JSR-303) en DTOs
- **Validación personalizada** de archivos
- **Sanitización** de inputs
- **Manejo seguro** de errores

## 🚀 Despliegue

### Producción con Render

```bash
# Variables de entorno en Render
DATABASE_URL=postgresql://...
JWT_SECRET=...
GOOGLE_CLIENT_ID=...
GOOGLE_CLIENT_SECRET=...
```

### Docker (Opcional)

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/gpx-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

## 📊 Monitoreo

### Spring Boot Actuator

- `/actuator/health` - Estado de la aplicación
- `/actuator/info` - Información del build
- `/actuator/metrics` - Métricas de rendimiento

## 🤝 Contribución

1. Fork del proyecto
2. Crear branch para feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit de cambios (`git commit -am 'Agregar nueva funcionalidad'`)
4. Push al branch (`git push origin feature/nueva-funcionalidad`)
5. Crear Pull Request

## 📝 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## 📞 Contacto

Para consultas sobre el proyecto o colaboraciones, contactar al equipo de desarrollo.

---

## 📈 **Métricas del Proyecto**

### 📊 **Estadísticas de Código**

- **9 Controladores REST** con 45+ endpoints
- **8 Entidades JPA** con relaciones complejas
- **10 Servicios** con lógica de negocio
- **9 Suites de Testing** con 300+ tests
- **Cobertura de Tests**: >85% líneas de código
- **Arquitectura en 3 capas**: Controller → Service → Repository

### 🎯 **Casos de Uso Principales**

#### 👨‍💼 **Administrador**

- Gestionar eventos de carreras
- Administrar usuarios y roles
- Subir imágenes y documentos
- Calcular resultados y clasificaciones
- Neutralizar etapas

#### 👤 **Usuario Final**

- Registrarse (local o Google)
- Gestionar perfil completo
- Registrar vehículos
- Ver eventos y resultados
- Actualizar información personal

## 🔄 **Changelog**

### v0.0.1-SNAPSHOT (Actual)

- ✅ **Core System**: CRUD completo para todas las entidades
- ✅ **Authentication**: Sistema dual JWT + OAuth2 Google
- ✅ **File Management**: Sistema de archivos con validaciones de seguridad
- ✅ **Testing Suite**: 300+ tests unitarios e integración
- ✅ **Multi-Database**: Soporte PostgreSQL/MySQL/H2
- ✅ **Security**: Autorización granular + validaciones + CORS
- ✅ **API Documentation**: 45+ endpoints RESTful documentados
- ✅ **Production Ready**: Configuración para Render + variables de entorno
