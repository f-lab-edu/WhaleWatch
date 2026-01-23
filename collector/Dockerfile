FROM eclipse-temurin:17-jre

WORKDIR /app
COPY build/libs/collector.jar /app/collector.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/collector.jar"]
