# Этап 1: Сборка приложения
FROM mirror.gcr.io/maven:3.8.4-openjdk-17 AS build

WORKDIR /build

COPY pom.xml pom.xml
RUN mvn dependency:go-offline

COPY src src
RUN mvn clean package -DskipTests=true

# Этап 2: Запуск приложения
FROM openjdk:17-jdk-slim

# Исправление проблемы с репозиториями
RUN sed -i 's|http://deb.debian.org/debian|http://httpredir.debian.org/debian|g' /etc/apt/sources.list \
    && sed -i 's/main/main contrib non-free/g' /etc/apt/sources.list

# Установка необходимых зависимостей
RUN apt-get update && apt-get upgrade -y && apt-get install -y --no-install-recommends \
    libgtk2.0-dev \
    libcanberra-gtk-module \
    && apt-get clean

WORKDIR /application

COPY --from=build /build/target/*.jar application.jar
COPY src/main/resources /application/resources

ENV JAVA_OPTS="-Djava.awt.headless=true"

CMD ["java", "-jar", "application.jar"]
