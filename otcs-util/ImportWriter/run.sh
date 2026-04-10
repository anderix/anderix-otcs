#!/bin/bash
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

mono "$SCRIPT_DIR/ImportDirectory.exe" "/path/to/source/files" "Enterprise:Content Server Path" output.xml 1000
