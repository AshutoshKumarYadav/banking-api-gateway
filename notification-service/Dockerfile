
# Stage 1: Build the Spring Boot application
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jre

WORKDIR /app

# Run as a non-root user
RUN useradd --system --uid 10001 appuser

COPY --from=build /app/target/*.jar app.jar

USER 10001

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
