FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD community-1.0-SNAPSHOT.jar app.jar
EXPOSE 9090
ENTRYPOINT ["java","-jar","/app.jar"]