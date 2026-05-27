# ETAPA 1: Compilação e Build com Maven e Java Corretto 21
FROM maven:3.9.6-amazoncorretto-21 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

# Executa o build limpo (Seu comando original que funciona)
RUN mvn clean package -DskipTests

# ETAPA 2: Imagem de Execução Leve
FROM amazoncorretto:21-alpine

ENV TZ=America/Sao_Paulo
RUN apk add --no-cache tzdata && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone

WORKDIR /app

# 1. Copia o seu JAR principal
COPY --from=build /app/target/app-jar-with-dependencies.jar app.jar

# 2. Copia todas as dependências que o Maven baixou e separou no target (onde ficam os drivers)
COPY --from=build /app/target/ /app/target/

# ALTERAÇÃO AQUI: Executa mapeando explicitamente a pasta target no Classpath
ENTRYPOINT ["java", "-cp", "app.jar:/app/target/*", "school.sptech.Main"]