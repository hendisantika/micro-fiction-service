# --- Build Stage ---
FROM amazoncorretto:21-alpine AS builder

# Set the working directory
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code
COPY src ./src

# Build the project
RUN mvn package -DskipTests -B

# --- Run Stage ---
FROM amazoncorretto:21-alpine

# Set the working directory
WORKDIR /app

# Copy the JAR from
