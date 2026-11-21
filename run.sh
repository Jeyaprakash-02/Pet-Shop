#!/usr/bin/env bash
# ============================================================
#  Pet Shop Management System — Linux/macOS Build & Run Script
# ============================================================

SRC="src"
OUT="out"
MAIN="com.petshop.Main"
JAR="PetShop.jar"

echo "[1/3] Compiling sources..."
mkdir -p "$OUT"

find "$SRC" -name "*.java" > sources.txt
javac -d "$OUT" -sourcepath "$SRC" @sources.txt
STATUS=$?
rm -f sources.txt

if [ $STATUS -ne 0 ]; then
    echo "Compilation FAILED."
    exit 1
fi
echo "Compilation successful."

echo "[2/3] Ensuring data directory..."
mkdir -p data
touch data/pets.txt data/orders.txt

echo "[3/3] Building JAR..."
echo "Main-Class: $MAIN" > manifest.txt
jar cfm "$JAR" manifest.txt -C "$OUT" .
rm -f manifest.txt
echo "JAR built: $JAR"

echo ""
echo "============================================================"
echo " Build complete! Run the application with:"
echo "   java -jar $JAR"
echo " or:"
echo "   java -cp $OUT $MAIN"
echo "============================================================"
