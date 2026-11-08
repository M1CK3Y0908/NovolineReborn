$ErrorActionPreference = "Stop"

$ProjectRoot = $PSScriptRoot
$DistDir = Join-Path $ProjectRoot "dist\Novoline"
$MinecraftVersionsDir = Join-Path $env:APPDATA ".minecraft\versions\Novoline"
$StagingDir = Join-Path $ProjectRoot "build\staging"
$OutClassesDir = Join-Path $ProjectRoot "out\production\novoline-eclipse-ready"

Write-Host "=========================================="
Write-Host "1. Recompiling modified sources"
Write-Host "=========================================="

$cp = "$OutClassesDir;$ProjectRoot\libs\*"
javac -encoding UTF-8 -cp $cp -d $OutClassesDir cc\novoline\modules\AbstractModule.java cc\novoline\modules\visual\HUD.java cc\novoline\modules\visual\Watermark.java cc\novoline\modules\visual\Information.java cc\novoline\modules\visual\ArmorHUD.java cc\novoline\modules\visual\PotionHUD.java cc\novoline\modules\visual\TargetList.java cc\novoline\modules\visual\InventoryHUD.java cc\novoline\Initializer.java net\minecraft\client\gui\GuiChat.java net\Main.java Start.java

if ($LASTEXITCODE -ne 0) {
    throw "Compilation failed!"
}

Write-Host "=========================================="
Write-Host "2. Preparing staging directory: $StagingDir"
Write-Host "=========================================="

if (Test-Path $StagingDir) {
    Remove-Item -Recurse -Force $StagingDir
}
New-Item -ItemType Directory -Path $StagingDir -Force | Out-Null

Write-Host "=========================================="
Write-Host "3. Copying compiled classes & resources"
Write-Host "=========================================="

# Copy core compiled packages
Copy-Item -Recurse (Join-Path $OutClassesDir "cc") (Join-Path $StagingDir "cc")
Copy-Item -Recurse (Join-Path $OutClassesDir "net") (Join-Path $StagingDir "net")
Copy-Item -Recurse (Join-Path $OutClassesDir "ninja") (Join-Path $StagingDir "ninja")
Copy-Item -Recurse (Join-Path $OutClassesDir "viaversion") (Join-Path $StagingDir "viaversion")
Copy-Item -Recurse (Join-Path $OutClassesDir "assets") (Join-Path $StagingDir "assets")

# Copy root classes and files
Copy-Item (Join-Path $OutClassesDir "Start.class") (Join-Path $StagingDir "Start.class")
Copy-Item (Join-Path $OutClassesDir "log4j2.xml") (Join-Path $StagingDir "log4j2.xml")
Copy-Item (Join-Path $OutClassesDir "pack.png") (Join-Path $StagingDir "pack.png")

Write-Host "=========================================="
Write-Host "4. Extracting custom dependencies into staging"
Write-Host "=========================================="

$customLibs = @(
    "annotations-18.0.0.jar",
    "api-4.0.1.jar",
    "bungeecord_chat-1.16_R0.4.jar",
    "checker-3.1.0.jar",
    "config-1.3.1.jar",
    "fastutil-8.3.0.jar",
    "gson-2.8.9.jar",
    "Intent-HWID-API-v1.1.jar",
    "json-20190722.jar",
    "kotlin-reflect-1.4.0.jar",
    "kotlin-stdlib-1.4.0.jar",
    "kotlin-stdlib-jdk7-1.4.0.jar",
    "kotlin-stdlib-jdk8-1.4.0.jar",
    "netty-1.8.8.jar",
    "opennbt-1.2_SNAPSHOT.jar",
    "slf4j-api-1.7.25.jar",
    "slf4j-simple-1.8.0.jar",
    "snakeyaml-1.18.jar",
    "vecmath-1.5.3.jar",
    "websockets-1.4.1.jar"
)

Add-Type -AssemblyName System.IO.Compression.FileSystem

foreach ($lib in $customLibs) {
    $libPath = Join-Path $ProjectRoot "libs\$lib"
    Write-Host "Extracting $lib..."
    $zip = [System.IO.Compression.ZipFile]::OpenRead($libPath)
    foreach ($entry in $zip.Entries) {
        $entryName = $entry.FullName
        # Skip META-INF signature files and manifests
        if ($entryName.StartsWith("META-INF/") -or $entryName.StartsWith("META-INF\")) {
            if ($entryName -like "*.SF" -or $entryName -like "*.DSA" -or $entryName -like "*.RSA" -or $entry.Name -eq "MANIFEST.MF") {
                continue
            }
        }
        if ($entryName.EndsWith("/")) {
            continue
        }

        $targetFile = Join-Path $StagingDir $entryName
        $targetDir = [System.IO.Path]::GetDirectoryName($targetFile)
        if (-not (Test-Path $targetDir)) {
            New-Item -ItemType Directory -Path $targetDir -Force | Out-Null
        }
        # Do not overwrite project classes if already present
        if (-not (Test-Path $targetFile)) {
            [System.IO.Compression.ZipFileExtensions]::ExtractToFile($entry, $targetFile)
        }
    }
    $zip.Dispose()
}

Write-Host "=========================================="
Write-Host "5. Setting up META-INF/MANIFEST.MF"
Write-Host "=========================================="

$metaInf = Join-Path $StagingDir "META-INF"
if (-not (Test-Path $metaInf)) {
    New-Item -ItemType Directory -Path $metaInf -Force | Out-Null
}
Get-ChildItem -Path $metaInf -Include "*.SF","*.DSA","*.RSA" -Recurse | Remove-Item -Force -ErrorAction SilentlyContinue

$manifestContent = "Manifest-Version: 1.0`r`nMain-Class: net.minecraft.client.main.Main`r`n`r`n"
[System.IO.File]::WriteAllText((Join-Path $metaInf "MANIFEST.MF"), $manifestContent, [System.Text.Encoding]::ASCII)

Write-Host "=========================================="
Write-Host "6. Packaging Novoline.jar"
Write-Host "=========================================="

if (-not (Test-Path $DistDir)) {
    New-Item -ItemType Directory -Path $DistDir -Force | Out-Null
}

$jarPath = Join-Path $DistDir "Novoline.jar"
if (Test-Path $jarPath) {
    Remove-Item -Force $jarPath
}

Push-Location $StagingDir
try {
    jar cvfm "$jarPath" "META-INF\MANIFEST.MF" * | Out-Null
} finally {
    Pop-Location
}

$jarFileInfo = Get-Item $jarPath
Write-Host "Generated Novoline.jar: $($jarFileInfo.Length / 1MB) MB at $jarPath"

Write-Host "=========================================="
Write-Host "7. Generating Novoline.json"
Write-Host "=========================================="

$jsonContent = @'
{
  "id": "Novoline",
  "time": "2026-11-08T00:00:00+08:00",
  "releaseTime": "2026-11-08T00:00:00+08:00",
  "type": "release",
  "mainClass": "net.minecraft.client.main.Main",
  "minecraftArguments": "--username ${auth_player_name} --version ${version_name} --gameDir ${game_directory} --assetsDir ${assets_root} --assetIndex ${assets_index_name} --uuid ${auth_uuid} --accessToken ${auth_access_token} --userProperties ${user_properties} --userType ${user_type}",
  "minimumLauncherVersion": 14,
  "assets": "1.8",
  "assetIndex": {
    "id": "1.8",
    "totalSize": 0,
    "url": "https://launchermeta.mojang.com/v1/packages/f6ad102bcaa53b1a58358f16e376d548d4a66e44/1.8.json"
  },
  "downloads": {},
  "libraries": [
    {
      "name": "net.sf.trove4j:trove4j:3.0.3"
    },
    {
      "name": "com.ibm.icu:icu4j-core-mojang:51.2"
    },
    {
      "name": "net.sf.jopt-simple:jopt-simple:4.6"
    },
    {
      "name": "com.paulscode:codecjorbis:20101023"
    },
    {
      "name": "com.paulscode:codecwav:20101023"
    },
    {
      "name": "com.paulscode:libraryjavasound:20101123"
    },
    {
      "name": "com.paulscode:librarylwjglopenal:20100824"
    },
    {
      "name": "com.paulscode:soundsystem:20120107"
    },
    {
      "name": "io.netty:netty-all:4.0.23.Final"
    },
    {
      "name": "com.google.guava:guava:17.0"
    },
    {
      "name": "org.apache.commons:commons-lang3:3.3.2"
    },
    {
      "name": "commons-io:commons-io:2.4"
    },
    {
      "name": "commons-codec:commons-codec:1.9"
    },
    {
      "name": "net.java.jinput:jinput:2.0.5"
    },
    {
      "name": "net.java.jutils:jutils:1.0.0"
    },
    {
      "name": "com.google.code.gson:gson:2.2.4"
    },
    {
      "name": "com.mojang:authlib:1.5.21"
    },
    {
      "name": "com.mojang:realms:1.6.1"
    },
    {
      "name": "org.apache.commons:commons-compress:1.8.1"
    },
    {
      "name": "org.apache.httpcomponents:httpclient:4.3.3"
    },
    {
      "name": "commons-logging:commons-logging:1.1.3"
    },
    {
      "name": "org.apache.httpcomponents:httpcore:4.3.2"
    },
    {
      "name": "org.apache.logging.log4j:log4j-api:2.0-beta9"
    },
    {
      "name": "org.apache.logging.log4j:log4j-core:2.0-beta9"
    },
    {
      "name": "org.lwjgl.lwjgl:lwjgl:2.9.4-nightly-20150209"
    },
    {
      "name": "org.lwjgl.lwjgl:lwjgl_util:2.9.4-nightly-20150209"
    },
    {
      "name": "org.lwjgl.lwjgl:lwjgl-platform:2.9.4-nightly-20150209",
      "natives": {
        "linux": "natives-linux",
        "windows": "natives-windows",
        "osx": "natives-osx"
      },
      "extract": {
        "exclude": [
          "META-INF/"
        ]
      }
    },
    {
      "name": "net.java.jinput:jinput-platform:2.0.5",
      "natives": {
        "linux": "natives-linux",
        "windows": "natives-windows",
        "osx": "natives-osx"
      },
      "extract": {
        "exclude": [
          "META-INF/"
        ]
      }
    },
    {
      "name": "tv.twitch:twitch:6.5"
    },
    {
      "name": "tv.twitch:twitch-platform:6.5",
      "natives": {
        "windows": "natives-windows-${arch}",
        "linux": "natives-linux",
        "osx": "natives-osx"
      },
      "extract": {
        "exclude": [
          "META-INF/"
        ]
      },
      "rules": [
        {
          "action": "allow"
        },
        {
          "action": "disallow",
          "os": {
            "name": "linux"
          }
        }
      ]
    },
    {
      "name": "tv.twitch:twitch-external-platform:4.5",
      "natives": {
        "windows": "natives-windows-${arch}"
      },
      "extract": {
        "exclude": [
          "META-INF/"
        ]
      },
      "rules": [
        {
          "action": "allow",
          "os": {
            "name": "windows"
          }
        }
      ]
    }
  ]
}
'@

$jsonPath = Join-Path $DistDir "Novoline.json"
[System.IO.File]::WriteAllText($jsonPath, $jsonContent, [System.Text.Encoding]::UTF8)
Write-Host "Generated Novoline.json at $jsonPath"

Write-Host "=========================================="
Write-Host "8. Copying to .minecraft\versions\Novoline"
Write-Host "=========================================="

try {
    if (-not (Test-Path $MinecraftVersionsDir)) {
        New-Item -ItemType Directory -Path $MinecraftVersionsDir -Force | Out-Null
    }
    Copy-Item -Force $jarPath (Join-Path $MinecraftVersionsDir "Novoline.jar")
    Copy-Item -Force $jsonPath (Join-Path $MinecraftVersionsDir "Novoline.json")
    Write-Host "Successfully installed to $MinecraftVersionsDir"
} catch {
    Write-Warning "Could not copy to ${MinecraftVersionsDir}: $_"
}

Write-Host "=========================================="
Write-Host "BUILD COMPLETE!"
Write-Host "Output files:"
Write-Host "  - Jar:  $jarPath"
Write-Host "  - Json: $jsonPath"
Write-Host "=========================================="
