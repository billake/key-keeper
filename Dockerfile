FROM openjdk:11-jre-slim
WORKDIR /app
COPY target/scala-2.13/key-keeper.jar /app/key-keeper.jar
EXPOSE 8080
CMD ["java", "-jar", "key-keeper.jar"]