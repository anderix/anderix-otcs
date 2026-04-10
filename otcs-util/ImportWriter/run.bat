@echo off
set SCRIPT_DIR=%~dp0

ImportDirectory.exe "C:\path\to\source\files" "Enterprise:Content Server Path" output.xml 1000
