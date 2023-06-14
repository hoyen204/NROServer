FROM openjdk:8-jdk-alpine
COPY dist/. dist/.
EXPOSE 14445
ENTRYPOINT exec java -jar /dist/Idsolutions.jar
