$ProjectRoot = Split-Path -Parent $PSScriptRoot
$LocalConfigPath = Join-Path $ProjectRoot "config\local.properties"

function Read-LocalConfig {
    param([string]$Path = $LocalConfigPath)

    $config = @{}
    if (-not (Test-Path -LiteralPath $Path)) {
        return $config
    }

    foreach ($line in Get-Content -LiteralPath $Path) {
        $trim = $line.Trim()
        if ($trim.Length -eq 0 -or $trim.StartsWith("#")) {
            continue
        }
        $separador = $trim.IndexOf("=")
        if ($separador -lt 1) {
            continue
        }
        $clave = $trim.Substring(0, $separador).Trim()
        $valor = $trim.Substring($separador + 1).Trim()
        $config[$clave] = $valor
    }
    return $config
}

function Save-LocalConfig {
    param(
        [hashtable]$Config,
        [string]$Path = $LocalConfigPath
    )

    New-Item -ItemType Directory -Force -Path (Split-Path -Parent $Path) | Out-Null
    $lineas = @(
        "# Archivo local ignorado por Git. No lo suba al repositorio.",
        "spring.datasource.username=$($Config['spring.datasource.username'])",
        "spring.datasource.password=$($Config['spring.datasource.password'])",
        "db.root.password=$($Config['db.root.password'])"
    )
    Set-Content -LiteralPath $Path -Value $lineas -Encoding UTF8
}

function New-RandomPassword {
    param([string]$Prefix = "Clinica_")
    return $Prefix + [guid]::NewGuid().ToString("N").Substring(0, 16)
}

function Initialize-LocalConfig {
    $config = Read-LocalConfig
    $completo = $config.ContainsKey("spring.datasource.username") -and
        $config.ContainsKey("spring.datasource.password") -and
        $config.ContainsKey("db.root.password")
    if ($completo) {
        return $config
    }

    if (Test-Path -LiteralPath (Join-Path $ProjectRoot "mysql-data\mysql")) {
        throw "Falta config/local.properties y ya existe una base local en mysql-data. Cree config/local.properties con las credenciales actuales (vea config/local.properties.example)."
    }

    $config = @{
        "spring.datasource.username" = "clinica_user"
        "spring.datasource.password" = New-RandomPassword
        "db.root.password" = New-RandomPassword
    }
    Save-LocalConfig -Config $config
    Write-Host "Se genero config/local.properties con credenciales aleatorias para la base local."
    return $config
}
