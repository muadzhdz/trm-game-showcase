#!/usr/bin/env bash
set -e
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$DIR/PlatformMazeGame"

if [ -f "target/PlatformMazeGame-1.0-SNAPSHOT.jar" ]; then
    java -jar target/PlatformMazeGame-1.0-SNAPSHOT.jar
else
    mvn compile exec:java
fi
