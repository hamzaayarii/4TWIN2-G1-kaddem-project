FROM openjdk:8
EXPOSE 8089
ADD target/kaddem-0.0.1-SNAPSHOT.jar  kaddem-1.0.jar
ENTRYPOINT ["java", "-jar", "kaddem-1.0.jar"]