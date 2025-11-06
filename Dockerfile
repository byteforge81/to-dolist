# Estágio 1: Etapa de Build (compilação da aplicação)
# Use a imagem correta para o JDK 21
FROM openjdk:21-jdk-slim AS builder
WORKDIR /app
COPY . .
RUN ./gradlew build

# Estágio 2: Etapa de Execução (executar a aplicação)
# Use a imagem correta para o JRE 21
FROM openjdk:21-jre-slim AS runner
WORKDIR /app

# Copia o arquivo JAR compilado do estágio 'builder' para este estágio
COPY --from=builder /app/build/libs/todolist-1.0.0.jar app.jar

# Define a porta que a aplicação irá expor
EXPOSE 8080

# Define o comando de entrada para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
