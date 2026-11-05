$ErrorActionPreference = "Continue"

$ProjectRoot = Split-Path -Parent $PSScriptRoot
. (Join-Path $PSScriptRoot "local-config.ps1")
$LocalConfig = Read-LocalConfig
$MySQLHome = "C:\Program Files\MySQL\MySQL Server 8.0"
$MySQLAdmin = Join-Path $MySQLHome "bin\mysqladmin.exe"
$Port = 3307
$RootPassword = $LocalConfig["db.root.password"]

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

if (-not (Test-Puerto -Puerto $Port)) {
    Write-Host "MySQL no esta en ejecucion en el puerto $Port."
    exit 0
}

if (Test-Path $MySQLAdmin) {
    $args = @(
        "--protocol=TCP",
        "-h", "127.0.0.1",
        "-P", "$Port",
        "-u", "root"
    )
    if ($RootPassword) {
        $args += "-p$RootPassword"
    }
    $args += "shutdown"
    & $MySQLAdmin @args 2>&1 | Out-Null
    Start-Sleep -Seconds 2
}

if (Test-Puerto -Puerto $Port) {
    $conexion = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue |
        Select-Object -First 1
    if ($conexion) {
        Stop-Process -Id $conexion.OwningProcess -Force -ErrorAction SilentlyContinue
        Start-Sleep -Seconds 1
    }
}

if (Test-Puerto -Puerto $Port) {
    Write-Host "No se pudo detener MySQL en el puerto $Port."
} else {
    Write-Host "MySQL detenido."
}
