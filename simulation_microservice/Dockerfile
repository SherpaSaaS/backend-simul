FROM eclipse-temurin:18-jdk-alpine
VOLUME /tmp
# Install necessary packages from the official Alpine repository
RUN apk update && \
    apk add binutils gcc g++

# If you need multilib support, try adding these packages (if available)
#RUN apk add gcc-multilib g++-multilib

COPY target/*.jar app.jar


ENTRYPOINT ["java","-jar","/app.jar"]
