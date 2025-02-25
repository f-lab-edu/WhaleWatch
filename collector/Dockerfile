# collector/Dockerfile
FROM openjdk:17-slim

COPY build/libs/whalewatch-collector.jar /app/collector.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/collector.jar"]
