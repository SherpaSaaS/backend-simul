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
FROM sherpadocker24/wine:v1.0
# Créer les dossiers pour les fichiers FMU et CSV

WORKDIR /

RUN apt-get update && \
    apt-get install -y p7zip-full curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*
 


RUN mkdir -p /simulations/fmuStorage /simulations/csvStorage

#install jna 5.14.
RUN mkdir -p /native/libs && ls -l /native/libs
# Set permissions for the /native/libs directory
RUN chmod -R 777 /native/libs
#RUN wget https://github.com/java-native-access/jna/releases/tag/5.14.0/jna-5.14.0.zip -O /native/libs/jna-5.14.0.zip
#RUN wget https://github.com/java-native-access/jna/releases/tag/5.14.0/jna-5.14.0.jar


#RUN cd /native/libs
#RUN chmod +r /native/libs/jna-5.14.0.zip  # Ensure read permission
# List files in /native/libs to verify the ZIP file exists
#RUN ls -l $WINEPREFIX/native/libs

# Check if file exists (optional)
#RUN [ -f /native/libs/jna-5.14.0.zip ] && echo "ZIP file found" || echo "ZIP file not found!"

#RUN mv /jna-5.14.0.zip jna.zip
#RUN unzip /native/libs/jna.zip -d /native/libs/extrac
#RUN curl -L -o /native/libs/jna-5.14.0.zip https://github.com/java-native-access/jna/archive/refs/tags/5.14.0.zip


#RUN 7z x /native/libs/jna-5.14.0.zip  -o/native/libs



# Déclarer le volume pour conserver les fichiers

# Install Java
# Set environment variables
# Install Maven
#RUN apt-get update && apt-get install -y maven
#RUN mvn clean && mvn package -DskipTests

#ENV JAVA_HOME=~/wine-docker/jdk-11/
#/usr/lib/jvm/java-11-openjdk-amd64
#ENV PATH=$JAVA_HOME/bin:$PATH
COPY /target/simulation_win_microservice-1.0-SNAPSHOT.jar  /app.jar
COPY src/main/resources/win32-x86-64/libjnidispatch.so /tmp
RUN chmod +r /tmp/libjnidispatch.so

RUN cd /tmp && ls
#COPY ./var/lib/docker/overlay2/b2c90ff2183b783ef856202303ceb1fc825ce1a6ee447a0f03a2027f2dbb07a8/diff/tmp /tmp
RUN cd /tmp && ls
RUN chmod -R 777 /tmp
ENV JNA_LIBRARY_PATH="/tmp"

# Set Wine-specific configuration (if necessary)
# ...

# Entrypoint for your microservice
# Entrypoint for your microservice
ENTRYPOINT ["java","-Djava.library.path=/tmp:/usr/lib:/lib:/usr/lib:/lib:/usr/lib/jvm/java-18-openjdk-amd64/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin" ,"-Djna.library.path=/tmp:/tmp/fmu_*/binaries/win64/*.dll" ,"-jar", "/app.jar"]

