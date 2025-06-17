# SonarCloud Coverage Analysis
# Ejecuta tests, genera cobertura con JaCoCo y sube análisis a SonarCloud

Write-Host "📈 SonarCloud Coverage Analysis" -ForegroundColor Cyan
Write-Host "Ejecuta tests, genera cobertura con JaCoCo y sube análisis a SonarCloud"
Write-Host ""

# Paso 1: Verificar configuración del proyecto
Write-Host "🔍 [1/6] Verificando configuración del proyecto..." -ForegroundColor Cyan
Write-Host ""

if (Test-Path "sonar-project.properties") {
    Write-Host "✅ Archivo sonar-project.properties encontrado" -ForegroundColor Green
    Get-Content "sonar-project.properties" | Select-String "sonar.organization|sonar.projectKey" | ForEach-Object {
        Write-Host "   $_" -ForegroundColor Gray
    }
} else {
    Write-Host "❌ No se encontró sonar-project.properties" -ForegroundColor Red
    Write-Host "💡 Ejecuta .warp/sonarcloud-setup.ps1 para ayuda" -ForegroundColor Yellow
    exit 1
}

if (Test-Path "pom.xml") {
    Write-Host "✅ Proyecto Maven detectado" -ForegroundColor Green
} else {
    Write-Host "❌ No se encontró pom.xml" -ForegroundColor Red
    exit 1
}

Write-Host ""

# Paso 2: Ejecutar tests y generar cobertura
Write-Host "🧪 [2/6] Ejecutando tests y generando reporte de cobertura..." -ForegroundColor Cyan
Write-Host ""

if (Test-Path "mvnw.cmd") {
    Write-Host "🔧 Usando Maven Wrapper..." -ForegroundColor Blue
    & .\mvnw.cmd clean test jacoco:report
} elseif (Get-Command mvn -ErrorAction SilentlyContinue) {
    Write-Host "🔧 Usando Maven global..." -ForegroundColor Blue
    mvn clean test jacoco:report
} else {
    Write-Host "❌ No se encontró Maven o Maven Wrapper" -ForegroundColor Red
    exit 1
}

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Error ejecutando tests" -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host ""

# Paso 3: Verificar reporte de cobertura
Write-Host "📊 [3/6] Verificando reporte de cobertura..." -ForegroundColor Cyan
Write-Host ""

if (Test-Path "target/site/jacoco/jacoco.xml") {
    Write-Host "✅ Reporte de JaCoCo generado correctamente" -ForegroundColor Green
    $fileSize = (Get-Item "target/site/jacoco/jacoco.xml").Length
    Write-Host "📁 Tamaño del reporte: $fileSize bytes" -ForegroundColor Blue
} else {
    Write-Host "❌ No se generó el reporte de cobertura" -ForegroundColor Red
    Write-Host "💡 Verifica que JaCoCo esté configurado correctamente en pom.xml" -ForegroundColor Yellow
    exit 1
}

Write-Host ""

# Paso 4: Configurar variables de SonarCloud
Write-Host "⚙️ [4/6] Configurando variables de entorno..." -ForegroundColor Cyan
Write-Host ""

# Verificar que las variables estén definidas
if (-not $env:SONAR_TOKEN) {
    Write-Host "❌ SONAR_TOKEN no está definido" -ForegroundColor Red
    Write-Host "💡 Ejecuta: `$env:SONAR_TOKEN='tu_token_aqui'" -ForegroundColor Yellow
    exit 1
}

if (-not $env:SONAR_ORGANIZATION) {
    Write-Host "📋 SONAR_ORGANIZATION no definido, usando valor del archivo de propiedades" -ForegroundColor Yellow
}

if (-not $env:SONAR_PROJECT_KEY) {
    Write-Host "📋 SONAR_PROJECT_KEY no definido, usando valor del archivo de propiedades" -ForegroundColor Yellow
}

Write-Host "✅ Variables configuradas" -ForegroundColor Green
Write-Host ""

# Paso 5: Ejecutar análisis de SonarCloud
Write-Host "🚀 [5/6] Ejecutando análisis de SonarCloud..." -ForegroundColor Cyan
Write-Host ""

$sonarCmd = @(
    "sonar:sonar"
)

if ($env:SONAR_ORGANIZATION) {
    $sonarCmd += "-Dsonar.organization=$env:SONAR_ORGANIZATION"
}

if ($env:SONAR_PROJECT_KEY) {
    $sonarCmd += "-Dsonar.projectKey=$env:SONAR_PROJECT_KEY"
}

$sonarCmd += "-Dsonar.host.url=https://sonarcloud.io"

Write-Host "🔧 Comando a ejecutar: $($sonarCmd -join ' ')" -ForegroundColor Blue
Write-Host ""

if (Test-Path "mvnw.cmd") {
    & .\mvnw.cmd @sonarCmd
} else {
    & mvn @sonarCmd
}

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Análisis completado exitosamente" -ForegroundColor Green
    Write-Host "🌐 Revisa los resultados en SonarCloud" -ForegroundColor Blue
} else {
    Write-Host "❌ Error en el análisis de SonarCloud" -ForegroundColor Red
    exit 1
}

Write-Host ""

# Paso 6: Mostrar resumen
Write-Host "📈 [6/6] Resumen del análisis" -ForegroundColor Cyan
Write-Host ""
Write-Host "===== RESUMEN DEL ANÁLISIS =====" -ForegroundColor White -BackgroundColor DarkBlue
Write-Host "✅ Tests ejecutados y cobertura generada" -ForegroundColor Green
Write-Host "✅ Análisis subido a SonarCloud" -ForegroundColor Green
Write-Host ""
Write-Host "🔗 Enlaces útiles:" -ForegroundColor Yellow

# Extraer información del archivo de propiedades
$org = (Get-Content "sonar-project.properties" | Select-String "sonar.organization=" | ForEach-Object { $_.Line.Split('=')[1] })
$projectKey = (Get-Content "sonar-project.properties" | Select-String "sonar.projectKey=" | ForEach-Object { $_.Line.Split('=')[1] })

if ($org -and $projectKey) {
    Write-Host "   Dashboard: https://sonarcloud.io/dashboard?id=$projectKey" -ForegroundColor Gray
    Write-Host "   Proyecto: https://sonarcloud.io/project/overview?id=$projectKey" -ForegroundColor Gray
}

Write-Host ""
Write-Host "💡 Tip: Guarda este workflow para futuros análisis" -ForegroundColor Blue
Write-Host ""
Write-Host "✅ Workflow completado exitosamente" -ForegroundColor Green

