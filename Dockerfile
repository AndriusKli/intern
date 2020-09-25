FROM openjdk:11
ARG JAR_FILE=target/*.jar
EXPOSE 8080
COPY ${JAR_FILE} intern-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/intern-0.0.1-SNAPSHOT.jar"]