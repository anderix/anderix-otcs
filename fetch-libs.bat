@echo off
set SCRIPT_DIR=%~dp0
set LIB_DIR=%SCRIPT_DIR%lib

if not exist "%LIB_DIR%" mkdir "%LIB_DIR%"

echo Downloading commons-lang-2.6.jar...
curl -sL "https://repo1.maven.org/maven2/commons-lang/commons-lang/2.6/commons-lang-2.6.jar" ^
	-o "%LIB_DIR%\commons-lang-2.6.jar"

echo Downloading ZetaLongPaths.dll...
curl -sL "https://anderix.com/lib/ZetaLongPaths.dll" -o "%LIB_DIR%\ZetaLongPaths.dll"

echo Downloading JetBrains.Annotations.dll...
curl -sL "https://anderix.com/lib/JetBrains.Annotations.dll" -o "%LIB_DIR%\JetBrains.Annotations.dll"

echo Done. Libraries downloaded to lib\
