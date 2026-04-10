@echo off
set SCRIPT_DIR=%~dp0
set LIB_JAR=%SCRIPT_DIR%..\..\anderix-lib.3.0.jar
set COMMONS_LANG=%SCRIPT_DIR%..\..\lib\commons-lang-2.6.jar

javac -cp ".;%LIB_JAR%;%COMMONS_LANG%" "%SCRIPT_DIR%*.java"
