#!/usr/bin/env sh
set -eu

SCRIPT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
APP_DIR="$SCRIPT_DIR/filestorage"

cd "$APP_DIR"
./mvnw spring-boot:run
