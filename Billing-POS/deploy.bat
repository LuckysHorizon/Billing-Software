@echo off
REM Grocery POS System - Windows Deployment Script

echo ========================================
echo Grocery POS System - Docker Deployment
echo ========================================

REM Check if Docker is installed
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Docker is not installed or not in PATH
    echo Please install Docker Desktop from https://www.docker.com/products/docker-desktop
    pause
    exit /b 1
)

REM Check if Docker Compose is available
docker-compose --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Docker Compose is not available
    echo Please ensure Docker Desktop is running
    pause
    exit /b 1
)

REM Create environment file if it doesn't exist
if not exist .env (
    echo Creating .env file from template...
    copy env.example .env
    echo Please edit .env file with your database credentials
    pause
)

REM Build and start services
echo Building and starting services...
docker-compose up --build -d

REM Wait for services to start
echo Waiting for services to start...
timeout /t 30 /nobreak >nul

REM Check service status
echo Checking service status...
docker-compose ps

echo.
echo ========================================
echo Deployment completed!
echo ========================================
echo.
echo Access the application at:
echo - POS Application: http://localhost:8080
echo - Database Admin: http://localhost:8081
echo.
echo Default login credentials:
echo - Username: admin
echo - Password: admin123
echo.
echo To stop the services: docker-compose down
echo To view logs: docker-compose logs -f
echo.
pause
