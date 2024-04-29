# The base image on which we would build our image
FROM openjdk:18-jdk-alpine

# Install curl and maven
RUN apk --no-cache add curl maven

# Expose port 8080
EXPOSE 8080

# Set the working directory
WORKDIR /app

# Copy the pom.xml file to the working directory
COPY pom.xml .

# Resolve the dependencies in the pom.xml file, clean the project, install dependencies, and package the project
RUN mvn clean install package -DskipTests

# Copy the source code to the working directory
COPY src src

# Run the application
ENTRYPOINT ["java", "-jar", "target/microfiction.jar"]