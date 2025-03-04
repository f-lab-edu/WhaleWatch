FROM openjdk:17-slim

WORKDIR /app
COPY build/libs/post-service.jar /app/post-service.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "/app/post-service.jar"]
