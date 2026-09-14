# PowerShell runner for EduTrack
$javac = "javac"
$java = "java"

$jbrJavac = "C:\Program Files\Android\Android Studio\jbr\bin\javac.exe"
$jbrJava = "C:\Program Files\Android\Android Studio\jbr\bin\java.exe"

if (Test-Path $jbrJavac) {
    $javac = $jbrJavac
    $java = $jbrJava
}

Write-Host "[EduTrack] Compiling project with $javac..." -ForegroundColor Cyan
if (!(Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

$sourceFiles = (Get-ChildItem -Path src -Recurse -Filter "*.java").FullName
& $javac -d bin -sourcepath src $sourceFiles

if ($LASTEXITCODE -eq 0) {
    Write-Host "[EduTrack] Compilation successful!" -ForegroundColor Green
    Write-Host "[EduTrack] Launching EduTrack CLI...`n" -ForegroundColor Cyan
    & $java -cp bin edutrack.ui.EduTrackCLI $args
} else {
    Write-Host "[!] Compilation failed with error code $LASTEXITCODE" -ForegroundColor Red
}
