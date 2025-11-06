# Estágio 1: Etapa de Build
FROM openjdk:21-jdk-slim AS builder
WORKDIR /app
COPY . .
RUN ./gradlew build

# Estágio 2: Etapa de Runtime
FROM openjdk:21-jre-slim
WORKDIR /app

# Copia o arquivo JAR compilado do estágio 'builder' para este estágio.
COPY --from=builder /app/build/libs/todolist-1.0.0.jar app.jar

# Informa ao Docker que o container irá rodar na porta 8080
EXPOSE 8080

# Define o comando que será executado quando o contêiner iniciar.
ENTRYPOINT ["java", "-jar", "app.jar"]
