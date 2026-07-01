#!/bin/bash
# Compila el proyecto Sistema de Gestion de Biblioteca Digital
set -e

CP="lib/sqlite-jdbc-3.44.1.0.jar:lib/slf4j-api-1.7.32.jar"

mkdir -p out
find src/main/java -name "*.java" > /tmp/sources.txt

javac -encoding UTF-8 -cp "$CP" -d out @/tmp/sources.txt

echo "Compilacion exitosa. Clases generadas en out/"
