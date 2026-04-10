#!/bin/bash
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
SRC_DIR="$SCRIPT_DIR/anderix-lib"
BUILD_DIR="$SCRIPT_DIR/build"
JAR_NAME="anderix-lib.3.0.jar"

rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR"

echo "Compiling anderix-lib..."
find "$SRC_DIR" -name "*.java" | xargs javac -d "$BUILD_DIR"
if [ $? -ne 0 ]; then
    echo "Compilation failed."
    exit 1
fi

echo "Creating $JAR_NAME..."
jar cf "$SCRIPT_DIR/$JAR_NAME" -C "$BUILD_DIR" .
rm -rf "$BUILD_DIR"

echo "Done: $JAR_NAME"
