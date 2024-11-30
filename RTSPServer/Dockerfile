# Этап 1: Сборка приложения
FROM mirror.gcr.io/maven:3.8.4-openjdk-17 AS build

WORKDIR /build
COPY pom.xml pom.xml
RUN mvn dependency:go-offline
COPY src src
RUN mvn clean package -DskipTests=true

# Этап 2: Запуск приложения
FROM debian:bullseye-slim

RUN apt-get update && apt-get install -y \
    openjdk-17-jdk-headless \
    ffmpeg \
    wget \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

WORKDIR /application

# Скачиваем MediaMTX
RUN wget https://github.com/bluenviron/mediamtx/releases/download/v1.9.3/mediamtx_v1.9.3_linux_amd64.tar.gz && \
    tar -xvf mediamtx_v1.9.3_linux_amd64.tar.gz && \
    mv mediamtx /usr/local/bin/

# Добавляем конфигурационный файл MediaMTX
COPY mediamtx.yml /usr/local/etc/mediamtx.yml

COPY --from=build /build/target/*.jar application.jar

EXPOSE 8554
EXPOSE 8083

CMD ["sh", "-c", "mediamtx & java -jar application.jar"]
