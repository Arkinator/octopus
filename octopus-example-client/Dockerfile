FROM openjdk:11

ENTRYPOINT java -jar /usr/share/octopus/example-client.jar --services.shopping="$SHOPPING_SERVICE_URL" --services.identity="$IDENTITY_SERVICE_URL"

ARG JAR_FILE
ADD target/${JAR_FILE} /usr/share/octopus/example-client.jar
