$ErrorActionPreference = "Continue"

$ProjectRoot = Split-Path -Parent $PSScriptRoot
$LogDir = Join-Path $ProjectRoot ".logs"

function Test-Puerto {
    param([int]$Puerto)
    try {
        $cliente = New-Object System.Net.Sockets.TcpClient
        $cliente.Connect("127.0.0.1", $Puerto)
        $cliente.Close()
        return $true
    } catch {
        return $false
    }
}

if (Test-Puerto -Puerto 8080) {
    $conexion = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue |
        Select-Object -First 1
    if ($conexion) {
        Stop-Process -Id $conexion.OwningProcess -Force -ErrorAction SilentlyContinue
        Start-Sleep -Seconds 1
        Write-Host "Aplicacion detenida."
    }
} else {
    Write-Host "La aplicacion no esta en ejecucion."
}

& (Join-Path $PSScriptRoot "mysql-stop.ps1")
