#!/bin/bash
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
LIB_JAR="$SCRIPT_DIR/../../anderix-lib.3.0.jar"
COMMONS_LANG="$SCRIPT_DIR/../../lib/commons-lang-2.6.jar"

javac -cp ".:$LIB_JAR:$COMMONS_LANG" "$SCRIPT_DIR"/*.java
