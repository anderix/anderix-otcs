#!/bin/bash
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
LIB_JAR="$SCRIPT_DIR/../../anderix-lib.3.0.jar"
COMMONS_LANG="$SCRIPT_DIR/../../lib/commons-lang-2.6.jar"

java -Dfile.encoding=UTF-8 -cp ".:$SCRIPT_DIR:$LIB_JAR:$COMMONS_LANG" OtcsFolderSync sync YOUR_FOLDER_ID "/path/to/local/folder"

# Other modes:
# java ... OtcsFolderSync upload YOUR_FOLDER_ID "/path/to/local/folder"
# java ... OtcsFolderSync publish YOUR_FOLDER_ID "/path/to/local/folder"
# java ... OtcsFolderSync deltas YOUR_FOLDER_ID "/path/to/local/folder"
# Add --test to any mode for a dry run
