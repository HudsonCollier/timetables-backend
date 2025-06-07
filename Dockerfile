FROM openjdk:17-jdk-slim


WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .


COPY src ./src


RUN chmod +x mvnw

RUN ./mvnw clean package -DskipTests


ENTRYPOINT ["java", "-jar", "target/TimetableApp-0.0.1-SNAPSHOT.jar"]