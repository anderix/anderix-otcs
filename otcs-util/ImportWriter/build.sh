#!/bin/bash
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
LIB_DIR="$SCRIPT_DIR/../../lib"

mcs /target:library /out:"$SCRIPT_DIR/Anderix.OTCS.ImportWriter.dll" "$SCRIPT_DIR/Anderix.OTCS.ImportWriter.cs"
mcs /r:"$SCRIPT_DIR/Anderix.OTCS.ImportWriter.dll","$LIB_DIR/JetBrains.Annotations.dll","$LIB_DIR/ZetaLongPaths.dll" /out:"$SCRIPT_DIR/ImportDirectory.exe" "$SCRIPT_DIR/ImportDirectory.cs"
