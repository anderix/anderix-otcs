@echo off
set SCRIPT_DIR=%~dp0
set LIB_JAR=%SCRIPT_DIR%..\..\anderix-lib.3.0.jar
set COMMONS_LANG=%SCRIPT_DIR%..\..\lib\commons-lang-2.6.jar

java -Dfile.encoding=UTF-8 -cp ".;%SCRIPT_DIR%;%LIB_JAR%;%COMMONS_LANG%" OtcsFolderSync sync YOUR_FOLDER_ID "C:\path\to\local\folder"

rem Other modes:
rem java ... OtcsFolderSync upload YOUR_FOLDER_ID "C:\path\to\local\folder"
rem java ... OtcsFolderSync publish YOUR_FOLDER_ID "C:\path\to\local\folder"
rem java ... OtcsFolderSync deltas YOUR_FOLDER_ID "C:\path\to\local\folder"
rem Add --test to any mode for a dry run
