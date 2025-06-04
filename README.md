# GPX Tracking Application

## Descripción

Esta es una aplicación Spring Boot para gestionar eventos de carreras, vehículos, usuarios, categorías y resultados de
etapas. Permite registrar y consultar información sobre eventos, vehículos participantes, usuarios administradores,
categorías de vehículos y los resultados de cada etapa de la carrera.

## Características

- Gestión de usuarios (CRUD).
- Gestión de vehículos (CRUD).
- Gestión de categorías de vehículos (CRUD).
- Gestión de eventos (CRUD).
- Gestión de resultados de etapas (CRUD).
- Cálculo del tiempo total de un vehículo en un evento.
- Obtención de la clasificación por categoría en un evento.
- Obtención de resultados por rango de etapas.

## Endpoints

### Usuarios

- `GET /api/users`: Obtiene todos los usuarios.
- `GET /api/users/{id}`: Obtiene un usuario por ID.
- `POST /api/users/simple-register`: Crea un nuevo usuario con registro simplificado.
- `POST /api/users/login`: Autentica un usuario y devuelve JWT token.
- `GET /api/users/check-email`: Verifica si un email ya está registrado.
- `PUT /api/users/{id}`: Actualiza un usuario existente (con soporte para archivos).
- `POST /api/users/{id}/complete-profile`: Completa el perfil básico del usuario.
- `GET /api/users/admins`: Obtiene todos los usuarios administradores.
- `GET /api/users/oauth2/login-url`: Obtiene la URL para iniciar login con Google.

#### Autenticación OAuth2

- `GET /oauth2/authorization/google`: Inicia el flujo de autenticación con Google.
- `GET /api/oauth2/success`: Callback para procesar autenticación exitosa.
- `GET /api/oauth2/profile-status`: Verifica el estado del perfil del usuario.

### Vehículos

- `GET /api/vehicles`: Obtiene todos los vehículos.
- `GET /api/vehicles/{id}`: Obtiene un vehículo por ID.
- `POST /api/vehicles`: Crea un nuevo vehículo.
- `PUT /api/vehicles/{id}`: Actualiza un vehículo existente.
- `DELETE /api/vehicles/{id}`: Elimina un vehículo.
- `GET /api/vehicles/bycategory?categoryId={categoryId}`: Obtiene vehículos por categoría.
- `GET /api/vehicles/byuser?userId={userId}`: Obtiene vehículos por usuario.

### Categorías

- `GET /api/categories`: Obtiene todas las categorías.
- `GET /api/categories/{id}`: Obtiene una categoría por ID.
- `POST /api/categories`: Crea una nueva categoría.
- `PUT /api/categories/{id}`: Actualiza una categoría existente.
- `DELETE /api/categories/{id}`: Elimina una categoría.

### Eventos

- `GET /api/events`: Obtiene todos los eventos.
- `GET /api/events/{id}`: Obtiene un evento por ID.
- `POST /api/events`: Crea un nuevo evento.
- `PUT /api/events/{id}`: Actualiza un evento existente.
- `DELETE /api/events/{id}`: Elimina un evento.
- `GET /api/events/bydate?date={date}`: Obtiene eventos por fecha.
  \-`GET /api/events/{eventId}/categories`: Obtiene las categorías asociadas a un evento.

### Resultados de Etapas

- `GET /api/stageresults`: Obtiene todos los resultados de etapas.
- `GET /api/stageresults/{id}`: Obtiene un resultado de etapa por ID.
- `POST /api/stageresults`: Crea un nuevo resultado de etapa.
- `PUT /api/stageresults/{id}`: Actualiza un resultado de etapa existente.
- `DELETE /api/stageresults/{id}`: Elimina un resultado de etapa.
- `GET /api/stageresults/clasificacion?eventId={eventId}&categoryId={categoryId}`: Obtiene la clasificación por categoría en un evento.
- `GET /api/stageresults/clasificacion?eventId={eventId}&stageNumber={stageNumber}`: Obtiene la clasificación por etapa en un evento.
- `GET /api/stageresults/clasificacion?eventId={eventId}`: Obtiene la clasificación general de un evento.
- `GET /api/stageresults/bystagerange?eventId={eventId}&stageStart={stageStart}&stageEnd={stageEnd}`: Obtiene resultados por rango de etapas (excluye etapas neutralizadas).
- `POST /api/stageresults/update-elapsed-times/{eventId}`: Calcula y actualiza los tiempos de cada etapa para un evento.
- `PUT /api/stageresults/penalizacion/{id}?penaltyWaypoint={PT30S}&penaltySpeed={PT10S}&discountClaim={PT5S}`: Aplica penalizaciones o descuentos a un resultado de etapa (los parámetros son opcionales y en formato ISO-8601 de duración, por ejemplo, `PT30S` para 30 segundos).

### Eventos y Vehículos

- `GET /api/eventvehicles`: Obtiene todos los registros de eventos y vehículos.
- `GET /api/eventvehicles/{id}`: Obtiene un registro de evento y vehículo por ID.
- `GET /api/eventvehicles/byevent/{eventId}`: Obtiene vehículos por ID de evento.

### Etapas

- `GET /api/stages`: Obtiene todas las etapas.
- `GET /api/stages/{id}`: Obtiene una etapa por ID.
- `GET /api/stages/byevent/{eventId}`: Obtiene etapas por ID de evento.

### Eventos y Categorías

- `GET /api/eventcategories`: Obtiene todos los registros de eventos y categorías.
- `GET /api/eventcategories/{id}`: Obtiene un registro de evento y categoría por ID.

## Configuración

1. Clonar el repositorio.
2. Configurar la base de datos en `src/main/resources/application.properties`.
3. Ejecutar la aplicación con Maven: `mvn spring-boot:run`.

## Dependencias

- Spring Boot
- Spring Data JPA
- H2 Database (para desarrollo)
- JUnit y Mockito (para pruebas)

## Pruebas

Las pruebas unitarias se encuentran en el directorio `src/test/java/com/udea/GPX`. Para ejecutar las pruebas, utiliza el
siguiente comando de Maven:

```bash
mvn test
```

## 🔐 Autenticación

El sistema soporta dos métodos de autenticación con flujos unificados:

### 1. Autenticación Tradicional (Simplificada)

- **Registro**: Solo firstName, lastName, email y password
- **Login**: Email y contraseña con verificación de perfil completo
- **JWT**: Para mantener sesión

### 2. Autenticación OAuth2 con Google

- **Registro**: Login rápido con cuenta de Google (datos automáticos)
- **Login**: Un clic para autenticarse
- **JWT**: Mismo sistema de tokens que método tradicional

#### 📋 Flujo Unificado para Ambos Métodos:

1. **Registro/Login** → Usuario se autentica (cualquier método)
2. **Token JWT** → Se genera inmediatamente
3. **Verificación de perfil** → Se comprueba si tiene datos esenciales
4. **Redirección inteligente**:
   - Si perfil completo → Dashboard
   - Si perfil incompleto → Completar datos (identification, phone, role)

#### ✨ Beneficios del Sistema Unificado:

- **Consistencia**: Misma experiencia sin importar el método de registro
- **Simplicidad**: Solo datos esenciales al inicio
- **Flexibilidad**: Usuarios pueden completar perfil cuando quieran
- **Verificación**: Emails validados (especialmente con Google)

#### 🔗 Endpoints Principales:

- `POST /api/users/simple-register` - Registro tradicional simplificado
- `POST /api/users/login` - Login tradicional con estado de perfil
- `GET /api/users/check-email` - Verificar disponibilidad de email
- `POST /api/users/{id}/complete-profile` - Completar datos esenciales
- `GET /oauth2/authorization/google` - Iniciar flujo Google OAuth2
