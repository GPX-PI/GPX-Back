# Guía de Despliegue en Render

Esta guía te ayudará a desplegar tu aplicación Spring Boot en Render.

## 📋 Prerequisitos

1. Cuenta en [Render](https://render.com)
2. Repositorio de código en GitHub/GitLab
3. Base de datos PostgreSQL (puedes crear una en Render)

## 🗄️ Configuración de Base de Datos

### Opción 1: Crear una nueva base de datos en Render

1. Ve a tu dashboard de Render
2. Crea un nuevo servicio de tipo "PostgreSQL"
3. Elige el plan Free (o el que prefieras)
4. Anota la `DATABASE_URL` que te proporciona Render

### Opción 2: Usar tu base de datos existente

Si ya tienes una base de datos PostgreSQL, asegúrate de tener la URL de conexión en el formato:

```postgresql://username:password@hostname:port/database_name

```

## 🚀 Pasos para el Despliegue

### 1. Preparar el repositorio

Asegúrate de que todos los archivos estén en tu repositorio:

- ✅ `Dockerfile`
- ✅ `.dockerignore`
- ✅ `render.yaml`
- ✅ `src/main/resources/application-render.properties`

### 2. Crear el servicio web en Render

1. **Conectar repositorio:**

   - Ve a tu dashboard de Render
   - Clic en "New +" → "Web Service"
   - Conecta tu repositorio de GitHub/GitLab

2. **Configurar el servicio:**
   - **Name:** `gpx-backend` (o el nombre que prefieras)
   - **Runtime:** Docker
   - **Build Command:** (dejar vacío)
   - **Start Command:** (dejar vacío, usa el del Dockerfile)

### 3. Configurar Variables de Entorno

En la sección "Environment" de tu servicio, agrega las siguientes variables:

#### Variables Obligatorias:

```bash
DATABASE_URL=postgresql://username:password@hostname:port/database_name
GOOGLE_CLIENT_ID=tu-google-client-id
GOOGLE_CLIENT_SECRET=tu-google-client-secret
JWT_SECRET=tu-clave-secreta-super-larga-y-segura-para-jwt-tokens
SERVER_URL=https://tu-app.onrender.com
FRONTEND_URL=https://tu-frontend.com
SPRING_PROFILES_ACTIVE=render
```

#### Cómo obtener las credenciales de Google OAuth2:

1. Ve a [Google Cloud Console](https://console.cloud.google.com/)
2. Crea un proyecto o selecciona uno existente
3. Habilita la API de Google+
4. Ve a "Credenciales" → "Crear credenciales" → "ID de cliente de OAuth 2.0"
5. Configura los orígenes autorizados:
   - **Orígenes autorizados:** `https://tu-app.onrender.com`
   - **URIs de redirección:** `https://tu-app.onrender.com/login/oauth2/code/google`

### 4. Configurar CORS

Actualiza la configuración de CORS en tu aplicación para incluir tu dominio de Render.

### 5. Desplegar

1. Clic en "Create Web Service"
2. Render automáticamente detectará el `Dockerfile` y comenzará el build
3. El proceso puede tomar unos minutos la primera vez

## 🔍 Verificación del Despliegue

Una vez desplegado, verifica que todo funciona:

1. **Health Check:** `https://tu-app.onrender.com/actuator/health`
2. **API Base:** `https://tu-app.onrender.com/api/...`

## 🔧 Configuraciones Adicionales

### Auto-Deploy desde GitHub

Render puede configurarse para hacer deploy automático cada vez que haces push a tu rama principal:

1. En la configuración del servicio, habilita "Auto-Deploy"
2. Selecciona la rama que quieres monitorear (generalmente `main` o `master`)

### Logs y Debugging

- Ve a la pestaña "Logs" en tu servicio de Render para ver los logs en tiempo real
- Los logs de Spring Boot aparecerán aquí

### Scaling

- En el plan Free tienes limitaciones de recursos
- Puedes upgrade a un plan paid para mejor rendimiento

## ⚠️ Troubleshooting

### Error de conexión a base de datos

- Verifica que la `DATABASE_URL` esté correcta
- Asegúrate de que el servidor de BD esté accesible

### Error 502 Bad Gateway

- Revisa los logs para ver errores de la aplicación
- Verifica que el puerto esté configurado correctamente (`${PORT}`)

### Problemas con OAuth2

- Verifica que las URLs de redirección en Google Cloud estén correctas
- Confirma que las variables `GOOGLE_CLIENT_ID` y `GOOGLE_CLIENT_SECRET` estén configuradas

## 📝 Notas Importantes

- Render usa el puerto de la variable de entorno `PORT`
- El plan Free tiene limitaciones de memoria y CPU
- La aplicación puede "dormirse" en el plan Free si no tiene tráfico
- Considera usar un plan paid para aplicaciones en producción

## 🔄 Actualizaciones

Para actualizar tu aplicación:

1. Haz push de tus cambios a GitHub
2. Si tienes auto-deploy habilitado, Render desplegará automáticamente
3. Si no, ve al dashboard y haz clic en "Manual Deploy"
