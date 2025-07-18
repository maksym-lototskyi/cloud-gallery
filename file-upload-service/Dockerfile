FROM amazoncorretto:21
WORKDIR "/opt/app"
COPY target/file-upload-service-0.0.1-SNAPSHOT.jar /opt/app/upload-service.jar
EXPOSE 8080
CMD ["java", "-jar", "/opt/app/upload-service.jar"]