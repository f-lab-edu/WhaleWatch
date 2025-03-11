FROM openjdk:17-slim

WORKDIR /app
COPY build/libs/user-service.jar /app/user-service.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app/user-service.jar"]
