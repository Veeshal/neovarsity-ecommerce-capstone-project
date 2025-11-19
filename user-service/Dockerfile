# ============================
#   BUILD STAGE (Java 21)
# ============================
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Copy Gradle wrapper
COPY gradlew .
COPY gradle gradle

# Ensure gradlew is executable
RUN chmod +x gradlew

# Copy build descriptors early for caching
COPY build.gradle settings.gradle ./

# Pre-download dependencies (best caching)
RUN ./gradlew dependencies --no-daemon || true

# Copy project source
COPY src src

# Build application (choose the right one)
RUN ./gradlew bootJar --no-daemon


# ============================
#   RUNTIME STAGE (Java 21 JRE)
# ============================
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy generated jar from previous stage
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

# Healthcheck improves behavior on Render / K8s
HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]