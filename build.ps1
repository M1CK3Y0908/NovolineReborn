$ErrorActionPreference = "Stop"

$ProjectRoot = $PSScriptRoot
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"

$buildDate = (Get-Date).ToString("MMddyy")
Write-Host "=========================================="
Write-Host "Building Novoline.Rat (Build $buildDate)"
Write-Host "=========================================="

# 1. Update BUILD_DATE in Novoline.java if needed
$novoFile = Join-Path $ProjectRoot "src\main\java\wtf\novoline\Novoline.java"
$utf8NoBom = New-Object System.Text.UTF8Encoding($false)
$novoContent = [System.IO.File]::ReadAllText($novoFile, $utf8NoBom)
$novoContent = $novoContent -replace 'public static final String BUILD_DATE = ".*?";', "public static final String BUILD_DATE = `"$buildDate`";"
[System.IO.File]::WriteAllText($novoFile, $novoContent, $utf8NoBom)

# 2. Compile and package fat jar
Push-Location $ProjectRoot
try {
    .\gradlew.bat bundledJar
    if ($LASTEXITCODE -ne 0) {
        throw "Gradle build failed with exit code $LASTEXITCODE"
    }
} finally {
    Pop-Location
}

# 3. Deploy outputs
$distDir = Join-Path $ProjectRoot "dist"
if (-not (Test-Path $distDir)) {
    New-Item -ItemType Directory -Path $distDir -Force | Out-Null
}

$builtJar = Join-Path $ProjectRoot "build\libs\OpenNovoline-1.0.0-all.jar"
$distJar = Join-Path $distDir "Novoline.jar"
Copy-Item $builtJar $distJar -Force
Write-Host "Built: $distJar ($([math]::Round((Get-Item $distJar).Length / 1MB, 2)) MB)"

# Copy to common Minecraft locations
$appdataMc = "$env:APPDATA\.minecraft\versions\Novoline"
if (Test-Path $appdataMc) {
    Copy-Item $distJar "$appdataMc\Novoline.jar" -Force
    Write-Host "Installed to $appdataMc"
}

$dMc = "D:\Minecraft\.minecraft\versions\Novoline"
if (Test-Path $dMc) {
    Copy-Item $distJar "$dMc\Novoline.jar" -Force
    Write-Host "Installed to $dMc"
}

# Copy to Novoline launcher runtime directories
$ratRoot = "C:\M1CK3Y\novoline.rat\versions"
if (Test-Path $ratRoot) {
    $rat111426 = Join-Path $ratRoot "Release-111426"
    if (Test-Path $rat111426) {
        Copy-Item $distJar "$rat111426\Release-111426.jar" -Force
    }
    $ratToday = Join-Path $ratRoot "Release-$buildDate"
    if (-not (Test-Path $ratToday)) {
        New-Item -ItemType Directory -Path $ratToday -Force | Out-Null
    }
    Copy-Item $distJar "$ratToday\Release-$buildDate.jar" -Force
    if ((Test-Path "$rat111426\Release-111426.json") -and (-not (Test-Path "$ratToday\Release-$buildDate.json"))) {
        $json = Get-Content "$rat111426\Release-111426.json" -Raw
        $json = $json -replace "Release-111426", "Release-$buildDate"
        Set-Content -Path "$ratToday\Release-$buildDate.json" -Value $json -Encoding UTF8
    }
    Write-Host "Installed to NovolineLauncher ($ratToday)"
}

Write-Host "=========================================="
Write-Host "BUILD COMPLETE!"
Write-Host "=========================================="
