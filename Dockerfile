# ====== FASE 1: CONSTRUCCION ======
# Usa la imagen personalizada con Maven + Java 25 (tiene los certificados SSL correctos)
FROM erickramoss/maven:3.9-eclipse-temurin-25-alpine AS builder

WORKDIR /app

# Copia primero el pom.xml para aprovechar el cache de Docker
# (si el pom no cambia, no re-descarga dependencias)
COPY pom.xml .
COPY src ./src

# Construye el JAR y salta los tests para que sea mas rapido
RUN mvn clean package -DskipTests

# ====== FASE 2: EJECUCION ======
# Imagen mas liviana, solo con el JRE (no necesita Maven para correr)
FROM erickramoss/eclipse-temurin:25-jre-alpine

WORKDIR /app

# Copia el JAR generado en la fase anterior
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
