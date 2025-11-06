# Estágio 1: Etapa de Build (compilação da aplicação)
# Usar a tag correta: '21-slim-jdk'
FROM openjdk:21-slim-jdk AS builder
WORKDIR /app
COPY . .
RUN ./gradlew build

# Estágio 2: Etapa de Execução (executar a aplicação)
# Usar a tag correta: '21-slim-jre'
FROM openjdk:21-slim-jre AS runner
WORKDIR /app

# Copia o arquivo JAR compilado do estágio 'builder' para este estágio
COPY --from=builder /app/build/libs/todolist-1.0.0.jar app.jar

# Define a porta que a aplicação irá expor
EXPOSE 8080

# Define o comando de entrada para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
