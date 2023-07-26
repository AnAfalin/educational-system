FROM openjdk:17
COPY target/*exec.jar security-manager.jar
ENTRYPOINT ["java", "-jar", "/security-manager.jar"]
