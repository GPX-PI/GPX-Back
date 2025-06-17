# SonarCloud Setup Helper
# Ayuda a configurar un proyecto para SonarCloud desde cero

Write-Host "🗺️ SonarCloud Setup Helper" -ForegroundColor Cyan
Write-Host "Ayuda a configurar un proyecto para SonarCloud desde cero"
Write-Host ""

# Paso 1: Verificar tipo de proyecto
Write-Host "🔍 [1/4] Verificando tipo de proyecto..." -ForegroundColor Cyan
Write-Host ""

if (Test-Path "pom.xml") {
    Write-Host "✅ Proyecto Maven detectado" -ForegroundColor Green
    $projectType = "maven"
} elseif (Test-Path "build.gradle" -or Test-Path "build.gradle.kts") {
    Write-Host "✅ Proyecto Gradle detectado" -ForegroundColor Green
    $projectType = "gradle"
} elseif (Test-Path "package.json") {
    Write-Host "✅ Proyecto Node.js/JavaScript detectado" -ForegroundColor Green
    $projectType = "nodejs"
} else {
    Write-Host "❌ Tipo de proyecto no reconocido" -ForegroundColor Red
    Write-Host "💡 Este workflow está optimizado para Maven, Gradle o Node.js" -ForegroundColor Yellow
    exit 1
}

Write-Host "🏷️ Tipo de proyecto: $projectType" -ForegroundColor Blue
Write-Host ""

# Paso 2: Verificar configuración existente
Write-Host "🗺️ [2/4] Verificando configuración actual..." -ForegroundColor Cyan
Write-Host ""

if (Test-Path "sonar-project.properties") {
    Write-Host "✅ Ya existe sonar-project.properties" -ForegroundColor Green
    Write-Host "📝 Contenido actual:" -ForegroundColor Yellow
    Get-Content "sonar-project.properties" | Where-Object { $_ -match "sonar\.(organization|projectKey|projectName)" } | ForEach-Object {
        Write-Host "   $_" -ForegroundColor Gray
    }
} else {
    Write-Host "⚠️ No existe sonar-project.properties" -ForegroundColor Yellow
    Write-Host "📝 Necesitarás crear este archivo" -ForegroundColor Yellow
}

if (Test-Path "pom.xml") {
    $jacocoPlugin = Select-String -Path "pom.xml" -Pattern "jacoco-maven-plugin" -Quiet
    $sonarPlugin = Select-String -Path "pom.xml" -Pattern "sonar-maven-plugin" -Quiet
    
    if ($jacocoPlugin) {
        Write-Host "✅ Plugin JaCoCo encontrado en pom.xml" -ForegroundColor Green
    } else {
        Write-Host "⚠️ Plugin JaCoCo no encontrado en pom.xml" -ForegroundColor Yellow
    }
    
    if ($sonarPlugin) {
        Write-Host "✅ Plugin SonarCloud encontrado en pom.xml" -ForegroundColor Green
    } else {
        Write-Host "⚠️ Plugin SonarCloud no encontrado en pom.xml" -ForegroundColor Yellow
    }
}
Write-Host ""

# Paso 3: Proporcionar guía de configuración
Write-Host "📋 [3/4] Guía de configuración" -ForegroundColor Cyan
Write-Host ""
Write-Host "===== GUÍA DE CONFIGURACIÓN =====" -ForegroundColor White -BackgroundColor DarkBlue
Write-Host ""

if (-not (Test-Path "sonar-project.properties")) {
    Write-Host "📄 1. Crear sonar-project.properties:" -ForegroundColor Yellow
    Write-Host "   sonar.organization=TU_ORGANIZACION" -ForegroundColor Gray
    Write-Host "   sonar.projectKey=TU_PROYECTO_KEY" -ForegroundColor Gray
    Write-Host "   sonar.projectName=Nombre de tu proyecto" -ForegroundColor Gray
    Write-Host "   sonar.projectVersion=1.0" -ForegroundColor Gray
    Write-Host "   sonar.sources=src/main/java" -ForegroundColor Gray
    Write-Host "   sonar.tests=src/test/java" -ForegroundColor Gray
    Write-Host "   sonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml" -ForegroundColor Gray
    Write-Host ""
}

Write-Host "🔑 2. Configurar token de SonarCloud:" -ForegroundColor Yellow
Write-Host "   - Ve a https://sonarcloud.io/account/security/" -ForegroundColor Gray
Write-Host "   - Genera un nuevo token" -ForegroundColor Gray
Write-Host "   - Ejecuta: `$env:SONAR_TOKEN='tu_token_aqui'" -ForegroundColor Gray
Write-Host ""

if (Test-Path "pom.xml") {
    Write-Host "⚙️ 3. Para proyectos Maven, asegúrate de tener estos plugins en pom.xml:" -ForegroundColor Yellow
    Write-Host "   - jacoco-maven-plugin (para cobertura)" -ForegroundColor Gray
    Write-Host "   - sonar-maven-plugin (para análisis)" -ForegroundColor Gray
    Write-Host ""
}

Write-Host "🚀 4. Una vez configurado, ejecuta:" -ForegroundColor Yellow
Write-Host "   .warp/sonarcloud-coverage.ps1" -ForegroundColor Gray
Write-Host ""
Write-Host "📚 Para más información: https://docs.sonarcloud.io/" -ForegroundColor Gray
Write-Host ""

# Paso 4: Probar conexión básica
Write-Host "🌐 [4/4] Probando conectividad..." -ForegroundColor Cyan
Write-Host ""

try {
    $response = Invoke-WebRequest -Uri "https://sonarcloud.io/api/system/status" -Method GET -TimeoutSec 10
    if ($response.StatusCode -eq 200) {
        Write-Host "✅ SonarCloud es accesible" -ForegroundColor Green
    }
} catch {
    Write-Host "⚠️ No se pudo conectar a SonarCloud" -ForegroundColor Yellow
    Write-Host "   Verifica tu conexión a internet" -ForegroundColor Gray
}

if ($env:SONAR_TOKEN) {
    Write-Host "✅ Variable SONAR_TOKEN está definida" -ForegroundColor Green
} else {
    Write-Host "⚠️ Variable SONAR_TOKEN no está definida" -ForegroundColor Yellow
    Write-Host "   Recuerda configurarla antes de ejecutar el análisis" -ForegroundColor Gray
}

Write-Host ""
Write-Host "✅ Verificación de configuración completada" -ForegroundColor Green

