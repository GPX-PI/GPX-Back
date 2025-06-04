# 📊 Diagramas del Sistema GPX

Este directorio contiene todos los diagramas Mermaid que documentan la arquitectura y funcionamiento de tu sistema GPX de gestión de carreras.

## 🗂️ Archivos Incluidos

### Archivos Mermaid (.mmd)

- **01-arquitectura-general.mmd** - Arquitectura completa del sistema
- **02-flujo-autenticacion.mmd** - Flujo de autenticación JWT + OAuth2
- **03-modelo-datos.mmd** - Modelo de datos y relaciones
- **04-flujo-carrera.mmd** - Proceso de una carrera completa
- **05-sistema-permisos.mmd** - Permisos por endpoint (ACTUALIZADO ✅)
- **06-flujo-clasificaciones.mmd** - Generación de clasificaciones
- **07-flujo-oauth2.mmd** - Flujo detallado OAuth2 con Google
- **08-flujo-oauth2-simplificado.mmd** - Versión simplificada OAuth2
- **09-flujo-registro-unificado.mmd** - Registro tradicional + OAuth2
- **10-arquitectura-endpoints-limpia.mmd** - Vista limpia de endpoints
- **11-testing-security.mmd** - Arquitectura de testing con Spring Security (NUEVO ✅)

### Visualizador HTML

- **visualizador.html** - Página web con todos los diagramas renderizados

## 🚀 Cómo Visualizar los Diagramas

### Opción 1: Navegador Web (RECOMENDADO)

1. Abre el archivo `visualizador.html` en cualquier navegador web
2. Verás todos los diagramas renderizados con navegación
3. Puedes hacer clic en los enlaces del menú para navegar

### Opción 2: Editores con Soporte Mermaid

Los siguientes editores pueden mostrar los archivos `.mmd` directamente:

#### Visual Studio Code

1. Instala la extensión "Mermaid Markdown Syntax Highlighting"
2. Instala la extensión "Mermaid Preview"
3. Abre cualquier archivo `.mmd`
4. Usa `Ctrl+Shift+P` → "Mermaid: Preview Diagram"

#### Otros Editores

- **IntelliJ IDEA**: Plugin "Mermaid"
- **Obsidian**: Soporte nativo para Mermaid
- **Notion**: Soporte nativo para bloques Mermaid

### Opción 3: Herramientas Online

Copia el contenido de cualquier archivo `.mmd` en:

- [Mermaid Live Editor](https://mermaid.live/)
- [GitHub Gist](https://gist.github.com/) (renderiza Mermaid automáticamente)

## 📋 Descripción de Cada Diagrama

### 🏗️ Arquitectura General

Muestra las capas del sistema: Frontend → Security → Controllers → Services → Database

### 🔐 Autenticación

Flujo completo desde login hasta autorización de requests con JWT + OAuth2

### 📊 Modelo de Datos

Entidades y relaciones: User, Vehicle, Event, Stage, Category, etc.

### 🏁 Flujo de Carrera

Proceso completo: Crear evento → Inscripciones → Carrera → Clasificaciones

### 🔒 Sistema de Permisos (ACTUALIZADO)

Qué endpoints son públicos, de admin, o mixtos - incluye nuevos endpoints OAuth2

### 📈 Clasificaciones

Cómo se calculan las clasificaciones por etapa y general

### 🔑 OAuth2 con Google

Flujo completo de autenticación con Google, intercambio de tokens y manejo de usuarios

### 🧪 Testing y Seguridad (NUEVO)

Arquitectura de testing con Spring Security, mocking de autenticación y patrones de prueba

## 🆕 Actualizaciones Recientes

### ✅ **05-sistema-permisos.mmd** - Actualizado

- ➕ Nuevos endpoints OAuth2: `/api/users/oauth2/login-url`, `/oauth2/authorization/google`
- ➕ Endpoints de completar perfil: `/api/users/:id/complete-profile`
- ➕ Callbacks OAuth2: `/api/oauth2/success`
- ✏️ Corrección en endpoint registro: `/api/users/simple-register`

### 🆕 **11-testing-security.mmd** - Nuevo Diagrama

- 🧪 Arquitectura de testing con Spring Security
- 🔧 Patrones de mocking para Authentication y SecurityContext
- 🎯 Escenarios de prueba para admin vs usuarios regulares
- 📋 Estado actual de archivos de prueba

## 💡 Consejos

- El archivo `visualizador.html` es la forma más fácil de ver todo
- Los archivos `.mmd` son útiles para editarlos o incluirlos en documentación
- Puedes modificar cualquier diagrama editando el archivo correspondiente
- Los diagramas están optimizados para pantallas grandes

## 🔧 Personalización

Para modificar los diagramas:

1. Edita el archivo `.mmd` correspondiente
2. Si modificas un `.mmd`, copia el contenido actualizado al `visualizador.html`
3. Los colores y estilos se pueden cambiar en la sección `themeVariables` del HTML

---

**¡Disfruta explorando la arquitectura de tu sistema GPX!** 🏆
