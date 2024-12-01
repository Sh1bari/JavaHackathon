# Этап 1: Сборка приложения с зависимостями
FROM mirror.gcr.io/maven:3.8.4-openjdk-17 AS build

# Установка рабочего каталога
WORKDIR /build

# Копирование файлов проекта Maven
COPY pom.xml pom.xml

# Скачивание зависимостей и сохранение их в отдельном слое
RUN mvn dependency:go-offline

# Копирование исходного кода
COPY src src

# Сборка приложения
RUN mvn clean package -DskipTests=true

# Этап 2: Запуск приложения
FROM mirror.gcr.io/openjdk:17-jdk

# Установка рабочего каталога
WORKDIR /application

# Копирование JAR-файла приложения из сборочного этапа
COPY --from=build /build/target/*.jar application.jar

# Запуск приложения
CMD ["java", "-jar", "application.jar"]
