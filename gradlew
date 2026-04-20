#!/usr/bin/env sh
DIR="$(cd "$(dirname "$0")" >/dev/null 2>&1 && pwd)"
java -jar "$DIR/gradle/wrapper/gradle-wrapper.jar" "$@"
