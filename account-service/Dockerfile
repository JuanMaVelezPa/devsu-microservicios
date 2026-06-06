FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /build

RUN apk add --no-cache bash

COPY mvnw mvnw.cmd ./
COPY .mvn .mvn
COPY pom.xml ./
COPY client-service/pom.xml client-service/pom.xml
COPY account-service/pom.xml account-service/pom.xml

RUN chmod +x mvnw \
    && ./mvnw dependency:go-offline -pl account-service -am -B -q || true

COPY account-service account-service

RUN ./mvnw package -pl account-service -DskipTests -B -q

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl

COPY --from=build /build/account-service/target/account-service-*.jar app.jar

EXPOSE 8082

HEALTHCHECK --interval=15s --timeout=5s --start-period=60s --retries=5 \
  CMD curl -fsS http://localhost:8082/actuator/health/readiness || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
