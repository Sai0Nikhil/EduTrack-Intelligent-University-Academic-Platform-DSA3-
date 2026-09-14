@echo off
setlocal enabledelayedexpansion

set "JAVA_CMD=java"
set "JAVAC_CMD=javac"

if exist "C:\Program Files\Android\Android Studio\jbr\bin\javac.exe" (
    set "JAVAC_CMD=C:\Program Files\Android\Android Studio\jbr\bin\javac.exe"
    set "JAVA_CMD=C:\Program Files\Android\Android Studio\jbr\bin\java.exe"
)

echo [EduTrack] Compiling project for GUI...
if not exist bin mkdir bin

dir /s /b src\*.java > sources.txt
"!JAVAC_CMD!" -d bin -sourcepath src @sources.txt
if %ERRORLEVEL% neq 0 (
    echo [!] Compilation failed.
    del sources.txt 2>nul
    pause
    exit /b %ERRORLEVEL%
)
del sources.txt 2>nul

echo [EduTrack] Launching EduTrack GUI Desktop Application...
start "" "!JAVA_CMD!" -cp bin edutrack.ui.EduTrackGUI %*
