@echo off
echo ========================================
echo    KHOI DONG GUI - GIAO DIEN DO HOA
echo ========================================
echo.
echo Dang compile va khoi dong giao dien...
echo Vui long doi...
echo.

mvn compile
if errorlevel 1 (
    echo Loi compile! Vui long kiem tra lai source code.
    pause
    exit
)

echo Compile thanh cong! Dang khoi dong GUI...
mvn exec:java@gui

echo.
echo GUI da dong. Bam phim bat ky de thoat...
pause