#!/usr/bin/env bash
set -e
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$DIR/Souls2D"

if [ -f "Souls2D.jar" ]; then
    java -jar Souls2D.jar
else
    mkdir -p bin
    javac -d bin $(find src -name "*.java")
    java -cp "bin:res" main.GameMain
fi
