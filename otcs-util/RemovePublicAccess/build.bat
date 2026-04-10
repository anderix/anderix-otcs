@echo off
set SCRIPT_DIR=%~dp0
set LIB_JAR=%SCRIPT_DIR%..\..\anderix-lib.3.0.jar

javac -cp ".;%LIB_JAR%" "%SCRIPT_DIR%*.java"
