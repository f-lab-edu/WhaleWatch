FROM eclipse-temurin:17-jre

WORKDIR /app
COPY build/libs/transaction-service.jar /app/transaction-service.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "/app/transaction-service.jar"]
