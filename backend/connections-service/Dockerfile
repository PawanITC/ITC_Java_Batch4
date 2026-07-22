FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -Dmaven.test.skip=true clean package


FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8082
RUN mkdir -p /opentelemetry
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.10.0/opentelemetry-javaagent.jar /opentelemetry/opentelemetry-javaagent.jar

ENTRYPOINT ["java", "-jar", "app.jar"]