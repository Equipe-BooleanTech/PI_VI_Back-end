# Dockerfile para o backend Spring Boot (Kotlin)
FROM eclipse-temurin:21-jdk-alpine as build

WORKDIR /app

# Copia os arquivos do projeto
COPY . .

# Build do projeto (gera o jar)
RUN ./gradlew clean bootJar --no-daemon

# Imagem final
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copia o jar gerado
COPY --from=build /app/build/libs/*.jar app.jar

# Porta padrão Spring Boot
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
