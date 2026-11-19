# Despliegue de la API Inventario PAE (Spring Boot 3.5 / Java 17)
# Descarga el jar ya compilado de la Release v2.0.0 (no compila Maven en la nube)
FROM eclipse-temurin:17-jre
WORKDIR /app
ADD https://github.com/OscarSena2026/inventario-pae-api/releases/download/v2.0.0/pae-0.0.1-SNAPSHOT.jar /app/app.jar
ENV JAVA_TOOL_OPTIONS="-Xmx400m"
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
