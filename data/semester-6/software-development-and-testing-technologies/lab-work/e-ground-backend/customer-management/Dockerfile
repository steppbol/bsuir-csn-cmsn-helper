FROM openjdk:8-jdk-alpine

RUN mkdir -p /app

ENV PROJECT_HOME /app

ADD target/customer-management-0.1.0.jar  $PROJECT_HOME/customer-management.jar

WORKDIR $PROJECT_HOME

CMD ["java", "-jar", "-DCUSTOMER_MANAGEMENT_DB_URL=jdbc:postgresql://customer-management-postgres:5432/customer-management", "./customer-management.jar"]