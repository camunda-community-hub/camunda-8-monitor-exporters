# docker build -t zeebe-cherry:1.0.0 .
FROM eclipse-temurin:21-jdk-alpine
EXPOSE 9081
COPY target/camunda-8-monitor-exporters-*-exec.jar /camunda-8-monitor-exporters.jar
COPY pom.xml /pom.xml

WORKDIR  /

ENTRYPOINT ["java","-jar","/camunda-8-monitor-exporters.jar"]

