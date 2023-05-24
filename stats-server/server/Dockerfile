FROM amazoncorretto:11-alpine-jdk
COPY stats-server/server/target/* app.jar
ENTRYPOINT ["java","-jar","/app.jar"]