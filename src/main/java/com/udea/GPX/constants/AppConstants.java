package com.udea.gpx.constants;

/**
 * Constantes centralizadas de la aplicación gpx Racing
 */
public final class AppConstants {

  private AppConstants() {
    // Prevenir instanciación
  }

  // === CONSTANTES DE ARCHIVOS ===
  public static final class Files {
    private Files() {
      // Prevenir instanciación
    }

    public static final long MAX_FILE_SIZE_BYTES = 10L * 1024 * 1024; // 10MB
    public static final long MAX_EVENT_IMAGE_SIZE_BYTES = 5L * 1024 * 1024; // 5MB
    public static final String UPLOADS_DIR = "uploads/";
    public static final String EVENTS_UPLOAD_DIR = "uploads/events/";

    // Tipos MIME permitidos - Necesitan ser public para uso externo
    @SuppressWarnings("java:S2386") // Arrays necesarios como public para configuración
    public static final String[] ALLOWED_IMAGE_TYPES = {
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    };

    @SuppressWarnings("java:S2386") // Arrays necesarios como public para configuración
    public static final String[] ALLOWED_DOCUMENT_TYPES = {
        "application/pdf", "image/jpeg", "image/jpg", "image/png", "image/gif"
    };

    // Extensiones permitidas - Necesitan ser public para uso externo
    @SuppressWarnings("java:S2386") // Arrays necesarios como public para configuración
    public static final String[] VALID_IMAGE_EXTENSIONS = {
        ".jpg", ".jpeg", ".png", ".gif", ".webp"
    };

    @SuppressWarnings("java:S2386") // Arrays necesarios como public para configuración
    public static final String[] VALID_DOCUMENT_EXTENSIONS = {
        ".pdf", ".jpg", ".jpeg", ".png", ".gif"
    };
  }

  // === CONSTANTES DE VALIDACIÓN ===
  public static final class Validation {
    private Validation() {
      // Prevenir instanciación
    }

    public static final int MAX_NAME_LENGTH = 100;
    public static final int MAX_TEAM_NAME_LENGTH = 100;
    public static final int MAX_VEHICLE_PLATES_LENGTH = 20;
    public static final int MAX_SOAT_LENGTH = 50;
    public static final int MAX_CATEGORY_NAME_LENGTH = 100;

    // Coordenadas geográficas
    public static final double MIN_LATITUDE = -90.0;
    public static final double MAX_LATITUDE = 90.0;
    public static final double MIN_LONGITUDE = -180.0;
    public static final double MAX_LONGITUDE = 180.0;
  }

  // === CONSTANTES DE SEGURIDAD ===
  public static final class Security {
    private Security() {
      // Prevenir instanciación
    }

    public static final String AUTH_PROVIDER_LOCAL = "LOCAL";
    public static final String AUTH_PROVIDER_GOOGLE = "GOOGLE";

    // CORS - Orígenes de producción - Necesitan ser public para uso externo
    @SuppressWarnings("java:S2386") // Arrays necesarios como public para configuración
    public static final String[] PRODUCTION_ORIGINS = {
        "https://project-gpx.vercel.app",
        "https://gpx-racing.com",
        "https://gpx-back.onrender.com" // Backend de producción
    };

    // CORS - Orígenes de desarrollo - Necesitan ser public para uso externo
    @SuppressWarnings("java:S2386") // Arrays necesarios como public para configuración
    public static final String[] DEVELOPMENT_ORIGINS = {
        "http://localhost:3000",
        "http://127.0.0.1:3000",
        "http://localhost:5173",
        "http://127.0.0.1:5173",
        "http://localhost:8080",
        "http://127.0.0.1:8080"
    };

    @SuppressWarnings("java:S2386") // Arrays necesarios como public para configuración
    public static final String[] ALLOWED_METHODS = {
        "GET", "POST", "PUT", "DELETE", "OPTIONS"
    };

    @SuppressWarnings("java:S2386") // Arrays necesarios como public para configuración
    public static final String[] ALLOWED_HEADERS = {
        Api.HEADER_CONTENT_TYPE, Api.HEADER_AUTHORIZATION, "X-Requested-With"
    };

    @SuppressWarnings("java:S2386") // Arrays necesarios como public para configuración
    public static final String[] EXPOSED_HEADERS = {
        Api.HEADER_TOTAL_COUNT
    };

    public static final long CORS_MAX_AGE_PRODUCTION = 300L; // 5 minutos
    public static final long CORS_MAX_AGE_DEVELOPMENT = 3600L; // 1 hora
  }

  // === CONSTANTES DE MENSAJES ===
  public static final class Messages {
    private Messages() {
      // Prevenir instanciación
    }

    public static final String USUARIO_NO_ENCONTRADO = "Usuario no encontrado";
    public static final String VEHICULO_NO_ENCONTRADO = "Vehículo no encontrado";
    public static final String EVENTO_NO_ENCONTRADO = "Evento no encontrado";
    public static final String ETAPA_NO_ENCONTRADA = "Etapa no encontrada";
    public static final String CATEGORIA_NO_ENCONTRADA = "Categoría no encontrada";
    public static final String RESULTADO_NO_ENCONTRADO = "Resultado no encontrado";

    public static final String EMAIL_YA_EN_USO = "El email ya está en uso";
    public static final String EMAIL_YA_EN_USO_POR_OTRO_USUARIO = "El email ya está en uso por otro usuario";
    public static final String ARCHIVO_VACIO = "El archivo está vacío";
    public static final String ARCHIVO_DEMASIADO_GRANDE = "El archivo es demasiado grande";
    public static final String TIPO_ARCHIVO_NO_VALIDO = "Tipo de archivo no válido";
    public static final String EXTENSION_NO_VALIDA = "Extensión de archivo no válida";

    public static final String ACCESO_DENEGADO = "Acceso denegado";
    public static final String PERMISOS_INSUFICIENTES = "Permisos insuficientes";
    public static final String TOKEN_INVALIDO = "Token inválido";

    public static final String PASSWORD_ACTUAL_INCORRECTA = "La contraseña actual es incorrecta";
    public static final String PASSWORD_NUEVA_DEBE_SER_DIFERENTE = "La nueva contraseña debe ser diferente a la actual";

    public static final String ERROR_INTERNO = "Error interno del servidor";
    public static final String OPERACION_EXITOSA = "Operación realizada exitosamente";
  }

  // === CONSTANTES DE API ===
  public static final class Api {
    private Api() {
      // Prevenir instanciación
    }

    public static final String BASE_PATH = "/api";
    public static final String USERS_PATH = "/api/users";
    public static final String EVENTS_PATH = "/api/events";
    public static final String VEHICLES_PATH = "/api/vehicles";
    public static final String CATEGORIES_PATH = "/api/categories";
    public static final String STAGES_PATH = "/api/stages";
    public static final String STAGE_RESULTS_PATH = "/api/stageresults";

    // Headers
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_TOTAL_COUNT = "X-Total-Count";

    // Parámetros comunes
    public static final String PARAM_PAGE = "page";
    public static final String PARAM_SIZE = "size";
    public static final String PARAM_SORT = "sort";
  }

  // === CONSTANTES DE CONFIGURACIÓN ===
  public static final class Config {
    private Config() {
      // Prevenir instanciación
    }

    public static final String PROFILE_PRODUCTION = "prod";
    public static final String PROFILE_DEVELOPMENT = "dev";
    public static final String PROFILE_TEST = "test";

    // Cache
    public static final String CACHE_EVENTS = "events";
    public static final String CACHE_CATEGORIES = "categories";
    public static final String CACHE_CLASSIFICATIONS = "classifications";

    // Timeouts
    public static final int DEFAULT_TIMEOUT_SECONDS = 30;
    public static final int FILE_UPLOAD_TIMEOUT_SECONDS = 120;
  }

  // === CONSTANTES DE LOGGING ===
  public static final class Logging {
    private Logging() {
      // Prevenir instanciación
    }

    public static final String OPERATION_START = "Iniciando operación: {}";
    public static final String OPERATION_SUCCESS = "Operación completada exitosamente: {}";
    public static final String OPERATION_ERROR = "Error en operación {}: {}";

    public static final String USER_ACTION = "Usuario {} realizó acción: {}";
    public static final String FILE_UPLOAD = "Archivo subido: {} por usuario: {}";
    public static final String FILE_DELETE = "Archivo eliminado: {}";

    public static final String AUTHENTICATION_SUCCESS = "Autenticación exitosa para: {}";
    public static final String AUTHENTICATION_FAILURE = "Fallo de autenticación para: {}";
    public static final String AUTHORIZATION_DENIED = "Acceso denegado para usuario {} en recurso: {}";
  }
}