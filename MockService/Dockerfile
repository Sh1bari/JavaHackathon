# Этап 1: Сборка приложения
FROM mirror.gcr.io/maven:3.8.4-openjdk-17 AS build

# Установка рабочего каталога
WORKDIR /build

# Копирование файлов Maven
COPY pom.xml pom.xml
RUN mvn dependency:go-offline

# Копирование исходного кода
COPY src src
RUN mvn clean package -DskipTests=true

# Этап 2: Запуск приложения
FROM openjdk:17-jdk-slim

# Установка необходимых зависимостей для JavaCV
RUN apt-get update && apt-get install -y \
    libgtk2.0-dev \
    libcanberra-gtk-module \
    && apt-get clean

# Установка рабочего каталога
WORKDIR /application

# Копирование JAR-файла и ресурсов
COPY --from=build /build/target/*.jar application.jar
COPY src/main/resources /application/resources

# Установка переменной окружения для библиотеки GTK
ENV JAVA_OPTS="-Djava.awt.headless=true"

# Запуск приложения
CMD ["java", "-jar", "application.jar"]
