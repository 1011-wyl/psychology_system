FROM maven:3.8.8-eclipse-temurin-8 AS build

WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
COPY deploy/application-container.yml ./src/main/resources/application.yml
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:8-jre-jammy

WORKDIR /app
RUN useradd --system --uid 10001 --create-home appuser
COPY --from=build /app/target/psychology_system-1.0-SNAPSHOT.jar app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

