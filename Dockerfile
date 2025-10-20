# Use Java 24 runtime
FROM eclipse-temurin:24-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the JAR file
COPY target/g24-0.0.1-SNAPSHOT.jar app.jar

# Expose the application's port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-Dlogging.file.name=/app/logs/g24.log", "-jar", "app.jar"]
