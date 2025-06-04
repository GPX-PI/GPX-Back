# 🔐 Configuración de Secretos y Variables de Entorno

## Configuración para Desarrollo

### 1. Archivo .env (Recomendado)

Crea un archivo `.env` en la raíz del proyecto (mismo nivel que `pom.xml`):

```env
# Google OAuth2 Configuration
GOOGLE_CLIENT_ID=tu-client-id-real-aqui
GOOGLE_CLIENT_SECRET=tu-client-secret-real-aqui
```

**⚠️ Importante:**

- NO uses comillas alrededor de los valores
- NO agregues espacios alrededor del `=`
- Este archivo está en `.gitignore` y NO se sube al repositorio

### 2. Variables de Entorno del Sistema

#### Windows PowerShell:

```powershell
$env:GOOGLE_CLIENT_ID = "tu-client-id-real"
$env:GOOGLE_CLIENT_SECRET = "tu-client-secret-real"
mvn spring-boot:run
```

#### Windows CMD:

```cmd
set GOOGLE_CLIENT_ID=tu-client-id-real
set GOOGLE_CLIENT_SECRET=tu-client-secret-real
mvn spring-boot:run
```

#### Linux/Mac:

```bash
export GOOGLE_CLIENT_ID=tu-client-id-real
export GOOGLE_CLIENT_SECRET=tu-client-secret-real
mvn spring-boot:run
```

### 3. Perfiles de Spring

Usa diferentes perfiles para diferentes entornos:

```bash
# Desarrollo
mvn spring-boot:run -Dspring.profiles.active=dev

# Producción
mvn spring-boot:run -Dspring.profiles.active=prod
```

## Configuración para Producción

### 1. Variables de Entorno del Servidor

En tu servidor de producción, configura:

```bash
export GOOGLE_CLIENT_ID=tu-client-id-produccion
export GOOGLE_CLIENT_SECRET=tu-client-secret-produccion
export FRONTEND_URL=https://tu-dominio.com
export SERVER_URL=https://tu-api.com
```

### 2. Docker (si usas containers)

```dockerfile
ENV GOOGLE_CLIENT_ID=tu-client-id
ENV GOOGLE_CLIENT_SECRET=tu-client-secret
```

### 3. Servicios en la Nube

- **AWS**: AWS Secrets Manager
- **Google Cloud**: Secret Manager
- **Azure**: Key Vault
- **Heroku**: Config Vars

## Verificación

Para verificar que las variables se cargan correctamente, revisa los logs:

```
✅ Variables de .env cargadas correctamente
```

Si ves este mensaje, tu configuración está funcionando.

## Troubleshooting

### Error: "invalid_client"

- Verifica que `GOOGLE_CLIENT_ID` y `GOOGLE_CLIENT_SECRET` estén configurados
- Asegúrate de que no hay espacios extra en los valores
- Verifica que el archivo `.env` esté en la raíz del proyecto

### Error: "Failed to fetch"

- Verifica que el backend esté corriendo en `http://localhost:8080`
- Revisa la configuración de CORS
- Asegúrate de que no hay problemas de red/firewall

## Estructura de Archivos

```
Proyecto/
├── pom.xml
├── .env                    # ← Aquí va tu archivo .env
├── .gitignore             # ← Debe incluir .env
├── src/
│   └── main/
│       └── resources/
│           ├── application.properties
│           ├── application-dev.properties
│           └── application-prod.properties
└── README.md
```
