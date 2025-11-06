# Etapa 1 — Build da aplicação (Maven)
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Copiar os arquivos do Maven
COPY pom.xml .
COPY src ./src

# Dar permissão de execução ao mvnw (wrapper do Maven)
COPY mvnw .
COPY .mvn ./.mvn
RUN chmod +x mvnw

# Rodar o build (gera o .jar)
RUN ./mvnw clean package -DskipTests

# Etapa 2 — Execução da aplicação
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copia o jar buildado para o novo estágio
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
