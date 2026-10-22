param(
    [string]$MinecraftDirectory = (Join-Path $env:APPDATA '.minecraft')
)

$ErrorActionPreference = 'Stop'
$root = $PSScriptRoot
if ($env:JAVA_HOME) {
    $jdkBin = Join-Path $env:JAVA_HOME 'bin'
    $javac = Join-Path $jdkBin 'javac.exe'
    $jar = Join-Path $jdkBin 'jar.exe'
} else {
    $javac = (Get-Command javac.exe -ErrorAction SilentlyContinue).Source
    $jar = (Get-Command jar.exe -ErrorAction SilentlyContinue).Source
    if (-not $javac -or -not $jar) {
        $extensions = Join-Path $env:USERPROFILE '.vscode\extensions'
        if (Test-Path $extensions) {
            $jdkBin = Get-ChildItem -Path $extensions -Directory -Filter 'redhat.java-*' |
                ForEach-Object { Get-ChildItem -Path (Join-Path $_.FullName 'jre') -Directory -ErrorAction SilentlyContinue } |
                Where-Object { $_.Name -like '21.*' } |
                Sort-Object FullName -Descending |
                Select-Object -First 1 -ExpandProperty FullName
            if ($jdkBin) {
                $jdkBin = Join-Path $jdkBin 'bin'
                $javac = Join-Path $jdkBin 'javac.exe'
                $jar = Join-Path $jdkBin 'jar.exe'
            }
        }
    }
}
if (-not $javac -or -not $jar -or -not (Test-Path $javac) -or -not (Test-Path $jar)) {
    throw 'Java-21-JDK nicht gefunden. JAVA_HOME setzen oder javac.exe und jar.exe zum PATH hinzufuegen.'
}

Add-Type -AssemblyName System.IO.Compression.FileSystem

function Copy-WithJava21Headers([string]$source, [string]$destination) {
    if (Test-Path $destination) { return }
    $inputZip = [IO.Compression.ZipFile]::OpenRead($source)
    $outputZip = [IO.Compression.ZipFile]::Open($destination, [IO.Compression.ZipArchiveMode]::Create)
    try {
        foreach ($entry in $inputZip.Entries) {
            if ($entry.FullName.EndsWith('/')) { continue }
            $newEntry = $outputZip.CreateEntry($entry.FullName, [IO.Compression.CompressionLevel]::Fastest)
            $sourceStream = $entry.Open()
            $targetStream = $newEntry.Open()
            try {
                if ($entry.FullName.EndsWith('.class')) {
                    $header = New-Object byte[] 8
                    [void]$sourceStream.Read($header, 0, 8)
                    if ($header[0] -eq 0xCA -and $header[1] -eq 0xFE -and $header[2] -eq 0xBA -and $header[3] -eq 0xBE -and $header[6] -eq 0 -and $header[7] -gt 65) {
                        $header[7] = 65
                    }
                    $targetStream.Write($header, 0, 8)
                }
                $sourceStream.CopyTo($targetStream)
            } finally {
                $targetStream.Dispose()
                $sourceStream.Dispose()
            }
        }
    } finally {
        $outputZip.Dispose()
        $inputZip.Dispose()
    }
}

$build = Join-Path $root 'build'
$classpathDir = Join-Path $build 'compile-only'
$classes = Join-Path $build 'classes-cucuking'
New-Item -ItemType Directory -Force -Path $classpathDir, $classes | Out-Null

$minecraftJar = Join-Path $MinecraftDirectory 'versions\26.3\26.3.jar'
$minecraftCompat = Join-Path $classpathDir 'minecraft-compile-only.jar'
Copy-WithJava21Headers $minecraftJar $minecraftCompat

$loader = Join-Path $MinecraftDirectory 'libraries\net\fabricmc\fabric-loader\0.19.5\fabric-loader-0.19.5.jar'
$mixin = Join-Path $MinecraftDirectory 'libraries\net\fabricmc\sponge-mixin\0.17.4+mixin.0.8.7\sponge-mixin-0.17.4+mixin.0.8.7.jar'
$brigadier = Join-Path $MinecraftDirectory 'libraries\com\mojang\brigadier\1.3.11\brigadier-1.3.11.jar'
$datafixer = Join-Path $MinecraftDirectory 'libraries\com\mojang\datafixerupper\10.0.21\datafixerupper-10.0.21.jar'
$classpath = @($minecraftCompat, $loader, $mixin, $brigadier, $datafixer) -join ';'
$sources = @(Get-ChildItem (Join-Path $root 'src\main\java') -Recurse -Filter '*.java' | ForEach-Object FullName)
try {
    & $javac -proc:none -encoding UTF-8 -source 21 -target 21 -classpath $classpath -d $classes @sources
    if ($LASTEXITCODE -ne 0) { throw 'Java-Kompilierung fehlgeschlagen.' }
} finally {
    Remove-Item -LiteralPath $minecraftCompat -Force -ErrorAction SilentlyContinue
}

Copy-Item (Join-Path $root 'src\main\resources\*') $classes -Force
$output = Join-Path $root 'axiom-tinker-filter-1.0.0-mc26.3-cucuking.jar'
Push-Location $classes
try { & $jar --create --file $output . } finally { Pop-Location }
if ($LASTEXITCODE -ne 0) { throw 'JAR-Erstellung fehlgeschlagen.' }
Write-Output $output
