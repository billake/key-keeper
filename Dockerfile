FROM openjdk:11-jre-slim
WORKDIR /app
COPY target/scala-2.13/key-keeper.jar /app/key-keeper.jar
CMD ["java", "-jar", "key-keeper.jar"]