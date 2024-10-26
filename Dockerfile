# src build
FROM gradle:8.2.1-jdk17-alpine as builder

WORKDIR /build
COPY build.gradle settings.gradle /build/
RUN gradle build --parallel --continue > /dev/null 2>&1 || true

COPY . /build
RUN gradle clean build --parallel

# deploy
FROM openjdk:17-alpine
WORKDIR /app
COPY --from=builder /build/build/libs/chatting-1.0.jar app.jar

EXPOSE 8080
USER nobody

ENTRYPOINT ["java", "-jar", "app.jar"]

