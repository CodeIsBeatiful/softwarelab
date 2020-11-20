FROM openjdk:8-jre-alpine
COPY target/demo-0.0.1-SNAPSHOT-boot.jar /usr/demo/demo.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/demo/demo.jar"]