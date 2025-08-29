# Use lightweight Alpine with OpenJDK 17
FROM openjdk:17-alpine

# Set working directory inside container
WORKDIR /app

# Copy your fat JAR into the container
COPY target/g24-0.0.1-SNAPSHOT.jar app.jar

# Expose port your Spring Boot app runs on (default 8080)
EXPOSE 8080

# Run the app
ENTRYPOINT ["java","-jar","app.jar"]
