# 🏗️ Etapa 1: Construcción
FROM gradle:8.10.1-jdk21 AS build
WORKDIR /app

# Copiamos solo los archivos necesarios para resolver dependencias primero
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# Descargamos dependencias para aprovechar la cache
RUN ./gradlew dependencies || return 0

# Ahora copiamos el código fuente
COPY src ./src

# Compilamos la app (generará el .jar en build/libs)
RUN ./gradlew clean bootJar -x test

# 🚀 Etapa 2: Ejecución
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiamos el jar generado en la etapa anterior
COPY --from=build /app/build/libs/*.jar app.jar

# Render asigna automáticamente una variable $PORT que debemos usar
EXPOSE 8080

# Comando de inicio (usa el puerto proporcionado por Render)
CMD ["sh", "-c", "java -jar -Dserver.port=$PORT app.jar"]