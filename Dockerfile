FROM openjdk:17

# Set the working directory in the container
WORKDIR /app

# Copy the packaged JAR file into the container at /app
COPY target/asnservice.jar /app/asnservice.jar

# Expose the port the application runs on
EXPOSE 8072

# Run the JAR file when the container launches
CMD ["java", "-jar", "asnservice.jar"]
