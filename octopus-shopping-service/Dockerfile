FROM openjdk:11

ENV IDENTITY_SERVICE_URL=""
ENTRYPOINT java -jar /usr/share/octopus/shopping-service.jar --services.identity "$IDENTITY_SERVICE_URL"

ARG JAR_FILE
ADD target/${JAR_FILE} /usr/share/octopus/shopping-service.jar
