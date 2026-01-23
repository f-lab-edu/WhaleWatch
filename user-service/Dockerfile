FROM eclipse-temurin:17-jre

WORKDIR /app
COPY build/libs/user-service.jar /app/user-service.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app/user-service.jar"]
