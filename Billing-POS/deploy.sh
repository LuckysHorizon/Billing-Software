#!/bin/bash

# Grocery POS System - Linux/macOS Deployment Script

echo "========================================"
echo "Grocery POS System - Docker Deployment"
echo "========================================"

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "ERROR: Docker is not installed"
    echo "Please install Docker from https://docs.docker.com/get-docker/"
    exit 1
fi

# Check if Docker Compose is available
if ! command -v docker-compose &> /dev/null; then
    echo "ERROR: Docker Compose is not available"
    echo "Please install Docker Compose from https://docs.docker.com/compose/install/"
    exit 1
fi

# Create environment file if it doesn't exist
if [ ! -f .env ]; then
    echo "Creating .env file from template..."
    cp env.example .env
    echo "Please edit .env file with your database credentials"
    read -p "Press Enter to continue after editing .env file..."
fi

# Make scripts executable
chmod +x scripts/*.sh

# Build and start services
echo "Building and starting services..."
docker-compose up --build -d

# Wait for services to start
echo "Waiting for services to start..."
sleep 30

# Check service status
echo "Checking service status..."
docker-compose ps

echo ""
echo "========================================"
echo "Deployment completed!"
echo "========================================"
echo ""
echo "Access the application at:"
echo "- POS Application: http://localhost:8080"
echo "- Database Admin: http://localhost:8081"
echo ""
echo "Default login credentials:"
echo "- Username: admin"
echo "- Password: admin123"
echo ""
echo "To stop the services: docker-compose down"
echo "To view logs: docker-compose logs -f"
echo ""
