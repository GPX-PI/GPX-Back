# Script para probar OAuth2ControllerTests específicamente
Write-Host "🔐 Ejecutando tests de OAuth2Controller..." -ForegroundColor Green

# Test de encoding que estaba fallando
Write-Host "`n1. Probando oauth2LoginSuccess_shouldProperlyEncodeFirstName..." -ForegroundColor Yellow
mvn test -Dtest="OAuth2ControllerTests#oauth2LoginSuccess_shouldProperlyEncodeFirstName" -q

# Test de caracteres especiales
Write-Host "`n2. Probando oauth2LoginSuccess_shouldEncodeSpecialCharactersInFirstName..." -ForegroundColor Yellow
mvn test -Dtest="OAuth2ControllerTests#oauth2LoginSuccess_shouldEncodeSpecialCharactersInFirstName" -q

# Todos los tests de OAuth2Controller
Write-Host "`n3. Probando todos los tests de OAuth2Controller..." -ForegroundColor Yellow
mvn test -Dtest="OAuth2ControllerTests" -q

Write-Host "`n✅ Tests de OAuth2 completados. Revisa los resultados arriba." -ForegroundColor Green 