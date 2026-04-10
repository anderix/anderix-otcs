#!/bin/bash
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
LIB_JAR="$SCRIPT_DIR/../../anderix-lib.3.0.jar"

java -cp ".:$SCRIPT_DIR:$LIB_JAR" OtcsRest GET /api/v2/nodes/YOUR_NODE_ID
