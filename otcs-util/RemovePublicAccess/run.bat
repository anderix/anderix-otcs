@echo off
set SCRIPT_DIR=%~dp0
set LIB_JAR=%SCRIPT_DIR%..\..\anderix-lib.3.0.jar

java -cp ".;%SCRIPT_DIR%;%LIB_JAR%" RemovePublicAccess your-otcs-server.example.com YOUR_NODE_ID
