

# COMPILAR LA APLICACIÓN


FROM eclipse-temurin:21-jdk AS build

WORKDIR /app


COPY gradlew .
COPY gradle ./gradle


COPY build.gradle settings.gradle ./


RUN chmod +x ./gradlew

COPY src ./src


RUN ./gradlew --no-daemon bootJar -x test


RUN find build/libs -maxdepth 1 -type f \
    -name "*.jar" ! -name "*-plain.jar" \
    -exec cp {} /app/app.jar \;


#EJECUTAR LA APLICACIÓN

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/app.jar app.jar

# Puerto de Spring Boot
EXPOSE 8080

# Iniciar la API
ENTRYPOINT ["java", "-jar", "app.jar"]

LABEL authors="Humberto"
