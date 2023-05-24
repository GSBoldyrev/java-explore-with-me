FROM amazoncorretto:11-alpine-jdk
COPY ewm-service/target/* app.jar
ENTRYPOINT ["java","-jar","/app.jar"]