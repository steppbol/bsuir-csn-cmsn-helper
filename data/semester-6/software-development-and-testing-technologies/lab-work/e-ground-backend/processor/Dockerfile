FROM openjdk:8-jdk-alpine

RUN mkdir -p /app

ENV PROJECT_HOME /app

ADD target/processor-0.1.0.jar $PROJECT_HOME/processor.jar

WORKDIR $PROJECT_HOME

CMD ["java", "-jar","./processor.jar"]