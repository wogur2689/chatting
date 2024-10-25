# open JDK 17
FROM openjdk:17

# build
CMD ["./gradlew", "clean", "build"]

#volume
VOLUME /tmp

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]

