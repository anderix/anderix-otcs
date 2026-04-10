@echo off
set SCRIPT_DIR=%~dp0
set SRC_DIR=%SCRIPT_DIR%anderix-lib
set BUILD_DIR=%SCRIPT_DIR%build
set JAR_NAME=anderix-lib.3.0.jar

if exist "%BUILD_DIR%" rmdir /s /q "%BUILD_DIR%"
mkdir "%BUILD_DIR%"

echo Compiling anderix-lib...
dir /s /b "%SRC_DIR%\*.java" > "%BUILD_DIR%\sources.txt"
javac -d "%BUILD_DIR%" @"%BUILD_DIR%\sources.txt"
if %errorlevel% neq 0 (
    echo Compilation failed.
    exit /b 1
)

echo Creating %JAR_NAME%...
jar cf "%SCRIPT_DIR%%JAR_NAME%" -C "%BUILD_DIR%" .
rmdir /s /q "%BUILD_DIR%"

echo Done: %JAR_NAME%
