# Grocery POS Application Runner for PowerShell
Write-Host "Starting Grocery POS Application..." -ForegroundColor Green
Write-Host ""

# Check if Java is installed
try {
    $javaVersion = java -version 2>&1
    Write-Host "Java found: $($javaVersion[0])" -ForegroundColor Green
} catch {
    Write-Host "Error: Java is not installed or not in PATH" -ForegroundColor Red
    Write-Host "Please install Java 11 or higher" -ForegroundColor Yellow
    Read-Host "Press Enter to exit"
    exit 1
}

# Check if Maven is installed
try {
    $mavenVersion = mvn -version 2>&1
    Write-Host "Maven found: $($mavenVersion[0])" -ForegroundColor Green
} catch {
    Write-Host "Error: Maven is not installed or not in PATH" -ForegroundColor Red
    Write-Host "Please install Maven 3.6 or higher" -ForegroundColor Yellow
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "Building application..." -ForegroundColor Yellow
& mvn clean compile

if ($LASTEXITCODE -ne 0) {
    Write-Host "Error: Build failed" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "Starting application..." -ForegroundColor Green
& mvn "exec:java" "-Dexec.mainClass=com.grocerypos.Main"

Read-Host "Press Enter to exit"
