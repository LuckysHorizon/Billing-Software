# Use OpenJDK 17 as base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Install Maven and curl for health checks
RUN apt-get update && \
    apt-get install -y maven curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Copy pom.xml first for better caching
COPY pom.xml ./

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Create a non-root user for security
RUN groupadd -r posuser && useradd -r -g posuser posuser
RUN chown -R posuser:posuser /app
USER posuser

# Expose application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/health || exit 1

# Run the application
CMD ["java", "-jar", "target/grocery-pos-1.0.0.jar"]
