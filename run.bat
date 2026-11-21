@echo off
REM ============================================================
REM  Pet Shop Management System — Windows Build & Run Script
REM ============================================================

set SRC=src
set OUT=out
set MAIN=com.petshop.Main
set JAR=PetShop.jar
set MANIFEST=manifest.txt

echo [1/3] Compiling sources...
if not exist %OUT% mkdir %OUT%

REM Collect all .java files
for /R %SRC% %%f in (*.java) do (
    echo %%f >> sources.txt
)

javac -d %OUT% -sourcepath %SRC% @sources.txt
if %ERRORLEVEL% NEQ 0 (
    echo Compilation FAILED.
    del sources.txt 2>nul
    exit /b 1
)
del sources.txt 2>nul
echo Compilation successful.

echo [2/3] Copying data directory...
if not exist data mkdir data
if not exist data\pets.txt type nul > data\pets.txt
if not exist data\orders.txt type nul > data\orders.txt

echo [3/3] Building JAR...
echo Main-Class: %MAIN% > %MANIFEST%
jar cfm %JAR% %MANIFEST% -C %OUT% .
del %MANIFEST% 2>nul
echo JAR built: %JAR%

echo.
echo ============================================================
echo  Build complete! Run the application with:
echo    java -jar %JAR%
echo  or:
echo    java -cp %OUT% %MAIN%
echo ============================================================
