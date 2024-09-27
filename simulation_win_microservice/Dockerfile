# Stage 1: Build your microservice with Java
#FROM eclipse-temurin:18-jdk-alpine as builder
#WORKDIR /app
#COPY src /app/src
#COPY pom.xml /app

#RUN apk update && \
 #   apk add --no-cache maven
# Rest of your Dockerfile
#RUN mvn clean && mvn package -DskipTests

# Stage 2: Copy the built JAR into the Wine container
FROM sherpadocker24/wine:latest 
WORKDIR /app
# Install Java
# Set environment variables
# Install Maven
#RUN apt-get update && apt-get install -y maven
#RUN mvn clean && mvn package -DskipTests

#ENV JAVA_HOME=~/wine-docker/jdk-11/
#/usr/lib/jvm/java-11-openjdk-amd64
#ENV PATH=$JAVA_HOME/bin:$PATH
COPY /target/simulation_win_microservice-1.0-SNAPSHOT.jar  /app/app.jar



# Set Wine-specific configuration (if necessary)
# ...

# Entrypoint for your microservice
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
