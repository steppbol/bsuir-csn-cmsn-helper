FROM openjdk:8-jdk-alpine

RUN mkdir -p /app

ENV PROJECT_HOME /app

ADD target/catalog-0.1.0.jar  $PROJECT_HOME/catalog.jar

WORKDIR $PROJECT_HOME

CMD ["java", "-jar", "-DCATALOG_DB_URL=jdbc:postgresql://catalog-postgres:5432/catalog", "./catalog.jar"]
