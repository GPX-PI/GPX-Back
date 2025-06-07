# 📊 Diagramas del Proyecto - GPX Rally Management

Este directorio contiene todos los diagramas Mermaid que documentan la arquitectura, flujos y funcionamiento completo del sistema de gestión de rallies.

## 🏗️ Arquitectura del Sistema

### [01-arquitectura-general.mmd](./01-arquitectura-general.mmd)

**Arquitectura general del sistema completo**

- Frontend Next.js con páginas principales
- Capa de seguridad con JWT y OAuth2
- Sistema de autenticación con AuthUtils
- Controladores, servicios y DTOs
- Base de datos PostgreSQL

### [12-frontend-arquitectura.mmd](./12-frontend-arquitectura.mmd)

**Arquitectura específica del Frontend Next.js 14**

- App Router con todas las rutas
- Componentes organizados en Atoms, Molecules y Organisms
- Sistema de autenticación y gestión de estado
- Servicios API y utilidades
- Integración con shadcn/ui y Tailwind CSS

## 🗄️ Modelo de Datos

### [03-modelo-datos.mmd](./03-modelo-datos.mmd)

**Modelo de datos actualizado con todas las entidades**

- Usuario con autenticación OAuth2 y local
- Vehículos con categorías
- Eventos con etapas y participantes
- Resultados de etapas con coordenadas GPS y penalizaciones
- Relaciones completas entre entidades

## 🔐 Seguridad y Autenticación

### [05-sistema-permisos.mmd](./05-sistema-permisos.mmd)

**Sistema de permisos completo**

- Endpoints públicos vs protegidos
- Panel de administración con permisos específicos
- Gestión de roles de usuario
- Rutas del frontend con protección

### [14-frontend-autenticacion.mmd](./14-frontend-autenticacion.mmd)

**Sistema de autenticación del frontend**

- Login local y OAuth2 con Google
- Gestión de tokens JWT
- Guards de protección de rutas
- Interceptor HTTP para requests automáticos
- Manejo de sesiones y logout automático

## 🎨 Frontend - Componentes y Flujos

### [15-frontend-componentes.mmd](./15-frontend-componentes.mmd)

**Arquitectura de componentes del frontend**

- Páginas con App Router de Next.js
- Atoms de shadcn/ui (Button, Input, Card, etc.)
- Molecules reutilizables (Navbar, Sidebar, etc.)
- Organisms complejos (AdminDashboard, gestión de entidades)
- Hooks personalizados y servicios API

### [13-frontend-flujo-admin.mmd](./13-frontend-flujo-admin.mmd)

**Flujo completo del panel de administración**

- Verificación de permisos y autenticación
- Dashboard con estadísticas del sistema
- Gestión de eventos, usuarios, categorías
- Gestión avanzada de resultados de etapas
- Manejo de estados, validación y errores

## 🏁 Gestión de Carreras

### [16-stage-results-management.mmd](./16-stage-results-management.mmd)

**Gestión completa de resultados de etapas**

- Selección de eventos y carga de datos
- Filtros por etapa y categoría
- Creación y edición de resultados básicos
- Gestión especializada de penalizaciones
- Conversión de formatos de tiempo
- Procesamiento backend con DTOs

### [04-flujo-carrera.mmd](./04-flujo-carrera.mmd)

**Flujo básico de una carrera**

- Registro de participantes
- Ejecución de etapas
- Registro de tiempos y resultados

### [06-flujo-clasificaciones.mmd](./06-flujo-clasificaciones.mmd)

**Sistema de clasificaciones**

- Clasificación general y por categorías
- Cálculo de tiempos y penalizaciones
- Visualización de resultados

## 🔧 Arquitectura Técnica

### [10-arquitectura-endpoints-limpia.mmd](./10-arquitectura-endpoints-limpia.mmd)

**Endpoints organizados por funcionalidad**

- Autenticación y gestión de usuarios
- CRUD completo de todas las entidades
- Sistema de clasificaciones
- Capa de seguridad y servicios

### [02-flujo-autenticacion.mmd](./02-flujo-autenticacion.mmd)

**Flujo de autenticación backend**

- Autenticación local vs OAuth2
- Generación y validación de tokens JWT
- Integración con Spring Security

## 🧪 Testing y Calidad

### [11-testing-security.mmd](./11-testing-security.mmd)

**Estrategia de testing y seguridad**

- Tests unitarios y de integración
- Validación de seguridad
- Tests de endpoints protegidos

## 🔄 Flujos OAuth2

### [07-flujo-oauth2.mmd](./07-flujo-oauth2.mmd)

**Flujo OAuth2 detallado**

- Integración con Google OAuth2
- Redirects y callbacks
- Manejo de tokens

### [08-flujo-oauth2-simplificado.mmd](./08-flujo-oauth2-simplificado.mmd)

**Versión simplificada del flujo OAuth2**

### [09-flujo-registro-unificado.mmd](./09-flujo-registro-unificado.mmd)

**Sistema de registro unificado**

- Registro simple vs OAuth2
- Completar perfil
- Validaciones

## 🔍 Cómo Visualizar los Diagramas

### Opción 1: Visualizador Local

Abrir [visualizador.html](./visualizador.html) en el navegador para ver todos los diagramas interactivamente.

### Opción 2: VS Code + Mermaid Extension

1. Instalar la extensión "Mermaid Preview" en VS Code
2. Abrir cualquier archivo .mmd
3. Presionar `Ctrl+Shift+P` → "Mermaid: Preview"

### Opción 3: Mermaid Live Editor

1. Ir a [mermaid.live](https://mermaid.live)
2. Copiar el contenido de cualquier archivo .mmd
3. Ver el diagrama renderizado

## 📈 Estado Actual del Proyecto

✅ **Completado:**

- Sistema completo de autenticación (local + OAuth2)
- Panel de administración funcional con CRUD completo
- Gestión avanzada de resultados de etapas con penalizaciones
- Sistema de permisos robusto
- Frontend responsive con shadcn/ui
- Arquitectura de componentes organizada

🚀 **Funcionalidades Principales:**

- **Dashboard Administrativo**: Estadísticas y gestión completa
- **Gestión de Eventos**: CRUD completo con fechas y precios
- **Gestión de Usuarios**: Lista, roles admin, protecciones
- **Gestión de Categorías**: CRUD de categorías de vehículos
- **Gestión de Resultados**: Creación, edición y penalizaciones avanzadas
- **Sistema de Clasificaciones**: Cálculos automáticos con penalizaciones
- **Autenticación Dual**: Local y Google OAuth2
- **Protección de Rutas**: Frontend y backend coordinados

## 🏗️ Arquitectura Destacada

- **Backend**: Spring Boot con arquitectura limpia
- **Frontend**: Next.js 14 con App Router
- **Base de Datos**: PostgreSQL con relaciones optimizadas
- **Autenticación**: JWT + Spring Security + OAuth2
- **UI/UX**: shadcn/ui + Tailwind CSS
- **Estado**: Gestión local optimizada
- **Validación**: Frontend y backend coordinada
