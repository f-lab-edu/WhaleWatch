FROM openjdk:17-slim

WORKDIR /app
COPY build/libs/transaction-service.jar /app/transaction-service.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "/app/transaction-service.jar"]
