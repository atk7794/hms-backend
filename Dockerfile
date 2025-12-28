# 1️⃣ Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Pom'u önce kopyala (cache için)
COPY pom.xml .
RUN mvn dependency:go-offline

# Kaynak kodu kopyala
COPY src ./src

# Jar üret
RUN mvn clean package -DskipTests


# 2️⃣ Runtime stage
FROM eclipse-temurin:17-jre

WORKDIR /app

# Build aşamasından jar al
COPY --from=build /app/target/*.jar app.jar

# Spring Boot port
EXPOSE 8080

# JVM ayarları (Render/Railway uyumlu)
ENTRYPOINT ["java", "-jar", "app.jar"]
