FROM openjdk:17-slim

WORKDIR /app
COPY build/libs/collector.jar /app/collector.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/collector.jar"]
