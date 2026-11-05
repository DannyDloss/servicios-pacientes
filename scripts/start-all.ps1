$ErrorActionPreference = "Stop"

$ProjectRoot = Split-Path -Parent $PSScriptRoot
. (Join-Path $PSScriptRoot "local-config.ps1")
Initialize-LocalConfig | Out-Null
$LogDir = Join-Path $ProjectRoot ".logs"
New-Item -ItemType Directory -Force -Path $LogDir | Out-Null

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

function Buscar-JavaHome {
    if ($env:JAVA_HOME) {
        $java = Join-Path $env:JAVA_HOME "bin\java.exe"
        if (Test-Path $java) {
            return $env:JAVA_HOME
        }
    }

    $candidatos = @()
    $candidatos += Get-ChildItem (Join-Path $env:USERPROFILE ".jdks") -Directory -ErrorAction SilentlyContinue |
        Sort-Object Name -Descending | ForEach-Object { $_.FullName }
    $candidatos += Get-ChildItem "C:\Program Files\Java" -Directory -ErrorAction SilentlyContinue |
        Sort-Object Name -Descending | ForEach-Object { $_.FullName }
    $candidatos += Get-ChildItem "C:\Program Files\Eclipse Adoptium" -Directory -ErrorAction SilentlyContinue |
        Sort-Object Name -Descending | ForEach-Object { $_.FullName }

    foreach ($candidato in $candidatos) {
        if (Test-Path (Join-Path $candidato "bin\java.exe")) {
            return $candidato
        }
    }
    return $null
}

$JavaHome = Buscar-JavaHome
if (-not $JavaHome) {
    throw "No se encontro un JDK. Instale JDK 17 o superior y configure JAVA_HOME."
}

$env:JAVA_HOME = $JavaHome
Write-Host "JDK utilizado: $JavaHome"

& (Join-Path $PSScriptRoot "mysql-init.ps1")
if ($LASTEXITCODE -ne 0) {
    throw "No se pudo inicializar MySQL."
}

if (Test-Puerto -Puerto 8080) {
    Write-Host "La aplicacion ya esta en ejecucion en http://localhost:8080"
    exit 0
}

$jar = Get-ChildItem (Join-Path $ProjectRoot "target\servicios-pacientes-*.jar") -ErrorAction SilentlyContinue |
    Where-Object { $_.Name -notlike "*.original" } |
    Sort-Object LastWriteTime -Descending |
    Select-Object -First 1

if (-not $jar) {
    $mvnw = Join-Path $ProjectRoot "mvnw.cmd"
    if (-not (Test-Path $mvnw)) {
        throw "No se encontro el jar ni Maven Wrapper. Ejecute primero la compilacion."
    }
    Write-Host "Compilando la aplicacion..."
    Push-Location $ProjectRoot
    try {
        & $mvnw -DskipTests package
        if ($LASTEXITCODE -ne 0) {
            throw "La compilacion fallo. Revise la salida de Maven."
        }
    } finally {
        Pop-Location
    }
    $jar = Get-ChildItem (Join-Path $ProjectRoot "target\servicios-pacientes-*.jar") |
        Where-Object { $_.Name -notlike "*.original" } |
        Sort-Object LastWriteTime -Descending |
        Select-Object -First 1
}

if (-not $jar) {
    throw "No se encontro el archivo jar de la aplicacion."
}

Write-Host "Arrancando la aplicacion..."
$appOut = Join-Path $LogDir "app.out.log"
$appErr = Join-Path $LogDir "app.err.log"
$proceso = Start-Process -FilePath (Join-Path $JavaHome "bin\java.exe") `
    -ArgumentList @("-jar", $jar.FullName) `
    -WorkingDirectory $ProjectRoot `
    -RedirectStandardOutput $appOut `
    -RedirectStandardError $appErr `
    -WindowStyle Hidden `
    -PassThru

$proceso.Id | Set-Content (Join-Path $LogDir "app.pid")

$inicio = Get-Date
$limite = $inicio.AddSeconds(90)
$respondio = $false
while ((Get-Date) -lt $limite) {
    if (Test-Puerto -Puerto 8080) {
        try {
            $respuesta = Invoke-WebRequest -Uri "http://localhost:8080/" -UseBasicParsing -TimeoutSec 5
            if ($respuesta.StatusCode -eq 200) {
                $respondio = $true
                break
            }
        } catch {
        }
    }
    Start-Sleep -Seconds 1
}

if (-not $respondio) {
    Write-Host "La aplicacion no respondio a tiempo. Revise los registros:"
    Write-Host "  $appOut"
    Write-Host "  $appErr"
    exit 1
}

Write-Host ""
Write-Host "Aplicacion lista."
Write-Host "  URL: http://localhost:8080"
Write-Host "  Log: $appOut"
