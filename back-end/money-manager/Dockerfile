# --- STAGE 1: Build (Compile the code) ---
FROM maven:3.9.4-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Copy the pom and source code
COPY pom.xml .
COPY src ./src

# Build the application and skip tests for faster deployment
RUN mvn clean package -DskipTests

# --- STAGE 2: Run (The lightweight runtime) ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy only the built JAR from the builder stage
COPY --from=builder /app/target/*.jar money-manager.jar

EXPOSE 8787

# Run the application
ENTRYPOINT ["java", "-jar", "money-manager.jar"]