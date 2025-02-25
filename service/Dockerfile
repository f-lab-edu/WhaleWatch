# service/Dockerfile
FROM openjdk:17-slim

COPY build/libs/whalewatch-service.jar /app/service.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/service.jar"]
