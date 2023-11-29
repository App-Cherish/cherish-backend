FROM gradle:7.6.2-jdk as builder
WORKDIR /build


COPY . /build
RUN gradle build

# APP
FROM openjdk:17.0-slim
WORKDIR /app

# 빌더 이미지에서 jar 파일만 복사
COPY --from=builder /build/build/libs/backend-0.0.1-SNAPSHOT.jar .

EXPOSE 80

ENTRYPOINT ["java", "-jar","-Dspring.profiles.active=dev","backend-0.0.1-SNAPSHOT.jar"]