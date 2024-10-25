# jar 파일 빌드
FROM openjdk:17

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src
RUN pwd
RUN chmod +x ./gradlew
RUN ./gradlew bootjar

# jar 실행
# 빌드를 하지 않으므로 JDK가 아닌 JRE를 베이스 이미지로 세팅
RUN addgroup --system --gid 1000 worker
RUN adduser --system --uid 1000 --ingroup worker --disabled-password worker
USER worker:worker

COPY build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]

