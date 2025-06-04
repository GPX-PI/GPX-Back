# 🔐 Guía Completa: Google OAuth2 Setup y Troubleshooting

## 📋 Índice

1. [Configuración Inicial](#1-configuración-inicial)
2. [Configuración Google Cloud Console](#2-configuración-google-cloud-console)
3. [Configuración Spring Boot](#3-configuración-spring-boot)
4. [Variables de Entorno](#4-variables-de-entorno)
5. [Problemas Comunes y Soluciones](#5-problemas-comunes-y-soluciones)
6. [Checklist de Verificación](#6-checklist-de-verificación)

---

## 1. Configuración Inicial

### 📦 Dependencias necesarias en `pom.xml`

```xml
<!-- OAuth2 Client -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>

<!-- Para manejar variables .env -->
<dependency>
    <groupId>io.github.cdimascio</groupId>
    <artifactId>dotenv-java</artifactId>
    <version>3.0.0</version>
</dependency>

<!-- JWT para tokens -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
```

---

## 2. Configuración Google Cloud Console

### 🌐 Paso 1: Crear/Configurar Proyecto

1. Ve a [Google Cloud Console](https://console.cloud.google.com/)
2. Selecciona tu proyecto o crea uno nuevo
3. Habilita la **Google+ API** o **Google People API**

### 🔑 Paso 2: Crear Credenciales OAuth2

1. Ve a **APIs y servicios** > **Credenciales**
2. Clic en **"Crear credenciales"** > **"ID de cliente OAuth 2.0"**
3. **Tipo de aplicación**: Aplicación web
4. **Nombre**: Tu aplicación (ej: "Mi App Spring Boot")

### 🔗 Paso 3: Configurar URIs de Redirección

**⚠️ CRÍTICO**: Agregar exactamente estas URIs:

```
http://localhost:8080/login/oauth2/code/google
http://localhost:8080/oauth2/success
```

### 📋 Paso 4: Guardar Credenciales

- Copia el **Client ID**
- Copia el **Client Secret**
- ⚠️ **NUNCA** compartas estas credenciales públicamente

---

## 3. Configuración Spring Boot

### 📄 `application.properties`

```properties
# OAuth2 Configuration
spring.security.oauth2.client.registration.google.client-id=${GOOGLE_CLIENT_ID}
spring.security.oauth2.client.registration.google.client-secret=${GOOGLE_CLIENT_SECRET}
spring.security.oauth2.client.registration.google.scope=openid,profile,email
spring.security.oauth2.client.registration.google.redirect-uri=http://localhost:8080/login/oauth2/code/google

# Provider
spring.security.oauth2.client.provider.google.authorization-uri=https://accounts.google.com/o/oauth2/v2/auth
spring.security.oauth2.client.provider.google.token-uri=https://oauth2.googleapis.com/token
spring.security.oauth2.client.provider.google.user-info-uri=https://www.googleapis.com/oauth2/v2/userinfo
spring.security.oauth2.client.provider.google.user-name-attribute=sub

# Frontend redirect after OAuth2 success
app.oauth2.frontend-redirect-url=http://localhost:3000/oauth2/redirect
```

### 🔧 SecurityConfig.java - Configuración Crítica

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)) // ⚠️ CRÍTICO
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/users/register").permitAll()
                .requestMatchers("/api/users/oauth2/login-url").permitAll() // ⚠️ IMPORTANTE
                .requestMatchers("/oauth2/**").permitAll()
                .requestMatchers("/login/oauth2/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .defaultSuccessUrl("/api/oauth2/success", true)
                .failureUrl("/oauth2/error")
            );

        return http.build();
    }
}
```

---

## 4. Variables de Entorno

### 📁 Crear archivo `.env` en la raíz del proyecto

```bash
# 🔑 Variables de entorno para OAuth2 con Google
GOOGLE_CLIENT_ID=tu_google_client_id_aqui
GOOGLE_CLIENT_SECRET=tu_google_client_secret_aqui
```

### 🚨 Actualizar `.gitignore`

```gitignore
# Environment files
.env
.env.dev
.env.prod
.env.local
*.env

# OAuth2 secrets
oauth2-secrets/
google-credentials.json
```

### ⚙️ `GpxApplication.java` - Cargar variables

```java
public static void main(String[] args) {
    loadEnvironmentVariables();
    SpringApplication.run(GpxApplication.class, args);
}

private static void loadEnvironmentVariables() {
    try {
        File envDevFile = new File(".env.dev");
        File envFile = new File(".env");

        Dotenv dotenv = null;
        if (envDevFile.exists()) {
            dotenv = Dotenv.configure().filename(".env.dev").load();
        } else if (envFile.exists()) {
            dotenv = Dotenv.configure().filename(".env").load();
        }

        // ⚠️ CRÍTICO: Establecer como propiedades del sistema
        if (dotenv != null) {
            dotenv.entries().forEach(entry -> {
                System.setProperty(entry.getKey(), entry.getValue());
            });
        }
    } catch (Exception e) {
        // Continuar sin errores
    }
}
```

---

## 5. Problemas Comunes y Soluciones

### 🚨 Error: "OAuth client was not found" - Error 401: invalid_client

**Causa**: Variables GOOGLE_CLIENT_ID/SECRET no cargadas correctamente

**Solución**:

1. ✅ Verificar que `.env` existe en raíz del proyecto
2. ✅ Verificar que `GpxApplication.java` carga variables correctamente
3. ✅ Verificar que las credenciales en `.env` son correctas
4. ✅ Reiniciar la aplicación después de cambios

### 🚨 Error: "el valor nulo en la columna «password» viola la restricción de no nulo"

**Causa**: Base de datos requiere password, pero OAuth2 no lo necesita

**Solución SQL**:

```sql
ALTER TABLE app_user ALTER COLUMN password DROP NOT NULL;
```

**Solución en Model**:

```java
@Column(nullable = true)
@JsonIgnore
private String password;
```

### 🚨 Error: "oauth2User es null" en controlador

**Causa**: JwtRequestFilter interfiere con OAuth2

**Solución en JwtRequestFilter**:

```java
// Excluir rutas OAuth2 del filtro JWT
if (requestPath.startsWith("/oauth2/") || requestPath.startsWith("/login/oauth2/")) {
    chain.doFilter(request, response);
    return;
}

// Si ya hay autenticación OAuth2, preservarla
Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
if (existingAuth != null && existingAuth.isAuthenticated() &&
        !(existingAuth instanceof AnonymousAuthenticationToken)) {
    chain.doFilter(request, response);
    return;
}
```

### 🚨 Error: "Failed to fetch" desde frontend

**Causa**: Endpoint no permitido en SecurityConfig

**Solución**:

```java
.requestMatchers("/api/users/oauth2/login-url").permitAll()
```

### 🚨 Error: "el valor es demasiado largo para el tipo character varying(20)"

**Causa**: Nombres de Google muy largos

**Solución en User.java**:

```java
@Column(name = "first_name", nullable = false, length = 50) // Era 20
private String firstName;

@Column(name = "last_name", nullable = true, length = 50) // Era 20
private String lastName;
```

### 🚨 Error: "Invalid characters (CR/LF) in header Location"

**Causa**: Caracteres especiales en URL de redirección

**Solución en OAuth2Controller**:

```java
// Limpiar parámetros para evitar caracteres inválidos
firstName = firstName.replaceAll("[\\r\\n\\t]", "").trim();
String redirectUrl = frontendRedirectUrl +
    "?token=" + token +
    "&firstName=" + java.net.URLEncoder.encode(firstName, "UTF-8");
```

### 🚨 Error: "googleId es null"

**Causa**: Google envía 'id' en lugar de 'sub'

**Solución en OAuth2Service**:

```java
String googleId = oauth2User.getAttribute("sub");
// Fallback si 'sub' es null
if (googleId == null) {
    googleId = oauth2User.getAttribute("id");
}
```

---

## 6. Checklist de Verificación

### ✅ Pre-requisitos

- [ ] Google Cloud Project creado
- [ ] APIs habilitadas (Google+ o People API)
- [ ] Credenciales OAuth2 creadas
- [ ] URIs de redirección configuradas correctamente

### ✅ Configuración Backend

- [ ] Dependencias agregadas al `pom.xml`
- [ ] Variables `.env` configuradas
- [ ] `application.properties` configurado
- [ ] `SecurityConfig` con SessionPolicy.IF_REQUIRED
- [ ] `JwtRequestFilter` excluye rutas OAuth2
- [ ] Columna `password` permite NULL
- [ ] Columnas `firstName`/`lastName` con length 50

### ✅ Testing

- [ ] Aplicación inicia sin errores
- [ ] Endpoint `/api/users/oauth2/login-url` responde
- [ ] Redirección a Google funciona
- [ ] Callback desde Google funciona
- [ ] Usuario se crea correctamente en DB
- [ ] JWT se genera correctamente
- [ ] Redirección a frontend funciona

### ✅ Comandos de Verificación

```bash
# Verificar variables de entorno cargadas
echo $GOOGLE_CLIENT_ID

# Verificar endpoint OAuth2
curl http://localhost:8080/api/users/oauth2/login-url

# Verificar base de datos
psql -d proyecto_integrador -c "\d app_user"
```

---

## 🆘 Troubleshooting Rápido

### Cuando algo no funciona:

1. **Verificar variables**:

   ```bash
   # Verificar que .env existe
   ls -la .env

   # Verificar contenido (ocultar secretos)
   cat .env | sed 's/=.*/=***/'
   ```

2. **Verificar logs**:

   - Buscar errores 401/403
   - Verificar si oauth2User es null
   - Comprobar errores de base de datos

3. **Verificar configuración Google**:

   - URIs de redirección exactas
   - APIs habilitadas
   - Credenciales correctas

4. **Reiniciar todo**:
   - Reiniciar aplicación Spring Boot
   - Limpiar caché navegador
   - Probar en ventana incógnito

---

## 📚 Recursos Adicionales

- [Spring Security OAuth2 Documentation](https://docs.spring.io/spring-security/reference/servlet/oauth2/index.html)
- [Google OAuth2 Setup Guide](https://developers.google.com/identity/protocols/oauth2)
- [Spring Boot OAuth2 Client](https://spring.io/guides/tutorials/spring-boot-oauth2/)

---

## ⚠️ Notas de Seguridad

- **NUNCA** subir archivos `.env` a Git
- **NUNCA** hardcodear credenciales en código
- Usar variables de entorno en producción
- Configurar HTTPS en producción
- Validar y sanitizar datos de Google

---

**📝 Esta guía cubre todos los problemas encontrados durante la implementación. ¡Guárdala para futuras referencias!** 🚀
