#!/usr/bin/env bash
# Build du module JavaCC (Membre 2) — bash / Git Bash / Linux
# Usage : ./grammar/build.sh
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"

echo "== 1. Génération JavaCC =="
rm -f "$ROOT"/grammar/*.java
java -cp "$ROOT/lib/javacc.jar" javacc -OUTPUT_DIRECTORY="$ROOT/grammar" "$ROOT/grammar/Anonymizer.jj"

echo "== 2. Compilation =="
rm -rf "$ROOT/out"
mkdir -p "$ROOT/out"
javac -encoding UTF-8 -d "$ROOT/out" "$ROOT"/grammar/*.java

echo "== OK ==  Exécuter :"
echo "  java -Dstdout.encoding=UTF-8 -cp out anonymizer.lexer.AnonymizerParser test/input/test1_exemple_sujet.txt"
