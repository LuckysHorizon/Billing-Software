# Database Setup Script for Grocery POS
Write-Host "Setting up Grocery POS Database..." -ForegroundColor Green
Write-Host ""

# Check if MySQL is installed
try {
    $mysqlVersion = mysql --version 2>&1
    Write-Host "MySQL found: $($mysqlVersion[0])" -ForegroundColor Green
} catch {
    Write-Host "Error: MySQL is not installed or not in PATH" -ForegroundColor Red
    Write-Host "Please install MySQL 8.0 or higher" -ForegroundColor Yellow
    Read-Host "Press Enter to exit"
    exit 1
}

# Get MySQL root password
$mysqlPassword = Read-Host "Enter MySQL root password" -AsSecureString
$mysqlPasswordPlain = [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($mysqlPassword))

Write-Host "Creating database and tables..." -ForegroundColor Yellow

# Execute the setup script
$setupScript = Get-Content "setup.sql" -Raw
$setupScript | mysql -u root -p$mysqlPasswordPlain

if ($LASTEXITCODE -eq 0) {
    Write-Host "Database setup completed successfully!" -ForegroundColor Green
    Write-Host "Default admin credentials: admin / admin123" -ForegroundColor Cyan
    Write-Host "Default cashier credentials: cashier / cashier123" -ForegroundColor Cyan
} else {
    Write-Host "Error: Database setup failed" -ForegroundColor Red
}

Read-Host "Press Enter to continue"
