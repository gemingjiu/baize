# baize local start script (Windows / PowerShell)
# Usage: run  .\start-backend.ps1  from repo root
# Notes:
#   - env vars match .run/*.run.xml (IDEA run configurations)
#   - start order: system(8020) -> auth(8010) -> gateway(8000)
#   - logs go to %TEMP%\baize-logs\*.log
#   - stop: Get-Process java | Stop-Process

$ErrorActionPreference = 'Stop'

$JAVA_HOME = 'D:\tool\jdk\jdk-17'
$BACKEND  = Join-Path $PSScriptRoot 'baize-backend'

# Nacos env vars, same as .run/*.run.xml
$env:JAVA_HOME         = $JAVA_HOME
$env:NACOS_SERVER_ADDR = '192.168.5.4:30848'
$env:NACOS_NAMESPACE   = 'baize'
$env:NACOS_USERNAME    = 'nacos'
$env:NACOS_PASSWORD    = 'nacos'

$logDir = Join-Path $env:TEMP 'baize-logs'
if (-not (Test-Path $logDir)) { New-Item -ItemType Directory -Path $logDir | Out-Null }

# service definitions: module dir -> port
$services = @(
    @{ Name = 'baize-system';  Module = 'baize-modules\baize-system'; Port = 8020 },
    @{ Name = 'baize-gen';     Module = 'baize-modules\baize-gen';    Port = 9300 },
    @{ Name = 'baize-auth';    Module = 'baize-auth';                Port = 8010 },
    @{ Name = 'baize-gateway'; Module = 'baize-gateway';             Port = 8000 }
)

function Test-Port([int]$port) {
    (Test-NetConnection -ComputerName 'localhost' -Port $port -InformationLevel Quiet -WarningAction SilentlyContinue)
}

foreach ($svc in $services) {
    if (Test-Port $svc.Port) {
        Write-Host ("[{0}] port {1} already listening, skip" -f $svc.Name, $svc.Port) -ForegroundColor Yellow
        continue
    }

    $jarDir = Join-Path $BACKEND ($svc.Module + '\target')
    $jar = Get-ChildItem -Path $jarDir -Filter '*.jar' -ErrorAction SilentlyContinue |
           Where-Object { $_.Name -notmatch 'sources|javadoc' } |
           Sort-Object LastWriteTime -Descending | Select-Object -First 1

    if (-not $jar) {
        Write-Host ("[{0}] jar not found, run mvn install first" -f $svc.Name) -ForegroundColor Red
        exit 1
    }

    $log = Join-Path $logDir ($svc.Name + '.log')
    Write-Host ("[{0}] starting... jar={1} log={2}" -f $svc.Name, $jar.Name, $log) -ForegroundColor Cyan

    Start-Process -FilePath (Join-Path $JAVA_HOME 'bin\java.exe') `
        -ArgumentList '-Dfile.encoding=UTF-8', '-Dstdout.encoding=UTF-8', '-Xmx512m', '-jar', $jar.FullName `
        -WorkingDirectory $jarDir `
        -RedirectStandardOutput $log `
        -RedirectStandardError ($log + '.err') `
        -WindowStyle Hidden

    # wait for port (max 90s)
    $ok = $false
    for ($i = 0; $i -lt 90; $i++) {
        Start-Sleep -Seconds 1
        if (Test-Port $svc.Port) { $ok = $true; break }
    }
    if ($ok) {
        Write-Host ("[{0}] ready http://localhost:{1}" -f $svc.Name, $svc.Port) -ForegroundColor Green
    } else {
        Write-Host ("[{0}] not ready in 90s, check log {1}" -f $svc.Name, $log) -ForegroundColor Red
    }
}

Write-Host ""
Write-Host ("done. logs: {0}" -f $logDir) -ForegroundColor Cyan
