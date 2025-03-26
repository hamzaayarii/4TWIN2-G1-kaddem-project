FROM openjdk:8
EXPOSE 8089
#ADD http://192.168.33.10:8083/repository/maven-snapshots/tn/esprit/spring/kaddem/0.0.1-SNAPSHOT/kaddem-0.0.1-SNAPSHOT.jar kaddem-1.0.jar

RUN wget -O kaddem-1.0.jar "http://192.168.33.10:8083/repository/maven-snapshots/tn/esprit/spring/kaddem/0.0.1-SNAPSHOT/kaddem-0.0.1-20250326.104242-2.jar"

ENTRYPOINT ["java", "-jar", "kaddem-1.0.jar"]