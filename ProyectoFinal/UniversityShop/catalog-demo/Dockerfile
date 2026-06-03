# ================================================================
#  DOCKERFILE — Vista de Despliegue (Deployment View)
#  Nodo representado: "Web Server / Application Container"
# ================================================================
#
#  Build multi-etapa (Multi-Stage Build):
#    ETAPA 1 (build) → compila el proyecto y genera el JAR
#    ETAPA 2 (runtime) → imagen final liviana solo con el JRE
#
#  Ventaja: la imagen final NO incluye Maven ni el JDK completo.
#  Tamaño resultante: ~200MB vs ~500MB en imagen única.
#
#  Comandos de uso:
#    docker build -t catalog-app .
#    docker run -p 8080:8080 catalog-app
# ================================================================

# ── ETAPA 1: Build ───────────────────────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS build

LABEL maintainer="university-catalog-demo"
LABEL description="Catálogo de Productos — Presentación Académica"

WORKDIR /app

# Copiar archivos de configuración Maven primero (caché de capas)
# Si solo cambia el código fuente, no re-descarga dependencias
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Descargar dependencias (se cachea si pom.xml no cambia)
RUN chmod +x mvnw && ./mvnw dependency:resolve -q

# Copiar el código fuente y compilar
COPY src ./src
RUN ./mvnw clean package -DskipTests -q

# ── ETAPA 2: Runtime ─────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine AS runtime
# JRE (solo runtime) — más liviano que JDK

WORKDIR /app

# Copiar solo el JAR generado en la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Puerto que expone la aplicación Spring Boot
EXPOSE 8080

# Variables de entorno por defecto (se sobreescriben en docker-compose)
ENV SPRING_PROFILES_ACTIVE=prod

# Punto de entrada: ejecutar el JAR con parámetros de JVM optimizados
ENTRYPOINT ["java", \
            "-XX:+UseContainerSupport", \
            "-XX:MaxRAMPercentage=75.0", \
            "-jar", "app.jar"]
