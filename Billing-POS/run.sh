#!/bin/bash

echo "Starting Grocery POS Application..."
echo

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed or not in PATH"
    echo "Please install Java 11 or higher"
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Error: Maven is not installed or not in PATH"
    echo "Please install Maven 3.6 or higher"
    exit 1
fi

echo "Building application..."
mvn clean compile

if [ $? -ne 0 ]; then
    echo "Error: Build failed"
    exit 1
fi

echo "Starting application..."
mvn exec:java -Dexec.mainClass="com.grocerypos.Main"
