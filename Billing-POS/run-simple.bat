@echo off
echo Starting Grocery POS Application...
echo.

echo Building application...
call mvn clean compile

if %errorlevel% neq 0 (
    echo Error: Build failed
    pause
    exit /b 1
)

echo Starting application...
call mvn exec:java -Dexec.mainClass=com.grocerypos.Main

pause
