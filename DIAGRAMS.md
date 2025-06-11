# 📊 **Diagramas Interactivos - gpx System**

## 🎯 **Opciones de Visualización**

| Opción                   | Descripción                          | Mejor Para            |
| ------------------------ | ------------------------------------ | --------------------- |
| 📄 **Este archivo**      | Diagramas optimizados para GitHub    | Vista general rápida  |
| 🖥️ **GitHub Codespaces** | Presiona `.` en el repo              | Desarrollo/revisión   |
| 🔍 **Click derecho**     | "Abrir imagen en nueva pestaña"      | Zoom nativo navegador |
| 🌐 **Mermaid Live**      | [mermaid.live](https://mermaid.live) | Edición interactiva   |

---

## 🏗️ **1. Arquitectura Completa del Sistema**

### Descripción

Arquitectura MVC completa con todos los componentes, servicios y dependencias externas.

```mermaid
%%{init: {'theme':'base', 'themeVariables': { 'fontSize': '12px'}}}%%
graph TD
    %% CONTROLLERS
    subgraph CONT ["🎯 CONTROLLERS"]
        UC[👥 UserController]
        EC[🏆 EventController]
        SC[🏁 StageController]
        OC[🔐 OAuth2Controller]
        VC[🚗 VehicleController]
        CC[🏷️ CategoryController]
        RC[📊 ResultController]
    end

    %% SERVICES
    subgraph SERV ["⚙️ SERVICES"]
        US[👥 UserService]
        ES[🏆 EventService]
        SS[🏁 StageService]
        OS[🔐 OAuth2Service]
        VS[🚗 VehicleService]
        CS[🏷️ CategoryService]
        RS[📊 ResultService]
        PS[🔒 PasswordService]
    end

    %% REPOSITORIES
    subgraph REPO ["🗄️ REPOSITORIES"]
        UR[👥 UserRepo]
        ER[🏆 EventRepo]
        SR[🏁 StageRepo]
        VR[🚗 VehicleRepo]
        CR[🏷️ CategoryRepo]
        RR[📊 ResultRepo]
    end

    %% ENTITIES
    subgraph ENT ["📊 ENTITIES"]
        UE[👤 User]
        EE[🏆 Event]
        SE[🏁 Stage]
        VE[🚗 Vehicle]
        CE[🏷️ Category]
        RE[📊 Result]
    end

    %% SECURITY
    subgraph SEC ["🔐 SECURITY"]
        JU[🔑 JwtUtil]
        JF[🔍 JwtFilter]
        SC_[🛡️ SecurityConfig]
    end

    %% EXTERNAL
    subgraph EXT ["🌐 EXTERNAL"]
        DB[(🐘 PostgreSQL)]
        GOOGLE[🔵 Google OAuth2]
        FS[📁 File System]
    end

    %% CONNECTIONS CONTROLLERS -> SERVICES
    UC --> US
    EC --> ES
    SC --> SS
    OC --> OS
    VC --> VS
    CC --> CS
    RC --> RS

    %% CONNECTIONS SERVICES -> REPOSITORIES
    US --> UR
    ES --> ER
    SS --> SR
    OS --> UR
    VS --> VR
    CS --> CR
    RS --> RR

    %% CONNECTIONS REPOSITORIES -> ENTITIES
    UR --> UE
    ER --> EE
    SR --> SE
    VR --> VE
    CR --> CE
    RR --> RE

    %% CONNECTIONS TO EXTERNAL
    UR --> DB
    ER --> DB
    SR --> DB
    VR --> DB
    CR --> DB
    RR --> DB

    OS --> GOOGLE
    ES --> FS
    US --> FS

    %% SECURITY CONNECTIONS
    JF --> JU
    UC --> JF
    EC --> JF
    VC --> JF

    %% STYLING
    classDef controller fill:#e3f2fd,stroke:#1976d2,stroke-width:2px
    classDef service fill:#f3e5f5,stroke:#7b1fa2,stroke-width:2px
    classDef repository fill:#e8f5e8,stroke:#388e3c,stroke-width:2px
    classDef entity fill:#fff3e0,stroke:#f57c00,stroke-width:2px
    classDef security fill:#ffebee,stroke:#d32f2f,stroke-width:2px
    classDef external fill:#f1f8e9,stroke:#689f38,stroke-width:3px

    class UC,EC,SC,OC,VC,CC,RC controller
    class US,ES,SS,OS,VS,CS,RS,PS service
    class UR,ER,SR,VR,CR,RR repository
    class UE,EE,SE,VE,CE,RE entity
    class JU,JF,SC_ security
    class DB,GOOGLE,FS external
```

---

## 🔐 **2. Flujo de Autenticación Dual Completo**

### Descripción

Sistema de autenticación híbrido que soporta tanto login tradicional como OAuth2 con Google.

```mermaid
%%{init: {'theme':'base', 'themeVariables': { 'fontSize': '14px'}}}%%
flowchart TD
    START([🚀 Usuario accede a la aplicación]) --> CHECK_TOKEN{🔍 ¿Tiene token JWT válido?}

    CHECK_TOKEN -->|✅ Sí| VALIDATE_JWT[🔒 JwtRequestFilter<br/>Validar token]
    CHECK_TOKEN -->|❌ No| LOGIN_CHOICE{🚪 Método de autenticación}

    VALIDATE_JWT --> TOKEN_VALID{✅ ¿Token es válido?}
    TOKEN_VALID -->|✅ Sí| EXTRACT_USER[👤 Extraer usuario del token]
    TOKEN_VALID -->|❌ No| CLEAR_AUTH[🧹 Limpiar autenticación] --> LOGIN_CHOICE

    EXTRACT_USER --> CHECK_PERMISSIONS{🛡️ ¿Tiene permisos necesarios?}
    CHECK_PERMISSIONS -->|✅ Sí| ALLOW_ACCESS[✅ Permitir acceso]
    CHECK_PERMISSIONS -->|❌ No| FORBIDDEN[🚫 403 Forbidden]

    subgraph "🏠 AUTENTICACIÓN LOCAL"
        LOGIN_CHOICE -->|📧 Email/Password| LOCAL_LOGIN[📝 POST /api/users/login]
        LOCAL_LOGIN --> VALIDATE_CREDS[🔍 UserService<br/>Validar credenciales]
        VALIDATE_CREDS --> CREDS_VALID{✅ ¿Credenciales válidas?}

        CREDS_VALID -->|❌ No| LOGIN_ERROR[❌ Error 401<br/>Credenciales inválidas]
        CREDS_VALID -->|✅ Sí| CHECK_PROFILE[👤 Verificar perfil completo]

        CHECK_PROFILE --> PROFILE_COMPLETE{📋 ¿Perfil completo?}
        PROFILE_COMPLETE -->|❌ No| PROFILE_INCOMPLETE[⚠️ Perfil incompleto<br/>Redirigir a completar]
        PROFILE_COMPLETE -->|✅ Sí| GENERATE_JWT_LOCAL[🔐 JwtUtil<br/>Generar token JWT]
    end

    subgraph "🌐 AUTENTICACIÓN OAUTH2"
        LOGIN_CHOICE -->|🔵 Google OAuth2| OAUTH_INIT[🌐 GET /oauth2/authorization/google]
        OAUTH_INIT --> GOOGLE_REDIRECT[🔄 Redirigir a Google]
        GOOGLE_REDIRECT --> GOOGLE_AUTH[🔐 Usuario se autentica en Google]

        GOOGLE_AUTH --> OAUTH_SUCCESS{✅ ¿Autenticación exitosa?}
        OAUTH_SUCCESS -->|❌ No| OAUTH_ERROR[❌ Error OAuth2<br/>Redirigir a login]
        OAUTH_SUCCESS -->|✅ Sí| OAUTH_CALLBACK[📞 Callback OAuth2Controller]

        OAUTH_CALLBACK --> GET_GOOGLE_PROFILE[👤 Obtener perfil de Google]
        GET_GOOGLE_PROFILE --> USER_EXISTS{🔍 ¿Usuario existe en BD?}

        USER_EXISTS -->|❌ No| CREATE_USER[👤 OAuth2Service<br/>Crear nuevo usuario]
        USER_EXISTS -->|✅ Sí| UPDATE_USER[🔄 Actualizar datos del usuario]

        CREATE_USER --> SET_INCOMPLETE[⚠️ Marcar perfil como incompleto]
        UPDATE_USER --> SET_INCOMPLETE
        SET_INCOMPLETE --> GENERATE_JWT_OAUTH[🔐 JwtUtil<br/>Generar token JWT]
    end

    subgraph "🔐 GENERACIÓN DE TOKENS"
        GENERATE_JWT_LOCAL --> CREATE_CLAIMS[📝 Crear JWT claims<br/>• userId<br/>• username<br/>• authorities<br/>• expiration]
        GENERATE_JWT_OAUTH --> CREATE_CLAIMS

        CREATE_CLAIMS --> SIGN_TOKEN[✍️ Firmar token con secret]
        SIGN_TOKEN --> RETURN_TOKEN[📤 Retornar token + user data]
    end

    subgraph "🏁 RESULTADOS FINALES"
        RETURN_TOKEN --> SUCCESS_LOGIN[✅ Login exitoso<br/>Redirigir a dashboard]
        PROFILE_INCOMPLETE --> REDIRECT_COMPLETE[🔄 Redirigir a<br/>/complete-profile]
        FORBIDDEN --> ERROR_RESPONSE[❌ Respuesta de error]
        LOGIN_ERROR --> ERROR_RESPONSE
        OAUTH_ERROR --> ERROR_RESPONSE

        ALLOW_ACCESS --> PROTECTED_RESOURCE[🔒 Acceso a recurso protegido]
    end

    subgraph "📱 FRONTEND HANDLING"
        SUCCESS_LOGIN --> STORE_TOKEN[💾 Guardar token en localStorage]
        STORE_TOKEN --> UPDATE_CONTEXT[🔄 Actualizar AuthContext]
        UPDATE_CONTEXT --> NAVIGATE_DASHBOARD[🏠 Navegar a dashboard]

        REDIRECT_COMPLETE --> COMPLETE_FORM[📝 Formulario completar perfil]
        COMPLETE_FORM --> SUBMIT_PROFILE[📤 POST /api/users/complete-profile]
        SUBMIT_PROFILE --> SUCCESS_LOGIN
    end

    %% STYLING
    classDef processStyle fill:#e3f2fd,stroke:#1976d2,stroke-width:2px
    classDef decisionStyle fill:#fff3e0,stroke:#f57c00,stroke-width:2px
    classDef errorStyle fill:#ffebee,stroke:#d32f2f,stroke-width:2px
    classDef successStyle fill:#e8f5e8,stroke:#388e3c,stroke-width:2px
    classDef authStyle fill:#f3e5f5,stroke:#7b1fa2,stroke-width:2px
    classDef frontendStyle fill:#e0f2f1,stroke:#00695c,stroke-width:2px

    class LOCAL_LOGIN,OAUTH_INIT,VALIDATE_JWT,VALIDATE_CREDS,OAUTH_CALLBACK,GET_GOOGLE_PROFILE,CREATE_USER,UPDATE_USER,GENERATE_JWT_LOCAL,GENERATE_JWT_OAUTH,CREATE_CLAIMS,SIGN_TOKEN processStyle
    class CHECK_TOKEN,TOKEN_VALID,CHECK_PERMISSIONS,CREDS_VALID,CHECK_PROFILE,PROFILE_COMPLETE,OAUTH_SUCCESS,USER_EXISTS decisionStyle
    class LOGIN_ERROR,OAUTH_ERROR,FORBIDDEN,ERROR_RESPONSE errorStyle
    class ALLOW_ACCESS,SUCCESS_LOGIN,PROTECTED_RESOURCE successStyle
    class EXTRACT_USER,SET_INCOMPLETE,RETURN_TOKEN authStyle
    class STORE_TOKEN,UPDATE_CONTEXT,NAVIGATE_DASHBOARD,COMPLETE_FORM,SUBMIT_PROFILE frontendStyle
```

---

## 🗄️ **3. Modelo de Base de Datos Detallado**

### Descripción

Esquema completo de la base de datos con todas las relaciones, constraints y tipos de datos.

```mermaid
%%{init: {'theme':'base', 'themeVariables': { 'fontSize': '12px'}}}%%
erDiagram
    USER {
        bigint id PK "🔑 Auto-increment primary key"
        varchar_255 username UK "👤 Unique username"
        varchar_255 email UK "📧 Unique email address"
        varchar_255 password "🔐 BCrypt encrypted password"
        varchar_255 full_name "📝 User's full name"
        varchar_20 phone "📱 Phone number"
        varchar_50 identification "🆔 ID document number"
        varchar_255 emergency_contact "🚨 Emergency contact info"
        varchar_10 blood_type "🩸 Blood type (A+, B-, etc)"
        text allergies "⚠️ Medical allergies"
        text medications "💊 Current medications"
        varchar_255 instagram "📷 Instagram handle"
        varchar_255 facebook "📘 Facebook profile"
        varchar_255 team "🏆 Team/club name"
        varchar_50 auth_provider "🔐 LOCAL or GOOGLE"
        varchar_500 picture "🖼️ Profile picture URL/path"
        varchar_500 insurance "🏥 Medical insurance doc path"
        boolean is_admin "👑 Admin privileges flag"
        timestamp created_at "📅 Record creation timestamp"
        timestamp updated_at "🔄 Last update timestamp"
    }

    VEHICLE {
        bigint id PK "🔑 Auto-increment primary key"
        varchar_255 name "📝 Vehicle display name"
        varchar_100 brand "🏭 Vehicle manufacturer"
        varchar_100 model "🚗 Vehicle model"
        varchar_4 year "📅 Manufacturing year"
        varchar_50 color "🎨 Vehicle color"
        varchar_20 license_plate UK "🔖 Unique license plate"
        varchar_500 soat "📋 SOAT document path"
        varchar_50 type "🏷️ Vehicle type category"
        bigint user_id FK "🔗 Owner user reference"
        bigint category_id FK "🔗 Category reference"
        timestamp created_at "📅 Record creation timestamp"
        timestamp updated_at "🔄 Last update timestamp"
    }

    EVENT {
        bigint id PK "🔑 Auto-increment primary key"
        varchar_255 name "📝 Event name"
        text description "📄 Event description"
        varchar_255 location "📍 Event location"
        timestamp start_date "🚀 Event start date/time"
        timestamp end_date "🏁 Event end date/time"
        varchar_500 picture_url "🖼️ Event image URL/path"
        boolean is_active "✅ Active status flag"
        timestamp created_at "📅 Record creation timestamp"
        timestamp updated_at "🔄 Last update timestamp"
    }

    STAGE {
        bigint id PK "🔑 Auto-increment primary key"
        varchar_255 name "📝 Stage name"
        text description "📄 Stage description"
        varchar_255 location "📍 Stage location"
        timestamp start_time "🚀 Stage start time"
        timestamp end_time "🏁 Stage end time"
        boolean is_neutralized "⚠️ Neutralization flag"
        integer stage_order "🔢 Stage sequence number"
        bigint event_id FK "🔗 Parent event reference"
        timestamp created_at "📅 Record creation timestamp"
        timestamp updated_at "🔄 Last update timestamp"
    }

    CATEGORY {
        bigint id PK "🔑 Auto-increment primary key"
        varchar_100 name UK "📝 Unique category name"
        text description "📄 Category description"
        varchar_7 color "🎨 Hex color code (#FF5722)"
        boolean is_active "✅ Active status flag"
        timestamp created_at "📅 Record creation timestamp"
        timestamp updated_at "🔄 Last update timestamp"
    }

    STAGE_RESULT {
        bigint id PK "🔑 Auto-increment primary key"
        bigint vehicle_id FK "🔗 Participant vehicle"
        bigint stage_id FK "🔗 Stage reference"
        timestamp start_time "🚀 Stage start timestamp"
        timestamp end_time "🏁 Stage finish timestamp"
        bigint elapsed_time_nanos "⏱️ Raw elapsed time (nanoseconds)"
        bigint penalty_waypoint_nanos "🚩 Waypoint penalty (nanoseconds)"
        bigint penalty_speed_nanos "🏃 Speed penalty (nanoseconds)"
        bigint discount_claim_nanos "💰 Claim discount (nanoseconds)"
        bigint final_time_nanos "🏆 Final calculated time (nanoseconds)"
        timestamp created_at "📅 Record creation timestamp"
        timestamp updated_at "🔄 Last update timestamp"
    }

    EVENT_VEHICLE {
        bigint id PK "🔑 Auto-increment primary key"
        bigint event_id FK "🔗 Event reference"
        bigint vehicle_id FK "🔗 Vehicle reference"
        timestamp registration_date "📅 Registration timestamp"
        varchar_50 status "📊 Registration status"
    }

    EVENT_CATEGORY {
        bigint id PK "🔑 Auto-increment primary key"
        bigint event_id FK "🔗 Event reference"
        bigint category_id FK "🔗 Category reference"
        timestamp assigned_date "📅 Assignment timestamp"
    }

    %% RELATIONSHIPS WITH CARDINALITY
    USER ||--o{ VEHICLE : "owns (1:N)"
    CATEGORY ||--o{ VEHICLE : "classifies (1:N)"
    EVENT ||--o{ STAGE : "contains (1:N)"
    EVENT ||--o{ EVENT_VEHICLE : "registers (1:N)"
    EVENT ||--o{ EVENT_CATEGORY : "includes (1:N)"
    VEHICLE ||--o{ EVENT_VEHICLE : "participates (1:N)"
    VEHICLE ||--o{ STAGE_RESULT : "achieves (1:N)"
    CATEGORY ||--o{ EVENT_CATEGORY : "applies_to (1:N)"
    STAGE ||--o{ STAGE_RESULT : "produces (1:N)"

    %% INDEXES AND CONSTRAINTS (represented as comments)
    USER {
        string INDEXES "📊 idx_user_email, idx_user_username, idx_user_auth_provider"
        string CONSTRAINTS "🔒 email UNIQUE, username UNIQUE, email NOT NULL"
    }

    VEHICLE {
        string INDEXES "📊 idx_vehicle_user_id, idx_vehicle_category_id, idx_vehicle_plate"
        string CONSTRAINTS "🔒 license_plate UNIQUE, user_id FK CASCADE"
    }

    STAGE_RESULT {
        string INDEXES "📊 idx_result_vehicle_stage, idx_result_final_time"
        string CONSTRAINTS "🔒 UNIQUE(vehicle_id, stage_id)"
    }
```

---

## 📁 **4. Sistema de Archivos y Validaciones**

### Descripción

Flujo completo de gestión de archivos con múltiples capas de validación y seguridad.

```mermaid
%%{init: {'theme':'base', 'themeVariables': { 'fontSize': '10px'}}}%%
flowchart TD
    START([📤 Cliente selecciona archivo]) --> FRONTEND_CHECK{🔍 Validación Frontend}

    FRONTEND_CHECK -->|❌ Falla| FRONTEND_ERROR[❌ Error Frontend]
    FRONTEND_CHECK -->|✅ Pasa| CREATE_FORM[📦 Crear FormData]

    CREATE_FORM --> SEND_REQUEST[📡 Enviar HTTP Request]

    %% VALIDACIONES DE SEGURIDAD
    subgraph VALIDATION ["🛡️ VALIDACIONES"]
        SEND_REQUEST --> AUTH_CHECK{🔐 Autenticado?}
        AUTH_CHECK -->|❌ No| AUTH_ERROR[🚫 401 Unauthorized]
        AUTH_CHECK -->|✅ Sí| PERMISSION_CHECK{👑 Permisos?}

        PERMISSION_CHECK -->|❌ No| PERMISSION_ERROR[🚫 403 Forbidden]
        PERMISSION_CHECK -->|✅ Sí| RECEIVE_FILE[📥 Recibir archivo]

        RECEIVE_FILE --> VALIDATE_EMPTY{📄 ¿Vacío?}
        VALIDATE_EMPTY -->|✅ Sí| EMPTY_ERROR[❌ Archivo vacío]
        VALIDATE_EMPTY -->|❌ No| VALIDATE_SIZE{📏 ¿Tamaño OK?}

        VALIDATE_SIZE -->|❌ No| SIZE_ERROR[❌ Muy grande]
        VALIDATE_SIZE -->|✅ Sí| VALIDATE_TYPE{🎭 ¿Tipo válido?}

        VALIDATE_TYPE -->|❌ No| TYPE_ERROR[❌ Tipo inválido]
        VALIDATE_TYPE -->|✅ Sí| VALIDATE_EXTENSION{🔧 ¿Extensión OK?}

        VALIDATE_EXTENSION -->|❌ No| EXT_ERROR[❌ Extensión inválida]
        VALIDATE_EXTENSION -->|✅ Sí| SECURITY_SCAN{🔍 ¿Seguro?}

        SECURITY_SCAN -->|❌ Sospechoso| SECURITY_ERROR[❌ Contenido sospechoso]
        SECURITY_SCAN -->|✅ Seguro| PROCEED_SAVE[✅ Proceder]
    end

    %% PROCESAMIENTO
    subgraph PROCESSING ["💾 PROCESAMIENTO"]
        PROCEED_SAVE --> GENERATE_NAME[🎲 Generar nombre único]
        GENERATE_NAME --> DETERMINE_PATH{📁 Ruta destino}

        DETERMINE_PATH -->|👤 Usuario| USER_PATH[📂 uploads/users/]
        DETERMINE_PATH -->|🏆 Evento| EVENT_PATH[📂 uploads/events/]
        DETERMINE_PATH -->|🏥 Seguro| INSURANCE_PATH[📂 uploads/insurance/]

        USER_PATH --> CREATE_DIRS[📁 Crear directorios]
        EVENT_PATH --> CREATE_DIRS
        INSURANCE_PATH --> CREATE_DIRS

        CREATE_DIRS --> SAVE_TO_DISK[💾 Guardar en disco]
        SAVE_TO_DISK --> SAVE_SUCCESS{✅ ¿Guardado OK?}

        SAVE_SUCCESS -->|❌ No| SAVE_ERROR[❌ Error al guardar]
        SAVE_SUCCESS -->|✅ Sí| GENERATE_URL[🔗 Generar URL]
    end

    %% BASE DE DATOS
    subgraph DATABASE ["🔄 BASE DE DATOS"]
        GENERATE_URL --> GET_CURRENT_FILE[🔍 Obtener archivo actual]
        GET_CURRENT_FILE --> UPDATE_ENTITY[🔄 Actualizar entidad]
        UPDATE_ENTITY --> DB_SUCCESS{💾 ¿BD OK?}

        DB_SUCCESS -->|❌ No| DB_ERROR[❌ Error BD]
        DB_SUCCESS -->|✅ Sí| DELETE_OLD{🗑️ ¿Eliminar anterior?}

        DELETE_OLD -->|✅ Sí| CLEANUP_OLD[🧹 Limpiar anterior]
        DELETE_OLD -->|❌ No| SUCCESS_RESPONSE[✅ Respuesta exitosa]
        CLEANUP_OLD --> SUCCESS_RESPONSE
    end

    %% RESPUESTAS
    subgraph RESPONSES ["📊 RESPUESTAS"]
        SUCCESS_RESPONSE --> LOG_SUCCESS[📝 Log éxito]

        FRONTEND_ERROR --> LOG_ERROR_RESP[📝 Log error]
        AUTH_ERROR --> LOG_ERROR_RESP
        PERMISSION_ERROR --> LOG_ERROR_RESP
        EMPTY_ERROR --> LOG_ERROR_RESP
        SIZE_ERROR --> LOG_ERROR_RESP
        TYPE_ERROR --> LOG_ERROR_RESP
        EXT_ERROR --> LOG_ERROR_RESP
        SECURITY_ERROR --> LOG_ERROR_RESP
        SAVE_ERROR --> LOG_ERROR_RESP
        DB_ERROR --> LOG_ERROR_RESP

        LOG_SUCCESS --> RETURN_SUCCESS[📤 Retornar éxito]
        LOG_ERROR_RESP --> RETURN_ERROR[📤 Retornar error]
    end

    %% LIMPIEZA
    subgraph CLEANUP ["🧹 LIMPIEZA AUTOMÁTICA"]
        CLEANUP_JOB[⏰ Tarea programada] --> FIND_ORPHANS[🔍 Buscar huérfanos]
        FIND_ORPHANS --> DELETE_ORPHANS[🗑️ Eliminar antiguos]
        DELETE_ORPHANS --> LOG_CLEANUP[📝 Log limpieza]
    end

    %% STYLING
    classDef process fill:#e3f2fd,stroke:#1976d2,stroke-width:2px
    classDef validation fill:#f3e5f5,stroke:#7b1fa2,stroke-width:2px
    classDef decision fill:#fff3e0,stroke:#f57c00,stroke-width:2px
    classDef error fill:#ffebee,stroke:#d32f2f,stroke-width:2px
    classDef success fill:#e8f5e8,stroke:#388e3c,stroke-width:2px
    classDef path fill:#e0f2f1,stroke:#00695c,stroke-width:2px

    class CREATE_FORM,SEND_REQUEST,RECEIVE_FILE,GENERATE_NAME,CREATE_DIRS,SAVE_TO_DISK,GENERATE_URL,GET_CURRENT_FILE,UPDATE_ENTITY,CLEANUP_OLD process
    class AUTH_CHECK,PERMISSION_CHECK,VALIDATE_EMPTY,VALIDATE_SIZE,VALIDATE_TYPE,VALIDATE_EXTENSION,SECURITY_SCAN,DETERMINE_PATH,SAVE_SUCCESS,DB_SUCCESS,DELETE_OLD validation
    class FRONTEND_CHECK,PROCEED_SAVE decision
    class FRONTEND_ERROR,AUTH_ERROR,PERMISSION_ERROR,EMPTY_ERROR,SIZE_ERROR,TYPE_ERROR,EXT_ERROR,SECURITY_ERROR,SAVE_ERROR,DB_ERROR,RETURN_ERROR error
    class SUCCESS_RESPONSE,RETURN_SUCCESS,LOG_SUCCESS success
    class USER_PATH,EVENT_PATH,INSURANCE_PATH path
```

---

## 🧪 **5. Arquitectura de Testing Completa**

### Descripción

Estrategia integral de testing con múltiples capas: unitarios, integración, seguridad y end-to-end.

```mermaid
%%{init: {'theme':'base', 'themeVariables': { 'fontSize': '10px'}}}%%
graph LR
    %% TESTING PYRAMID
    subgraph PYRAMID ["🧪 TESTING PYRAMID"]
        direction TB
        E2E_LAYER[🌐 E2E TESTS 10%]
        INT_LAYER[🔗 INTEGRATION 20%]
        UNIT_LAYER[🔬 UNIT TESTS 70%]

        E2E_LAYER --- INT_LAYER
        INT_LAYER --- UNIT_LAYER
    end

    %% UNIT TESTS
    subgraph UNIT_TESTS ["🔬 UNIT TESTS"]
        UT1[👥 UserController]
        UT2[🏆 EventController]
        UT3[🚗 VehicleController]
        UT4[📊 ResultController]
        UT5[🔐 OAuth2Controller]
    end

    %% INTEGRATION TESTS
    subgraph INT_TESTS ["🔗 INTEGRATION"]
        IT1[📊 Repository]
        IT2[🔐 Security]
        IT3[🗄️ Database]
    end

    %% E2E TESTS
    subgraph E2E_TESTS ["🌐 E2E"]
        E2E1[🔄 AuthFlow]
        E2E2[📁 FileUpload]
    end

    %% EXECUTION FLOW
    subgraph FLOW ["🔍 EXECUTION"]
        START([🚀 Start])
        UNIT_RUN[🔬 Unit]
        INT_RUN[🔗 Integration]
        E2E_RUN[🌐 E2E]
        COVERAGE[📊 Coverage]
        SUCCESS[✅ Success]

        START --> UNIT_RUN
        UNIT_RUN --> INT_RUN
        INT_RUN --> E2E_RUN
        E2E_RUN --> COVERAGE
        COVERAGE --> SUCCESS
    end

    %% SUPPORT
    subgraph SUPPORT ["🎯 SUPPORT"]
        TC[⚙️ TestConfig]
        TF[🏭 DataFactory]
        TU[🛠️ TestUtils]
        SEC[🛡️ Security]
    end

    %% REPORTING
    subgraph REPORTS ["📊 REPORTS"]
        JACOCO[📈 JaCoCo]
        SONAR[🧹 SonarQube]
        CI[🚀 GitHub Actions]
    end

    %% CONNECTIONS
    PYRAMID --> FLOW

    UNIT_RUN --> UNIT_TESTS
    INT_RUN --> INT_TESTS
    E2E_RUN --> E2E_TESTS

    UNIT_TESTS --> TC
    INT_TESTS --> SEC
    E2E_TESTS --> TF

    SUCCESS --> REPORTS

    %% STYLING
    classDef pyramid fill:#f0f8ff,stroke:#4169e1,stroke-width:2px
    classDef unit fill:#e3f2fd,stroke:#1976d2,stroke-width:2px
    classDef integration fill:#f3e5f5,stroke:#7b1fa2,stroke-width:2px
    classDef e2e fill:#e8f5e8,stroke:#388e3c,stroke-width:2px
    classDef flow fill:#e0f2f1,stroke:#00695c,stroke-width:2px
    classDef support fill:#fff3e0,stroke:#f57c00,stroke-width:2px
    classDef report fill:#fafafa,stroke:#616161,stroke-width:2px

    class PYRAMID,E2E_LAYER,INT_LAYER,UNIT_LAYER pyramid
    class UNIT_TESTS,UT1,UT2,UT3,UT4,UT5 unit
    class INT_TESTS,IT1,IT2,IT3 integration
    class E2E_TESTS,E2E1,E2E2 e2e
    class FLOW,START,UNIT_RUN,INT_RUN,E2E_RUN,COVERAGE,SUCCESS flow
    class SUPPORT,TC,TF,TU,SEC support
    class REPORTS,JACOCO,SONAR,CI report
```

---

## 🔄 **6. Flujo de Registro Unificado**

### Descripción

Sistema de registro que unifica tanto el método tradicional (email/contraseña) como OAuth2 con Google, ambos dirigiendo al mismo flujo de completar perfil.

```mermaid
%%{init: {'theme':'base', 'themeVariables': { 'fontSize': '11px'}}}%%
sequenceDiagram
    participant U as Usuario
    participant F as Frontend
    participant B as Backend
    participant G as Google
    participant DB as Base de Datos

    Note over U,DB: Registro Unificado - Ambos métodos convergen

    U->>F: Elige método de registro

    alt Registro Email/Contraseña
        U->>F: Datos: firstName, lastName, email, password
        F->>B: POST /api/users/simple-register
        B->>DB: Verificar email único
        alt Email existe
            DB-->>B: Email encontrado
            B-->>F: Error: "Email ya registrado"
            F-->>U: Mostrar error
        else Email disponible
            B->>B: Crear usuario básico
            Note right of B: firstName, lastName, email, password, authProvider=LOCAL
            B->>DB: Guardar usuario incompleto
            DB-->>B: Usuario creado
            B->>B: Generar JWT token
            B-->>F: {token, userId, profileComplete=false}
        end
    else Registro Google OAuth2
        U->>F: Clic "Registrarse con Google"
        F->>B: Flujo OAuth2 Google
        B->>G: Solicitar autorización
        G-->>U: Pantalla consentimiento
        U->>G: Autorizar permisos
        G->>B: Callback con datos
        B->>DB: Verificar/crear usuario
        B->>B: Procesar datos Google
        Note right of B: firstName, lastName, email, picture, authProvider=GOOGLE
        B->>DB: Guardar usuario incompleto
        B->>B: Generar JWT token
        B->>F: Redirigir con token
    end

    Note over U,DB: Flujo común - Completar perfil
    F->>F: Evaluar profileComplete=false
    F->>F: Redirigir a completar perfil
    F-->>U: Formulario datos esenciales

    U->>F: Llenar: identification, phone, role
    F->>B: POST /api/users/{id}/complete-profile
    B->>DB: Actualizar usuario completo
    DB-->>B: Usuario completado
    B-->>F: Perfil completado
    F->>F: Redirigir a dashboard
    F-->>U: Acceso completo aplicación

    Note over U,DB: Login posterior - Ambos métodos
    U->>F: Iniciar sesión

    alt Login tradicional
        F->>B: POST /api/users/login (email, password)
    else Login OAuth2
        F->>B: Flujo OAuth2 Google
    end

    B->>DB: Verificar usuario
    DB-->>B: Usuario encontrado
    B->>B: Verificar perfil completo
    B-->>F: {token, profileComplete, datos}

    alt Perfil completo
        F-->>U: Acceso directo dashboard
    else Perfil incompleto
        F-->>U: Redirigir completar perfil
    end
```

---

## 🌐 **7. Flujo OAuth2 Google Detallado**

### Descripción

Implementación completa del flujo OAuth2 con Google desde el GOOGLE_OAUTH_SETUP.md, mostrando cada paso del proceso de autenticación.

```mermaid
%%{init: {'theme':'base', 'themeVariables': { 'fontSize': '11px'}}}%%
sequenceDiagram
    participant U as Usuario
    participant F as Frontend
    participant B as Backend
    participant G as Google OAuth2
    participant DB as Base de Datos
    participant JWT as JWT Service

    Note over U,DB: Inicio flujo OAuth2
    U->>F: Clic "Iniciar sesión con Google"
    F->>B: GET /api/users/oauth2/login-url
    B-->>F: {loginUrl: "/oauth2/authorization/google"}

    Note over U,DB: Redirección a Google
    F->>B: Redirige a /oauth2/authorization/google
    B->>G: Redirige con client_id y scopes
    G-->>U: Muestra pantalla consentimiento

    Note over U,DB: Autorización usuario
    U->>G: Concede permisos (email, profile)
    G->>B: Callback: /login/oauth2/code/google?code=AUTH_CODE

    Note over U,DB: Intercambio código por token
    B->>G: POST /token (code, client_id, client_secret)
    G-->>B: Access token + User info

    Note over U,DB: Procesamiento usuario
    B->>B: OAuth2Service.processOAuth2User()
    B->>DB: Buscar usuario por email

    alt Usuario existente
        DB-->>B: Usuario encontrado
        B->>B: Actualizar googleId si necesario
        B->>DB: Guardar cambios
    else Usuario nuevo
        B->>B: Crear usuario desde info Google
        B->>DB: Guardar nuevo usuario
        DB-->>B: Usuario creado
    end

    Note over U,DB: Generación JWT y redirección
    B->>JWT: generateToken(userId, isAdmin)
    JWT-->>B: JWT token
    B->>F: Redirige a frontend con parámetros
    Note right of B: URL: /oauth2/redirect?token=JWT&userId=123&admin=false&provider=google

    Note over U,DB: Manejo en frontend
    F->>F: Extraer token de URL
    F->>F: Guardar token en localStorage
    F->>F: Establecer estado autenticación
    F-->>U: Usuario autenticado y redirigido

    Note over U,DB: Uso token en requests posteriores
    U->>F: Navegar a página protegida
    F->>B: Request con Authorization: Bearer JWT
    B->>B: JwtRequestFilter valida token
    B->>DB: Buscar usuario por ID del token
    DB-->>B: Usuario válido
    B-->>F: Respuesta autorizada
    F-->>U: Contenido mostrado
```

---

## 📊 **8. Flujo Completo de una Carrera**

### Descripción

Proceso completo desde la creación de un evento de carreras hasta la generación de clasificaciones finales, incluyendo todas las interacciones entre actores.

```mermaid
%%{init: {'theme':'base', 'themeVariables': { 'fontSize': '11px'}}}%%
flowchart TD
    START([🏁 Iniciar nueva carrera]) --> ADMIN_CREATE[👑 Admin crea Evento]

    ADMIN_CREATE --> CONFIG_EVENT[⚙️ Configurar evento]
    CONFIG_EVENT --> ADD_STAGES[🏁 Definir Etapas]
    ADD_STAGES --> ASSIGN_CATEGORIES[🏷️ Asignar Categorías]

    ASSIGN_CATEGORIES --> OPEN_REGISTRATION[📢 Abrir registro]

    subgraph REGISTRATION ["📝 PROCESO DE INSCRIPCIÓN"]
        OPEN_REGISTRATION --> USER_REGISTER[👤 Usuarios se registran]
        USER_REGISTER --> ADD_VEHICLES[🚗 Registrar vehículos]
        ADD_VEHICLES --> VEHICLE_INSCRIPTION[📋 Inscribir vehículos al evento]
        VEHICLE_INSCRIPTION --> VERIFY_DOCS[📄 Verificar documentos]
    end

    VERIFY_DOCS --> CLOSE_REGISTRATION[🔒 Cerrar registro]
    CLOSE_REGISTRATION --> EVENT_START[🚀 Inicio del evento]

    subgraph RACE_EXECUTION ["🏁 EJECUCIÓN DE CARRERA"]
        EVENT_START --> STAGE_LOOP[🔄 Por cada etapa]
        STAGE_LOOP --> RECORD_TIMES[⏱️ Registrar tiempos]
        RECORD_TIMES --> CHECK_PENALTIES{⚠️ ¿Penalizaciones?}

        CHECK_PENALTIES -->|✅ Sí| APPLY_PENALTIES[📝 Aplicar penalizaciones]
        CHECK_PENALTIES -->|❌ No| CALCULATE_RESULTS[📊 Calcular resultados]
        APPLY_PENALTIES --> CALCULATE_RESULTS

        CALCULATE_RESULTS --> STAGE_RANKING[🏆 Clasificación etapa]
        STAGE_RANKING --> MORE_STAGES{🔄 ¿Más etapas?}
        MORE_STAGES -->|✅ Sí| STAGE_LOOP
        MORE_STAGES -->|❌ No| FINAL_CALCULATION[🎯 Cálculo final]
    end

    FINAL_CALCULATION --> GENERATE_GENERAL[🏆 Clasificación general]
    GENERATE_GENERAL --> PUBLISH_RESULTS[📢 Publicar resultados]
    PUBLISH_RESULTS --> EVENT_END[🏁 Finalizar evento]

    subgraph RESULTS_MANAGEMENT ["📊 GESTIÓN DE RESULTADOS"]
        PUBLISH_RESULTS --> VIEW_BY_STAGE[👁️ Ver por etapa]
        PUBLISH_RESULTS --> VIEW_BY_CATEGORY[👁️ Ver por categoría]
        PUBLISH_RESULTS --> VIEW_GENERAL[👁️ Ver general]
        PUBLISH_RESULTS --> EXPORT_DATA[📤 Exportar datos]
    end

    %% STYLING
    classDef admin fill:#ffebee,stroke:#d32f2f,stroke-width:2px
    classDef user fill:#e3f2fd,stroke:#1976d2,stroke-width:2px
    classDef process fill:#f3e5f5,stroke:#7b1fa2,stroke-width:2px
    classDef decision fill:#fff3e0,stroke:#f57c00,stroke-width:2px
    classDef success fill:#e8f5e8,stroke:#388e3c,stroke-width:2px

    class ADMIN_CREATE,CONFIG_EVENT,ADD_STAGES,ASSIGN_CATEGORIES,APPLY_PENALTIES admin
    class USER_REGISTER,ADD_VEHICLES,VEHICLE_INSCRIPTION user
    class RECORD_TIMES,CALCULATE_RESULTS,FINAL_CALCULATION,GENERATE_GENERAL process
    class CHECK_PENALTIES,MORE_STAGES decision
    class EVENT_END,PUBLISH_RESULTS success
```

---

## 🔒 **9. Sistema de Permisos por Endpoint**

### Descripción

Matriz completa de permisos mostrando qué endpoints son públicos, requieren autenticación, o necesitan privilegios de administrador.

```mermaid
%%{init: {'theme':'base', 'themeVariables': { 'fontSize': '14px'}}}%%
flowchart TB
    START([🔒 Control de Acceso]) --> CHECK_AUTH{🔍 ¿Token presente?}

    CHECK_AUTH -->|❌ No| PUBLIC_ENDPOINTS[🌐 ENDPOINTS PÚBLICOS<br/>✅ GET /api/categories<br/>✅ POST /api/users/login<br/>✅ POST /api/users/simple-register<br/>✅ GET /api/events/current<br/>✅ GET /api/events/past<br/>✅ GET /oauth2/authorization/google<br/>✅ GET /api/oauth2/success<br/>✅ GET /api/users/check-email]

    CHECK_AUTH -->|✅ Sí| VALIDATE_TOKEN[🔐 Validar JWT Token]

    VALIDATE_TOKEN -->|❌ Inválido| ACCESS_DENIED[❌ Acceso Denegado]
    VALIDATE_TOKEN -->|✅ Válido| CHECK_ROLE{👑 ¿Es Admin?}

    CHECK_ROLE -->|❌ Usuario Normal| USER_ENDPOINTS[👤 ENDPOINTS USUARIO<br/>✅ GET /api/users/propio<br/>✅ PUT /api/users/propio<br/>✅ GET /api/vehicles propios<br/>✅ POST /api/vehicles<br/>✅ PUT/DELETE /api/vehicles propios<br/>✅ POST /api/users/complete-profile<br/>✅ GET /api/oauth2/profile-status<br/>✅ POST /api/users/logout]

    CHECK_ROLE -->|✅ Admin| ADMIN_ENDPOINTS[👑 ENDPOINTS ADMIN<br/>✅ GET /api/users - Todos<br/>✅ GET /api/users/admins<br/>✅ GET /api/vehicles - Todos<br/>✅ POST/PUT/DELETE /api/events<br/>✅ POST/PUT/DELETE /api/stages<br/>✅ POST/PUT/DELETE /api/stageresults<br/>✅ PUT /api/stageresults/penalizacion<br/>✅ PATCH /api/users/admin]

    ADMIN_ENDPOINTS --> MIXED_ACCESS[🔄 ACCESO MIXTO<br/>🚗 EventVehicles específicos<br/>📊 StageResults consulta<br/>👤 Vehículos por usuario<br/>📁 Archivos propios vs todos]

    PUBLIC_ENDPOINTS --> ACCESS_GRANTED[✅ Acceso Concedido]
    USER_ENDPOINTS --> ACCESS_GRANTED
    MIXED_ACCESS --> ACCESS_GRANTED
    ACCESS_DENIED --> ERROR_RESPONSE[❌ HTTP 401/403]

    %% STYLING
    classDef startStyle fill:#e3f2fd,stroke:#1976d2,stroke-width:3px
    classDef publicStyle fill:#e8f5e8,stroke:#4caf50,stroke-width:3px
    classDef userStyle fill:#fff3e0,stroke:#ff9800,stroke-width:3px
    classDef adminStyle fill:#ffebee,stroke:#f44336,stroke-width:3px
    classDef successStyle fill:#e0f2f1,stroke:#00695c,stroke-width:3px
    classDef errorStyle fill:#fce4ec,stroke:#e91e63,stroke-width:3px

    class START,CHECK_AUTH,VALIDATE_TOKEN,CHECK_ROLE startStyle
    class PUBLIC_ENDPOINTS publicStyle
    class USER_ENDPOINTS,MIXED_ACCESS userStyle
    class ADMIN_ENDPOINTS adminStyle
    class ACCESS_GRANTED successStyle
    class ACCESS_DENIED,ERROR_RESPONSE errorStyle
```

---

## 📈 **10. Flujo de Generación de Clasificaciones**

### Descripción

Proceso detallado de cómo se calculan y generan las clasificaciones por etapa, categoría y general, incluyendo el manejo de penalizaciones.

```mermaid
%%{init: {'theme':'base', 'themeVariables': { 'fontSize': '11px'}}}%%
sequenceDiagram
    participant A as Admin
    participant SRC as StageResultController
    participant SRS as StageResultService
    participant DB as Database
    participant CALC as CalculationEngine

    Note over A,DB: Registrar resultado de etapa
    A->>SRC: POST /api/stageresults
    SRC->>SRS: createStageResult(resultData)
    SRS->>DB: Guardar tiempo inicial
    DB-->>SRS: Resultado guardado
    SRS-->>SRC: Resultado creado
    SRC-->>A: 201 Created

    Note over A,DB: Aplicar penalizaciones
    A->>SRC: PUT /api/stageresults/{id}/penalizacion
    SRC->>SRS: aplicarPenalizacion(id, penaltyData)
    SRS->>DB: Actualizar con penalizaciones
    SRS->>CALC: Recalcular tiempo final
    CALC-->>SRS: Tiempo final calculado
    SRS->>DB: Guardar tiempo final
    DB-->>SRS: Confirmación
    SRS-->>SRC: Resultado actualizado
    SRC-->>A: 200 OK

    Note over A,DB: Obtener clasificación por etapa
    A->>SRC: GET /api/stageresults/clasificacion/stage/{id}
    SRC->>SRS: getClasificacionPorStage(stageId)
    SRS->>DB: Consultar resultados de etapa
    DB-->>SRS: Lista de resultados
    SRS->>CALC: Ordenar por tiempo final
    CALC-->>SRS: Lista ordenada con posiciones
    SRS-->>SRC: ClasificacionCompletaDTO[]
    SRC-->>A: Clasificación de etapa

    Note over A,DB: Obtener clasificación general
    A->>SRC: GET /api/stageresults/clasificacion/event/{id}
    SRC->>SRS: getClasificacionGeneral(eventId)
    SRS->>DB: Consultar todos los resultados del evento
    DB-->>SRS: Resultados completos
    SRS->>CALC: Agrupar por vehículo
    CALC->>CALC: Sumar tiempos totales
    CALC->>CALC: Ordenar por tiempo total
    CALC-->>SRS: Clasificación general
    SRS-->>SRC: ClasificacionCompletaDTO[]
    SRC-->>A: Clasificación general

    Note over A,DB: Clasificación por categoría
    A->>SRC: GET /api/stageresults/clasificacion/category/{id}
    SRC->>SRS: getClasificacionPorCategoria(categoryId, eventId)
    SRS->>DB: Consultar por evento y categoría
    DB-->>SRS: Resultados filtrados
    SRS->>CALC: Calcular clasificación filtrada
    CALC-->>SRS: Clasificación por categoría
    SRS-->>SRC: ClasificacionCompletaDTO[]
    SRC-->>A: Clasificación de categoría
```

---

## 🧪 **11. Arquitectura de Testing Avanzada**

### Descripción

Estrategia completa de testing incluyendo configuración de Spring Security, mocking, y cobertura de todos los escenarios de autenticación y autorización.

```mermaid
%%{init: {'theme':'base', 'themeVariables': { 'fontSize': '14px'}}}%%
flowchart TB
    START([🧪 Testing Pipeline]) --> CONFIG[⚙️ CONFIGURACIÓN<br/>🔧 MockitoExtension<br/>🔐 Mock SecurityContext<br/>👤 Mock Authentication<br/>🌐 Mock HttpServletRequest<br/>🛠️ ReflectionTestUtils]

    CONFIG --> SETUP[🔒 SETUP PATTERNS<br/>👑 Admin User Setup<br/>👤 Regular User Setup<br/>🔑 JWT Token Mocking<br/>🛡️ Security Context Setup<br/>📊 Service Layer Mocking]

    SETUP --> AUTH_TESTS[🔐 AUTHENTICATION TESTS<br/>✅ Login válido/inválido<br/>✅ OAuth2 flow completo<br/>✅ JWT generation/validation<br/>✅ Session management<br/>✅ Token expiration]

    SETUP --> AUTHZ_TESTS[🛡️ AUTHORIZATION TESTS<br/>✅ Admin vs User access<br/>✅ Resource ownership<br/>✅ Endpoint permissions<br/>✅ Role-based access<br/>✅ Forbidden scenarios]

    SETUP --> BUSINESS_TESTS[📊 BUSINESS LOGIC TESTS<br/>✅ CRUD operations<br/>✅ Data validation<br/>✅ Business rules<br/>✅ Entity relationships<br/>✅ Cascade operations]

    SETUP --> ERROR_TESTS[🚨 ERROR HANDLING TESTS<br/>✅ Custom exceptions<br/>✅ Global exception handler<br/>✅ Validation errors<br/>✅ HTTP status codes<br/>✅ Error responses]

    SETUP --> FILE_TESTS[📁 FILE SECURITY TESTS<br/>✅ Upload validation<br/>✅ File type restrictions<br/>✅ Size limitations<br/>✅ Path traversal protection<br/>✅ Malicious file detection]

    AUTH_TESTS --> VALIDATE[✅ Ejecutar Tests]
    AUTHZ_TESTS --> VALIDATE
    BUSINESS_TESTS --> VALIDATE
    ERROR_TESTS --> VALIDATE
    FILE_TESTS --> VALIDATE

    VALIDATE --> RESULTS[📊 RESULTADOS<br/>📈 >85% Line Coverage<br/>🌿 >90% Branch Coverage<br/>🎯 100% Critical Paths<br/>🔒 Security Scenarios<br/>✅ All Tests Passing]

    %% STYLING
    classDef startStyle fill:#e3f2fd,stroke:#1976d2,stroke-width:3px
    classDef configStyle fill:#fff3e0,stroke:#ff9800,stroke-width:3px
    classDef testStyle fill:#f3e5f5,stroke:#7b1fa2,stroke-width:3px
    classDef validateStyle fill:#e0f2f1,stroke:#00695c,stroke-width:3px
    classDef resultsStyle fill:#e8f5e8,stroke:#4caf50,stroke-width:3px

    class START startStyle
    class CONFIG,SETUP configStyle
    class AUTH_TESTS,AUTHZ_TESTS,BUSINESS_TESTS,ERROR_TESTS,FILE_TESTS testStyle
    class VALIDATE validateStyle
    class RESULTS resultsStyle
```

---

## ⚠️ **12. Sistema de Manejo de Errores**

### Descripción

Arquitectura centralizada de manejo de excepciones con GlobalExceptionHandler, respuestas estandarizadas y logging comprehensivo para todos los tipos de errores.

```mermaid
%%{init: {'theme':'base', 'themeVariables': { 'fontSize': '11px'}}}%%
graph TD
    EXCEPTION([⚠️ Excepción ocurre]) --> EXCEPTION_TYPE{🔍 Tipo de excepción}

    EXCEPTION_TYPE -->|ValidationException| VALIDATION_HANDLER[🔧 GlobalExceptionHandler<br/>handleValidationException]
    EXCEPTION_TYPE -->|ResourceNotFoundException| NOT_FOUND_HANDLER[🔍 GlobalExceptionHandler<br/>handleResourceNotFound]
    EXCEPTION_TYPE -->|UnauthorizedException| UNAUTHORIZED_HANDLER[🔐 GlobalExceptionHandler<br/>handleUnauthorized]
    EXCEPTION_TYPE -->|ForbiddenException| FORBIDDEN_HANDLER[🛡️ GlobalExceptionHandler<br/>handleForbidden]
    EXCEPTION_TYPE -->|FileStorageException| FILE_HANDLER[📁 GlobalExceptionHandler<br/>handleFileStorageException]
    EXCEPTION_TYPE -->|JWTException| JWT_HANDLER[🔑 GlobalExceptionHandler<br/>handleJWTException]
    EXCEPTION_TYPE -->|OAuth2Exception| OAUTH_HANDLER[🌐 GlobalExceptionHandler<br/>handleOAuth2Exception]
    EXCEPTION_TYPE -->|Exception| GENERIC_HANDLER[⚠️ GlobalExceptionHandler<br/>handleGenericException]

    VALIDATION_HANDLER --> BUILD_RESPONSE_400[📋 Construir ApiError<br/>Status 400 Bad Request<br/>Detalles de validación]
    NOT_FOUND_HANDLER --> BUILD_RESPONSE_404[🔍 Construir ApiError<br/>Status 404 Not Found<br/>Recurso no encontrado]
    UNAUTHORIZED_HANDLER --> BUILD_RESPONSE_401[🔐 Construir ApiError<br/>Status 401 Unauthorized<br/>Credenciales inválidas]
    FORBIDDEN_HANDLER --> BUILD_RESPONSE_403[🛡️ Construir ApiError<br/>Status 403 Forbidden<br/>Sin permisos suficientes]
    FILE_HANDLER --> BUILD_RESPONSE_422[📁 Construir ApiError<br/>Status 422 Unprocessable<br/>Error de archivo]
    JWT_HANDLER --> BUILD_RESPONSE_401_JWT[🔑 Construir ApiError<br/>Status 401 Unauthorized<br/>Token JWT inválido]
    OAUTH_HANDLER --> BUILD_RESPONSE_401_OAUTH[🌐 Construir ApiError<br/>Status 401 Unauthorized<br/>OAuth2 falló]
    GENERIC_HANDLER --> BUILD_RESPONSE_500[⚠️ Construir ApiError<br/>Status 500 Internal Error<br/>Error del servidor]

    BUILD_RESPONSE_400 --> LOG_LEVEL{📊 Determinar nivel de log}
    BUILD_RESPONSE_404 --> LOG_LEVEL
    BUILD_RESPONSE_401 --> LOG_LEVEL
    BUILD_RESPONSE_403 --> LOG_LEVEL
    BUILD_RESPONSE_422 --> LOG_LEVEL
    BUILD_RESPONSE_401_JWT --> LOG_LEVEL
    BUILD_RESPONSE_401_OAUTH --> LOG_LEVEL
    BUILD_RESPONSE_500 --> LOG_LEVEL

    LOG_LEVEL -->|400-404| LOG_WARN[⚠️ LOG.warn<br/>Usuario/Client error]
    LOG_LEVEL -->|401-403| LOG_INFO[ℹ️ LOG.info<br/>Security event]
    LOG_LEVEL -->|422| LOG_WARN_FILE[⚠️ LOG.warn<br/>File processing error]
    LOG_LEVEL -->|500| LOG_ERROR[❌ LOG.error<br/>Server error + stacktrace]

    LOG_WARN --> RETURN_RESPONSE[📤 Retornar ResponseEntity<ApiError>]
    LOG_INFO --> RETURN_RESPONSE
    LOG_WARN_FILE --> RETURN_RESPONSE
    LOG_ERROR --> RETURN_RESPONSE

    RETURN_RESPONSE --> CLIENT_RESPONSE[📱 Respuesta al cliente<br/>JSON estructura consistente]

    %% STYLING
    classDef exception fill:#ffebee,stroke:#d32f2f,stroke-width:2px
    classDef handler fill:#e3f2fd,stroke:#1976d2,stroke-width:2px
    classDef response fill:#f3e5f5,stroke:#7b1fa2,stroke-width:2px
    classDef logging fill:#fff3e0,stroke:#f57c00,stroke-width:2px
    classDef success fill:#e8f5e8,stroke:#388e3c,stroke-width:2px

    class EXCEPTION,EXCEPTION_TYPE exception
    class VALIDATION_HANDLER,NOT_FOUND_HANDLER,UNAUTHORIZED_HANDLER,FORBIDDEN_HANDLER,FILE_HANDLER,JWT_HANDLER,OAUTH_HANDLER,GENERIC_HANDLER handler
    class BUILD_RESPONSE_400,BUILD_RESPONSE_404,BUILD_RESPONSE_401,BUILD_RESPONSE_403,BUILD_RESPONSE_422,BUILD_RESPONSE_401_JWT,BUILD_RESPONSE_401_OAUTH,BUILD_RESPONSE_500 response
    class LOG_LEVEL,LOG_WARN,LOG_INFO,LOG_WARN_FILE,LOG_ERROR logging
    class RETURN_RESPONSE,CLIENT_RESPONSE success
```

---

## 🎯 **Guía de Navegación**

### 📱 **Para la mejor experiencia de visualización:**

| Método                   | Pasos                                                | Mejor Para                   |
| ------------------------ | ---------------------------------------------------- | ---------------------------- |
| **🖱️ Click Derecho**     | Diagrama → "Abrir imagen en nueva pestaña"           | Zoom nativo del navegador    |
| **⌨️ GitHub Codespaces** | Presiona `.` en el repositorio                       | Desarrollo y análisis        |
| **🔍 Mermaid Live**      | Copiar código a [mermaid.live](https://mermaid.live) | Edición y zoom personalizado |
| **📱 Móvil**             | Pellizcar para hacer zoom (pinch to zoom)            | Dispositivos táctiles        |

### 🔗 **Enlaces Relacionados**

- 🏠 **[README Principal](./README.md)** - Documentación completa del proyecto
- 🌐 **[Demo en Vivo](https://project-gpx.vercel.app)** - Aplicación funcionando
- 💻 **Código Fuente** - Explorar las carpetas del proyecto
- 🎨 **Mermaid Editor** - [mermaid.live](https://mermaid.live) para edición avanzada

---

## 📊 **Métricas de los Diagramas**

- **📈 12 Diagramas completos** cubriendo toda la arquitectura del sistema
- **🎯 500+ elementos visuales** con iconos descriptivos y códigos de color
- **🔗 300+ conexiones** mostrando flujos de datos y relaciones
- **🎨 Código coloreado** para identificación rápida de capas y componentes
- **📱 Responsive design** optimizado para GitHub, Codespaces y dispositivos móviles
- **🧩 Cobertura total** desde arquitectura hasta testing, OAuth2 y clasificaciones
- **🔍 Fuentes optimizadas** (10-14px) para mejor legibilidad en GitHub
- **📊 100% basado en Mermaid** compatible con todo el ecosistema GitHub
