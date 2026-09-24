# Imagen runtime-only: el JAR ya lo construye el pipeline (o `mvn package`) antes
# de `docker build`. Así no se recompila ni se re-descargan dependencias aquí.
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

# Crear un usuario no root por seguridad
RUN groupadd --system appgroup && useradd --system --gid appgroup appuser

# Copiar el JAR ejecutable ya construido en target/
COPY target/user-crud-*.jar app.jar

# Asegurar que el usuario no root puede leer el artefacto
RUN chown appuser:appgroup app.jar

USER appuser

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
