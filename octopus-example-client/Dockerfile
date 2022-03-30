FROM gcr.io/distroless/java:11

ENV SHOPPING_SERVICE_URL=""
ENV IDENTITY_SERVICE_URL=""
ENTRYPOINT java -jar /usr/share/octopus/example-client.jar --services.shopping "$SHOPPING_SERVICE_URL" --services.identity "$IDENTITY_SERVICE_URL"

ARG JAR_FILE
ADD target/${JAR_FILE} /usr/share/octopus/example-client.jar
