# 🔐 Configuración de Variables de Entorno

Este archivo describe cómo configurar las variables de entorno necesarias para el proyecto GPX Backend.

## 📋 Variables Requeridas

### 🗄️ Base de Datos

```bash
DATABASE_URL=jdbc:postgresql://your-db-host:5432/your-database-name
DATABASE_USERNAME=your-username
DATABASE_PASSWORD=your-secure-password
```

### 🔑 JWT Configuration

```bash
JWT_SECRET=your-super-long-and-secure-jwt-secret-key-here
```

### 🔒 Google OAuth2

```bash
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
GOOGLE_OAUTH2_REDIRECT_URI=http://localhost:8080/login/oauth2/code/google
```

### 🌐 Frontend & CORS

```bash
FRONTEND_REDIRECT_URL=http://localhost:3000/
CORS_ALLOWED_ORIGINS=http://localhost:3000
```

### 🚀 Server

```bash
PORT=8080
```

## 🛠️ Configuración Local

### 1. Crear archivo `.env`

Copia el archivo `.env.example` a `.env`:

```bash
cp .env.example .env
```

### 2. Configurar valores reales

Edita el archivo `.env` con tus valores reales:

```bash
# No subir este archivo al repositorio
DATABASE_PASSWORD=tu-password-real-aqui
JWT_SECRET=tu-clave-jwt-super-segura-de-al-menos-256-bits
GOOGLE_CLIENT_ID=tu-google-client-id-real
GOOGLE_CLIENT_SECRET=tu-google-client-secret-real
```

## ☁️ Configuración en Render

### Variables de Entorno en Render:

1. Ve a tu dashboard de Render
2. Selecciona tu servicio
3. Ve a "Environment"
4. Agrega las siguientes variables:

```
DATABASE_URL=jdbc:postgresql://dpg-xxx-a.ohio-postgres.render.com:5432/gpx_db_xxx
DATABASE_USERNAME=renderuser
DATABASE_PASSWORD=tu-password-de-render
JWT_SECRET=tu-clave-jwt-super-segura
GOOGLE_CLIENT_ID=tu-google-client-id
GOOGLE_CLIENT_SECRET=tu-google-client-secret
GOOGLE_OAUTH2_REDIRECT_URI=https://tu-app.onrender.com/login/oauth2/code/google
FRONTEND_REDIRECT_URL=https://tu-frontend.onrender.com/
CORS_ALLOWED_ORIGINS=https://tu-frontend.onrender.com
```

## 🔒 Buenas Prácticas de Seguridad

### ✅ Hacer:

- Usar variables de entorno para todos los datos sensibles
- Mantener `.env` fuera del control de versiones
- Usar contraseñas largas y complejas
- Rotar las claves periódicamente

### ❌ No hacer:

- Hardcodear contraseñas en el código
- Subir archivos `.env` al repositorio
- Usar contraseñas débiles
- Compartir credenciales en texto plano

## 🚨 Recuperación de Emergencia

Si accidentalmente subes credenciales al repositorio:

1. **Cambiar inmediatamente** todas las credenciales comprometidas
2. **Revocar** tokens de acceso
3. **Limpiar** el historial de Git si es necesario
4. **Notificar** al equipo del incidente

## 📝 Validación

Para verificar que las variables están configuradas correctamente:

```bash
# Verificar que las variables están disponibles
echo $DATABASE_PASSWORD
echo $JWT_SECRET
```

Si usas aplicaciones que leen `.env` automáticamente, simplemente inicia la aplicación y verifica los logs.
