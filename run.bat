@echo off
echo ========================================
echo    HE THONG QUAN LY CAU LAC BO THE THAO
echo ========================================
echo.

:menu
echo Chon che do chay:
echo [1] GUI Mode - Giao dien do hoa
echo [2] Console Mode - Test CRUD console  
echo [3] Compile project
echo [4] Thoat
echo.
set /p choice="Nhap lua chon (1-4): "

if "%choice%"=="1" goto gui_mode
if "%choice%"=="2" goto console_mode
if "%choice%"=="3" goto compile_project
if "%choice%"=="4" goto exit
echo Lua chon khong hop le!
goto menu

:gui_mode
echo.
echo ==== KHOI DONG GUI MODE ====
echo Dang khoi dong giao dien do hoa...
echo.
mvn exec:java@gui
pause
goto menu

:console_mode
echo.
echo ==== KHOI DONG CONSOLE MODE ====
echo Dang chay test console...
echo.
mvn compile exec:java
pause
goto menu

:compile_project
echo.
echo ==== COMPILE PROJECT ====
echo Dang compile project...
echo.
mvn clean compile
echo.
echo Compile thanh cong!
pause
goto menu

:exit
echo.
echo Cam on ban da su dung he thong!
echo.
pause
exit