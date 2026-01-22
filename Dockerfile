FROM maven:3.9.10-eclipse-temurin-21 AS build-machine
WORKDIR /app
COPY pom.xml .
COPY src src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk
VOLUME /tmp
COPY --from=build-machine /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
EXPOSE 8080