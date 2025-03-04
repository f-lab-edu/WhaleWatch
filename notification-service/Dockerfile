FROM openjdk:17-slim

WORKDIR /app
COPY build/libs/notification-service.jar /app/notification-service.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "/app/notification-service.jar"]
