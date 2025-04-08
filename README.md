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
- `POST /api/users`: Crea un nuevo usuario.
- `PUT /api/users/{id}`: Actualiza un usuario existente.
- `DELETE /api/users/{id}`: Elimina un usuario.
- `GET /api/users/admins`: Obtiene todos los usuarios administradores.

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
- `GET /api/stageresults/bycategory?categoryId={categoryId}&stageStart={stageStart}&stageEnd={stageEnd}`: Obtiene
  resultados por categoría y rango de etapas.
- `GET /api/stageresults/tiempo-total?vehicleId={vehicleId}&eventId={eventId}`: Calcula el tiempo total de un vehículo
  en un evento.
- `GET /api/stageresults/clasificacion?eventId={eventId}&categoryId={categoryId}`: Obtiene la clasificación por
  categoría en un evento.
- `GET /api/stageresults/bystagerange?eventId={eventId}&stageStart={stageStart}&stageEnd={stageEnd}`: Obtiene resultados
  por rango de etapas.

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