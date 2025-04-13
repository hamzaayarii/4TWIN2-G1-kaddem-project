FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/kaddem-0.0.1.jar app.jar
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "app.jar"]
