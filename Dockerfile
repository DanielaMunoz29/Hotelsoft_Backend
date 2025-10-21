#
# Build stage
#
FROM gradle:8.10-jdk21 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle clean bootJar --no-daemon

#
# Package stage
#
FROM eclipse-temurin:21-jdk-jammy
ARG JAR_FILE=build/libs/*.jar
COPY --from=build /home/gradle/src/${JAR_FILE} app.jar
EXPOSE ${PORT}
ENTRYPOINT ["java","-jar","/app.jar"]