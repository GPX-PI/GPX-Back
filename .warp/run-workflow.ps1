# Script para ejecutar workflows de SonarCloud
# Uso: .warp/run-workflow.ps1 -WorkflowName "sonarcloud-coverage"

param(
    [Parameter(Mandatory=$true)]
    [ValidateSet("sonarcloud-coverage", "sonarcloud-setup")]
    [string]$WorkflowName
)

# Colores para output
function Write-Info {
    param([string]$Message)
    Write-Host $Message -ForegroundColor Cyan
}

function Write-Success {
    param([string]$Message)
    Write-Host $Message -ForegroundColor Green
}

function Write-Warning {
    param([string]$Message)
    Write-Host $Message -ForegroundColor Yellow
}

function Write-Error {
    param([string]$Message)
    Write-Host $Message -ForegroundColor Red
}

# Función para leer y ejecutar un workflow YAML
function Invoke-WarpWorkflow {
    param([string]$WorkflowPath)
    
    if (-not (Test-Path $WorkflowPath)) {
        Write-Error "❌ Workflow no encontrado: $WorkflowPath"
        exit 1
    }
    
    Write-Info "🚀 Ejecutando workflow: $WorkflowName"
    Write-Info "📁 Archivo: $WorkflowPath"
    Write-Host ""
    
    # Leer el archivo YAML (procesamiento básico)
    $content = Get-Content $WorkflowPath -Raw
    
    # Extraer información básica
    $nameMatch = [regex]::Match($content, 'name:\s*"([^"]+)"')
    $descMatch = [regex]::Match($content, 'description:\s*"([^"]+)"')
    
    if ($nameMatch.Success) {
        Write-Info "📋 $($nameMatch.Groups[1].Value)"
    }
    if ($descMatch.Success) {
        Write-Info "📝 $($descMatch.Groups[1].Value)"
    }
    Write-Host ""
    
    # Extraer y ejecutar pasos
    $steps = [regex]::Matches($content, '(?ms)- name:\s*"([^"]+)"\s*command:\s*\|\s*([^\r\n]+(?:\r?\n(?!\s*-\s*name:).*)*)')
    
    $stepNumber = 1
    foreach ($step in $steps) {
        $stepName = $step.Groups[1].Value.Trim()
        $stepCommand = $step.Groups[2].Value.Trim()
        
        Write-Info "[$stepNumber/$($steps.Count)] $stepName"
        Write-Host ""
        
        try {
            # Ejecutar el comando PowerShell
            Invoke-Expression $stepCommand
            
            if ($LASTEXITCODE -ne 0 -and $LASTEXITCODE -ne $null) {
                Write-Error "❌ Error en el paso: $stepName"
                exit $LASTEXITCODE
            }
        }
        catch {
            Write-Error "❌ Error ejecutando paso '$stepName': $($_.Exception.Message)"
            exit 1
        }
        
        Write-Host ""
        $stepNumber++
    }
    
    Write-Success "✅ Workflow completado exitosamente"
}

# Directorio de workflows
$workflowsDir = Join-Path $PSScriptRoot "workflows"
$workflowFile = Join-Path $workflowsDir "$WorkflowName.yaml"

# Ejecutar workflow
Invoke-WarpWorkflow -WorkflowPath $workflowFile

