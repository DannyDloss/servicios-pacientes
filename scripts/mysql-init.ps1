$ErrorActionPreference = "Stop"

$ProjectRoot = Split-Path -Parent $PSScriptRoot
. (Join-Path $PSScriptRoot "local-config.ps1")
$LocalConfig = Initialize-LocalConfig
$MySQLHome = "C:\Program Files\MySQL\MySQL Server 8.0"
$MySQLBin = Join-Path $MySQLHome "bin"
$Mysqld = Join-Path $MySQLBin "mysqld.exe"
$Mysql = Join-Path $MySQLBin "mysql.exe"
$DataDir = Join-Path $ProjectRoot "mysql-data"
$LogDir = Join-Path $ProjectRoot "mysql-logs"
$Port = 3307
$RootPassword = $LocalConfig["db.root.password"]
$Database = "clinica_db"
$AppUser = $LocalConfig["spring.datasource.username"]
$AppPassword = $LocalConfig["spring.datasource.password"]

if (-not $AppUser -or -not $AppPassword -or -not $RootPassword) {
    throw "config/local.properties debe definir spring.datasource.username, spring.datasource.password y db.root.password."
}

if (-not (Test-Path $Mysqld)) {
    throw "No se encontro MySQL en '$MySQLHome'. Verifique la instalacion."
}

New-Item -ItemType Directory -Force -Path $DataDir | Out-Null
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

function Invoke-Native {
    param(
        [string]$FilePath,
        [string[]]$Arguments,
        [string]$OutFile,
        [string]$ErrFile
    )
    $anterior = $ErrorActionPreference
    $ErrorActionPreference = "SilentlyContinue"
    try {
        & $FilePath @Arguments 1> $OutFile 2> $ErrFile
        return $LASTEXITCODE
    } finally {
        $ErrorActionPreference = $anterior
    }
}

$inicializado = Test-Path (Join-Path $DataDir "mysql")
if (-not $inicializado) {
    Write-Host "Inicializando la instancia local de MySQL..."
    $initOut = Join-Path $LogDir "mysql-init.out.log"
    $initErr = Join-Path $LogDir "mysql-init.err.log"
    $initArgs = @(
        "--no-defaults",
        "--initialize-insecure",
        "--basedir=$MySQLHome",
        "--datadir=$DataDir",
        "--console"
    )
    $initCode = Invoke-Native -FilePath $Mysqld -Arguments $initArgs -OutFile $initOut -ErrFile $initErr
    if ($initCode -ne 0) {
        Get-Content $initErr -ErrorAction SilentlyContinue | Select-Object -Last 30
        throw "No se pudo inicializar MySQL. Revise $initErr"
    }
}

if (-not (Test-Puerto -Puerto $Port)) {
    Write-Host "Arrancando MySQL en el puerto $Port..."
    $outLog = Join-Path $LogDir "mysql.out.log"
    $errLog = Join-Path $LogDir "mysql.err.log"
    $startArgs = @(
        "--no-defaults",
        "--basedir=`"$MySQLHome`"",
        "--datadir=`"$DataDir`"",
        "--port=$Port",
        "--bind-address=127.0.0.1",
        "--mysqlx=0",
        "--console"
    )
    Start-Process -FilePath $Mysqld -ArgumentList $startArgs `
        -RedirectStandardOutput $outLog -RedirectStandardError $errLog `
        -WindowStyle Hidden | Out-Null

    $limite = (Get-Date).AddSeconds(60)
    while (-not (Test-Puerto -Puerto $Port) -and (Get-Date) -lt $limite) {
        Start-Sleep -Milliseconds 500
    }
    if (-not (Test-Puerto -Puerto $Port)) {
        Get-Content $errLog -ErrorAction SilentlyContinue | Select-Object -Last 30
        throw "MySQL no pudo arrancar. Revise $errLog"
    }
}

$baseArgs = @(
    "--protocol=TCP",
    "-h", "127.0.0.1",
    "-P", "$Port",
    "-u", "root",
    "--default-character-set=utf8mb4"
)

$rootTestOut = Join-Path $LogDir "mysql-root-test.out.log"
$rootTestErr = Join-Path $LogDir "mysql-root-test.err.log"
$rootTestCode = Invoke-Native -FilePath $Mysql `
    -Arguments ($baseArgs + @("--execute=SELECT 1;")) `
    -OutFile $rootTestOut -ErrFile $rootTestErr

$rootRequierePassword = $rootTestCode -ne 0
if ($rootRequierePassword) {
    $baseArgs += "-p$RootPassword"
}

$sqlFile = Join-Path $LogDir "mysql-setup.sql"
$sql = @"
ALTER USER 'root'@'localhost' IDENTIFIED BY '$RootPassword';
CREATE DATABASE IF NOT EXISTS $Database CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS '$AppUser'@'%' IDENTIFIED BY '$AppPassword';
ALTER USER '$AppUser'@'%' IDENTIFIED BY '$AppPassword';
GRANT ALL PRIVILEGES ON $Database.* TO '$AppUser'@'%';
FLUSH PRIVILEGES;
"@
Set-Content -LiteralPath $sqlFile -Value $sql -Encoding UTF8

$setupOut = Join-Path $LogDir "mysql-setup.out.log"
$setupErr = Join-Path $LogDir "mysql-setup.err.log"
$setupCode = Invoke-Native -FilePath $Mysql `
    -Arguments ($baseArgs + @("--execute=source $sqlFile")) `
    -OutFile $setupOut -ErrFile $setupErr
if ($setupCode -ne 0) {
    Get-Content $setupErr -ErrorAction SilentlyContinue | Select-Object -Last 30
    throw "No se pudo configurar la base de datos. Verifique la contrasena del usuario root local."
}

$appArgs = @(
    "--protocol=TCP",
    "-h", "127.0.0.1",
    "-P", "$Port",
    "-u", $AppUser,
    "-p$AppPassword",
    "--default-character-set=utf8mb4",
    "--execute=SELECT DATABASE();"
)
$appOut = Join-Path $LogDir "mysql-app-test.out.log"
$appErr = Join-Path $LogDir "mysql-app-test.err.log"
$appCode = Invoke-Native -FilePath $Mysql `
    -Arguments ($appArgs + @($Database)) `
    -OutFile $appOut -ErrFile $appErr
if ($appCode -ne 0) {
    Get-Content $appErr -ErrorAction SilentlyContinue | Select-Object -Last 30
    throw "No se pudo validar el acceso con el usuario de aplicacion."
}

Write-Host ""
Write-Host "MySQL listo."
Write-Host "  Host:     127.0.0.1"
Write-Host "  Puerto:   $Port"
Write-Host "  Base:     $Database"
Write-Host "  Usuario:  $AppUser"
Write-Host "  Password: definida en config/local.properties"
