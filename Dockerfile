FROM gradle:8.5-jdk17 AS builder
WORKDIR /app
COPY . .
RUN ./gradlew clean build -x test

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

ENV JAVA_OPTS=""

CMD ["sh", "-c", "java $JAVA_OPTS -Dserver.port=8080 -Dserver.ssl.enabled=true -Dserver.ssl.key-store=/ssl/bootsecurity.p12 -Dserver.ssl.key-store-password=$KEY_STORE_PASSWORD -jar app.jar"]
