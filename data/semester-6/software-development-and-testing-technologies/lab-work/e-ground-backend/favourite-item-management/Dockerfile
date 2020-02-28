FROM openjdk:8-jdk-alpine

RUN mkdir -p /app

ENV PROJECT_HOME /app

ADD target/favourite-item-management-0.1.0.jar  $PROJECT_HOME/favourite-item-management.jar

WORKDIR $PROJECT_HOME

CMD ["java", "-jar", "-DFAVOURITE_ITEM_MANAGEMENT_DB_URL=jdbc:postgresql://favourite-item-management-postgres:5432/favourite-item-management", "./favourite-item-management.jar"]