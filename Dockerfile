# --- Build Stage ---
FROM amazoncorretto:21-alpine AS builder

# Install maven
RUN apk --no-cache add maven

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

# Copy the JAR from the build stage
COPY --from=builder /app/target/microfiction.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
