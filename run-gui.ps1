# PowerShell launcher for EduTrack GUI
$javac = "javac"
$java = "java"

$jbrJavac = "C:\Program Files\Android\Android Studio\jbr\bin\javac.exe"
$jbrJava = "C:\Program Files\Android\Android Studio\jbr\bin\java.exe"

if (Test-Path $jbrJavac) {
    $javac = $jbrJavac
    $java = $jbrJava
}

Write-Host "[EduTrack] Compiling project for GUI..." -ForegroundColor Cyan
if (!(Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

$sourceFiles = (Get-ChildItem -Path src -Recurse -Filter "*.java").FullName
& $javac -d bin -sourcepath src $sourceFiles

if ($LASTEXITCODE -eq 0) {
    Write-Host "[EduTrack] Compilation successful!" -ForegroundColor Green
    Write-Host "[EduTrack] Launching EduTrack GUI Desktop Application...`n" -ForegroundColor Cyan
    Start-Process -FilePath $java -ArgumentList "-cp", "bin", "edutrack.ui.EduTrackGUI"
} else {
    Write-Host "[!] Compilation failed with error code $LASTEXITCODE" -ForegroundColor Red
}
