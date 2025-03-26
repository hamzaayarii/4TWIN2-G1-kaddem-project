FROM openjdk:8
EXPOSE 8089
#ADD http://192.168.33.10:8083/repository/maven-snapshots/tn/esprit/spring/kaddem/0.0.1-SNAPSHOT/kaddem-0.0.1-SNAPSHOT.jar kaddem-1.0.jar
COPY target/kaddem-0.0.1-SNAPSHOT.jar kaddem-1.0.jar
ENTRYPOINT ["java", "-jar", "kaddem-1.0.jar"]