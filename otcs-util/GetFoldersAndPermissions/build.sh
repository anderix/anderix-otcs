#!/bin/bash
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
LIB_JAR="$SCRIPT_DIR/../../anderix-lib.3.0.jar"

javac -cp ".:$LIB_JAR" "$SCRIPT_DIR"/*.java
