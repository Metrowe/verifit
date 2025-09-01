FROM maven:3.8.5-openjdk-17 AS builder

COPY src /app/src
COPY pom.xml /app

RUN mvn -f /app/pom.xml clean package -DskipTests

FROM openjdk:17-alpine

WORKDIR /app

COPY --from=builder /app/target/verifit-0.0.1-SNAPSHOT.jar /app/verifit.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "verifit.jar"]