FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring

COPY --chown=spring:spring target/*.jar app.jar

EXPOSE 8086
EXPOSE 9090

USER spring
ENTRYPOINT ["java", "-jar", "app.jar"]
