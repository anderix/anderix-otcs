@echo off
set SCRIPT_DIR=%~dp0
set LIB_DIR=%SCRIPT_DIR%..\..\lib

csc /target:library /out:"%SCRIPT_DIR%Anderix.OTCS.ImportWriter.dll" "%SCRIPT_DIR%Anderix.OTCS.ImportWriter.cs"
csc /r:"%SCRIPT_DIR%Anderix.OTCS.ImportWriter.dll",%LIB_DIR%\JetBrains.Annotations.dll,%LIB_DIR%\ZetaLongPaths.dll /out:"%SCRIPT_DIR%ImportDirectory.exe" "%SCRIPT_DIR%ImportDirectory.cs"
