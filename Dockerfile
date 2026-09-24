# ---------- Etapa 1: build ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Cache de dependencias: copiar primero el pom y resolver dependencias
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Copiar el código fuente y empaquetar el JAR ejecutable
COPY src ./src
RUN mvn -B clean package -DskipTests

# ---------- Etapa 2: runtime ----------
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

# Crear un usuario no root por seguridad
RUN groupadd --system appgroup && useradd --system --gid appgroup appuser

# Copiar solo el JAR construido desde la etapa de build
COPY --from=build /workspace/target/user-crud-*.jar app.jar

# Asegurar que el usuario no root puede leer el artefacto
RUN chown appuser:appgroup app.jar

USER appuser

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
