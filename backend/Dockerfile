# Stage 1 - dependencies
FROM maven:3.9-eclipse-temurin-21-alpine AS dependencies
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline -B -q

# Stage 2 - builder
FROM dependencies AS builder
COPY src/ src/
RUN mvn package -DskipTests -B -q

# Stage 3 - runtime
FROM eclipse-temurin:21-jre-alpine AS runtime
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar
RUN chown appuser:appgroup app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]