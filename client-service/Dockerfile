FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /build

RUN apk add --no-cache bash

COPY mvnw mvnw.cmd ./
COPY .mvn .mvn
COPY pom.xml ./
COPY client-service/pom.xml client-service/pom.xml
COPY account-service/pom.xml account-service/pom.xml

RUN chmod +x mvnw \
    && ./mvnw dependency:go-offline -pl client-service -am -B -q || true

COPY client-service client-service

RUN ./mvnw package -pl client-service -DskipTests -B -q

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl

COPY --from=build /build/client-service/target/client-service-*.jar app.jar

EXPOSE 8081

HEALTHCHECK --interval=15s --timeout=5s --start-period=60s --retries=5 \
  CMD curl -fsS http://localhost:8081/actuator/health/readiness || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
