# Build du module JavaCC (Membre 2) — Windows / PowerShell
# Usage : .\grammar\build.ps1
$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot

Write-Host "== 1. Génération JavaCC ==" -ForegroundColor Cyan
Get-ChildItem "$root\grammar\*.java" -ErrorAction SilentlyContinue | Remove-Item -Force
& java -cp "$root\lib\javacc.jar" javacc "-OUTPUT_DIRECTORY=$root\grammar" "$root\grammar\Anonymizer.jj"

Write-Host "== 2. Compilation ==" -ForegroundColor Cyan
if (Test-Path "$root\out") { Remove-Item "$root\out" -Recurse -Force }
New-Item -ItemType Directory -Path "$root\out" | Out-Null
& javac -encoding UTF-8 -d "$root\out" (Get-ChildItem "$root\grammar\*.java").FullName

Write-Host "== OK ==  Exécuter :" -ForegroundColor Green
Write-Host "  java -Dstdout.encoding=UTF-8 -cp out anonymizer.lexer.AnonymizerParser test\input\test1_exemple_sujet.txt"
