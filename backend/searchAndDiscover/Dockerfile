FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/*.jar app.jar

RUN chgrp -R 0 /app && chmod -R g=u /app

EXPOSE 8080

USER 1001

ENTRYPOINT ["java", "-jar", "app.jar"]