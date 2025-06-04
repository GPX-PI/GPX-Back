# Script para probar tests específicos después de las correcciones
Write-Host "🧪 Ejecutando tests corregidos..." -ForegroundColor Green

# Test del UserController con getGoogleLoginUrl
Write-Host "`n1. Probando UserControllerTests#getGoogleLoginUrl_shouldReturnLoginUrl..." -ForegroundColor Yellow
mvn test -Dtest="UserControllerTests#getGoogleLoginUrl_shouldReturnLoginUrl" -q

# Test del OAuth2Controller (sin el método que no existe)
Write-Host "`n2. Probando OAuth2ControllerTests..." -ForegroundColor Yellow
mvn test -Dtest="OAuth2ControllerTests" -q

# Test del EventCategoryController
Write-Host "`n3. Probando EventCategoryControllerTests..." -ForegroundColor Yellow
mvn test -Dtest="EventCategoryControllerTests" -q

Write-Host "`n✅ Tests completados. Revisa los resultados arriba." -ForegroundColor Green 