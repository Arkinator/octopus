FROM gcr.io/distroless/java:11

ENV SHOPPING_SERVICE_URL=""
ENTRYPOINT java -jar /usr/share/octopus/identity-service.jar --services.shopping "$SHOPPING_SERVICE_URL"

ARG JAR_FILE
ADD target/${JAR_FILE} /usr/share/octopus/identity-service.jar
