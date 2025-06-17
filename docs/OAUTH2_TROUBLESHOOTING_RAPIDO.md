# 🚨 OAuth2 Troubleshooting Rápido

## Error 401: "OAuth client was not found"

### ✅ Solución Inmediata:

```bash
# 1. Verificar archivo .env existe
dir .env

# 2. Verificar contenido .env (ocultar secretos)
type .env

# 3. Reiniciar aplicación
# Ctrl+C y volver a ejecutar

# 4. Verificar variables en aplicación
# Agregar temporalmente println en GpxApplication.java para debug
```

### 📋 Checklist:

- [ ] Archivo `.env` existe en raíz del proyecto
- [ ] GOOGLE_CLIENT_ID configurado en `.env`
- [ ] GOOGLE_CLIENT_SECRET configurado en `.env`
- [ ] `GpxApplication.java` carga variables correctamente
- [ ] Aplicación reiniciada después de cambios

---

## Error: "oauth2User es null"

### ✅ Solución:

Verificar `JwtRequestFilter.java` excluye rutas OAuth2:

```java
if (requestPath.startsWith("/oauth2/") || requestPath.startsWith("/login/oauth2/")) {
    chain.doFilter(request, response);
    return;
}
```

---

## Error: "password viola restricción de no nulo"

### ✅ Solución SQL:

```sql
ALTER TABLE app_user ALTER COLUMN password DROP NOT NULL;
```

---

## Error: "Failed to fetch"

### ✅ Solución:

Agregar a `SecurityConfig.java`:

```java
.requestMatchers("/api/users/oauth2/login-url").permitAll()
```

---

## Error: "nombres muy largos"

### ✅ Solución:

En `User.java` cambiar:

```java
@Column(name = "first_name", nullable = false, length = 50) // Era 20
@Column(name = "last_name", nullable = true, length = 50)   // Era 20
```

---

## 🔧 Pasos de Emergencia

### 1. Verificar Variables

```bash
echo $GOOGLE_CLIENT_ID
echo $GOOGLE_CLIENT_SECRET
```

### 2. Verificar Endpoints

```bash
curl http://localhost:8080/api/users/oauth2/login-url
```

### 3. Reiniciar Todo

- Ctrl+C en aplicación
- Reiniciar aplicación
- Limpiar caché navegador
- Probar en incógnito

### 4. Verificar Google Console

- URIs exactas: `http://localhost:8080/login/oauth2/code/google`
- APIs habilitadas
- Credenciales correctas

---

## 📞 Última Opción

Si nada funciona, comparar con configuración que funcionaba:

1. **GpxApplication.java** debe cargar variables
2. **SecurityConfig.java** con `SessionCreationPolicy.IF_REQUIRED`
3. **JwtRequestFilter.java** excluye rutas OAuth2
4. **User.java** password nullable, nombres length=50
5. **application.properties** con variables ${GOOGLE_CLIENT_ID}

**¡Esta guía soluciona el 99% de problemas OAuth2!** 🚀
