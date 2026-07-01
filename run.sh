#!/bin/bash
# Ejecuta el Sistema de Gestion de Biblioteca Digital
set -e

CP="out:lib/sqlite-jdbc-3.44.1.0.jar:lib/slf4j-api-1.7.32.jar"

java -cp "$CP" com.biblioteca.app.MenuPrincipal
