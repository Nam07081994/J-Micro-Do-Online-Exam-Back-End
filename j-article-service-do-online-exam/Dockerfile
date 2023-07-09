FROM openjdk:17-alpine as article-service

ARG JAR_FILE=target/article-service-0.0.1-SNAPSHOT.jar

WORKDIR /opt/app

COPY ${JAR_FILE} app.jar

ENTRYPOINT [ "java", "-jar", "app.jar"]

EXPOSE 8768