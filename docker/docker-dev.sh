#!/bin/bash

# =======================================
# SCRIPT DE DESARROLLO - gpx RACING API
# =======================================

set -e

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Función para logging
log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')] $1${NC}"
}

warn() {
    echo -e "${YELLOW}[$(date +'%Y-%m-%d %H:%M:%S')] ⚠️  $1${NC}"
}

error() {
    echo -e "${RED}[$(date +'%Y-%m-%d %H:%M:%S')] ❌ $1${NC}"
}

# Función de ayuda
show_help() {
    echo -e "${BLUE}gpx Racing - Docker Development Helper${NC}"
    echo ""
    echo "Uso: $0 [COMANDO]"
    echo ""
    echo "Comandos disponibles:"
    echo "  up           - Iniciar todos los servicios"
    echo "  up-admin     - Iniciar servicios + pgAdmin"
    echo "  down         - Detener todos los servicios"
    echo "  restart      - Reiniciar aplicación"
    echo "  logs         - Ver logs de la aplicación"
    echo "  logs-db      - Ver logs de PostgreSQL"
    echo "  build        - Construir imagen de la aplicación"
    echo "  clean        - Limpiar contenedores y volúmenes"
    echo "  status       - Ver estado de servicios"
    echo "  shell        - Conectar a shell del contenedor de la app"
    echo "  db-shell     - Conectar a PostgreSQL"
    echo "  help         - Mostrar esta ayuda"
    echo ""
    echo "Ejemplos:"
    echo "  $0 up                    # Iniciar desarrollo"
    echo "  $0 up-admin             # Iniciar con pgAdmin"
    echo "  $0 logs                 # Ver logs en tiempo real"
    echo "  $0 restart              # Reiniciar solo la app"
}

# Verificar si Docker está corriendo
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        error "Docker no está corriendo. Por favor inicia Docker Desktop."
        exit 1
    fi
}

# Comando UP - Iniciar servicios
cmd_up() {
    log "🚀 Iniciando servicios de desarrollo..."
    check_docker
    docker-compose up -d postgres
    log "⏳ Esperando a que PostgreSQL esté listo..."
    sleep 10
    docker-compose up -d gpx-api
    log "✅ Servicios iniciados!"
    log "🌐 API disponible en: http://localhost:8080"
    log "📚 Swagger UI en: http://localhost:8080/swagger-ui.html"
    log "📊 Health check en: http://localhost:8080/actuator/health"
}

# Comando UP-ADMIN - Iniciar con pgAdmin
cmd_up_admin() {
    log "🚀 Iniciando servicios con pgAdmin..."
    check_docker
    docker-compose --profile admin up -d
    log "✅ Servicios iniciados con pgAdmin!"
    log "🌐 API disponible en: http://localhost:8080"
    log "🗄️  pgAdmin disponible en: http://localhost:5050"
    log "   Email: admin@gpx.com | Password: admin123"
}

# Comando DOWN - Detener servicios
cmd_down() {
    log "🛑 Deteniendo servicios..."
    docker-compose down
    log "✅ Servicios detenidos."
}

# Comando RESTART - Reiniciar aplicación
cmd_restart() {
    log "🔄 Reiniciando aplicación..."
    docker-compose restart gpx-api
    log "✅ Aplicación reiniciada."
}

# Comando LOGS - Ver logs
cmd_logs() {
    log "📋 Mostrando logs de la aplicación..."
    docker-compose logs -f gpx-api
}

# Comando LOGS-DB - Ver logs de base de datos
cmd_logs_db() {
    log "📋 Mostrando logs de PostgreSQL..."
    docker-compose logs -f postgres
}

# Comando BUILD - Construir imagen
cmd_build() {
    log "🔨 Construyendo imagen de la aplicación..."
    check_docker
    docker-compose build --no-cache gpx-api
    log "✅ Imagen construida exitosamente."
}

# Comando CLEAN - Limpiar
cmd_clean() {
    warn "🧹 Esto eliminará todos los contenedores y volúmenes. ¿Continuar? (y/N)"
    read -r response
    if [[ "$response" =~ ^([yY][eE][sS]|[yY])$ ]]; then
        log "🧹 Limpiando contenedores y volúmenes..."
        docker-compose down -v --remove-orphans
        docker system prune -f
        log "✅ Limpieza completada."
    else
        log "Operación cancelada."
    fi
}

# Comando STATUS - Ver estado
cmd_status() {
    log "📊 Estado de servicios:"
    docker-compose ps
}

# Comando SHELL - Conectar a contenedor
cmd_shell() {
    log "🐚 Conectando al shell del contenedor..."
    docker-compose exec gpx-api /bin/bash
}

# Comando DB-SHELL - Conectar a PostgreSQL
cmd_db_shell() {
    log "🗄️ Conectando a PostgreSQL..."
    docker-compose exec postgres psql -U postgres -d gpx_db
}

# Router principal
case "${1:-}" in
    up)
        cmd_up
        ;;
    up-admin)
        cmd_up_admin
        ;;
    down)
        cmd_down
        ;;
    restart)
        cmd_restart
        ;;
    logs)
        cmd_logs
        ;;
    logs-db)
        cmd_logs_db
        ;;
    build)
        cmd_build
        ;;
    clean)
        cmd_clean
        ;;
    status)
        cmd_status
        ;;
    shell)
        cmd_shell
        ;;
    db-shell)
        cmd_db_shell
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        error "Comando no reconocido: $1"
        echo ""
        show_help
        exit 1
        ;;
esac 