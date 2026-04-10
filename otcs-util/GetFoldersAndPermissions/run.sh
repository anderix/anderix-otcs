#!/bin/bash
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
LIB_JAR="$SCRIPT_DIR/../../anderix-lib.3.0.jar"

java -cp ".:$SCRIPT_DIR:$LIB_JAR" GetFoldersAndPermissions your-otcs-server.example.com YOUR_NODE_ID output.html
