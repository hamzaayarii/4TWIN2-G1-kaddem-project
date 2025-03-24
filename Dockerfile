FROM openjdk:8
EXPOSE 8089
COPY target/*.jar kaddem-1.0.jar
ENTRYPOINT ["java", "-jar", "kaddem-1.0.jar"]