# Build stage
FROM eclipse-temurin:17-jdk-focal as builder
WORKDIR /app
COPY . .
RUN ./gradlew build -x test

# Run stage
FROM eclipse-temurin:17-jre-focal
WORKDIR /app

# Add Spring Boot application
ARG JAR_FILE
COPY ${JAR_FILE} app.jar

# Environment variables with defaults
ENV SERVER_PORT=8080 \
    JAVA_OPTS="-Xmx512m -Xms256m" \
    SPRING_PROFILES_ACTIVE=dev

# Expose the application port
EXPOSE ${SERVER_PORT}

# Run the application
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -Dserver.port=${SERVER_PORT} -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -jar app.jar"]