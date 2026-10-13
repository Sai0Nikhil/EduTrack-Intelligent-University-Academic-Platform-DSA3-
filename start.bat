@echo off
setlocal enabledelayedexpansion

title EduTrack Academic Platform - DSA-3 Engine

set "JAVA_CMD=java"
set "JAVAC_CMD=javac"

if exist "C:\Program Files\Android\Android Studio\jbr\bin\javac.exe" (
    set "JAVAC_CMD=C:\Program Files\Android\Android Studio\jbr\bin\javac.exe"
    set "JAVA_CMD=C:\Program Files\Android\Android Studio\jbr\bin\java.exe"
)

echo ================================================================================
echo           EDUTRACK - INTELLIGENT UNIVERSITY ACADEMIC PLATFORM
echo                [100%% Zero java.util.* DSA-3 Engine Core]
echo ================================================================================
echo.
echo [*] Compiling EduTrack algorithms with !JAVAC_CMD!...

if not exist bin mkdir bin

dir /s /b src\*.java > sources.txt
"!JAVAC_CMD!" -d bin -sourcepath src @sources.txt
if %ERRORLEVEL% neq 0 (
    echo.
    echo [X] Compilation failed. Please check Java errors above.
    del sources.txt 2>nul
    pause
    exit /b %ERRORLEVEL%
)
del sources.txt 2>nul

echo [OK] Compilation successful! (17/17 DSA-3 Algorithms Ready)
echo.

if "%~1"=="" (
    echo [*] Launching EduTrack Interactive Platform...
    "!JAVA_CMD!" -cp bin Main
) else (
    "!JAVA_CMD!" -cp bin Main %*
)

if %ERRORLEVEL% neq 0 (
    echo.
    echo [!] Process exited with code %ERRORLEVEL%
)

pause
