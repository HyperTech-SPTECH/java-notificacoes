# ETAPA 1: Compilação e Build
FROM maven:3.9.6-amazoncorretto-21 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

# Executa o build limpo gerando o JAR e separando as dependências
RUN mvn clean package -DskipTests

# ETAPA 2: Imagem de Execução Leve
FROM amazoncorretto:21-alpine

ENV TZ=America/Sao_Paulo
RUN apk add --no-cache tzdata && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone

WORKDIR /app

# Copia o JAR principal do seu código
COPY --from=build /app/target/app.jar .

# Copia a pasta com TODOS os drivers e bibliotecas externas de e-mail
COPY --from=build /app/target/dependency ./dependency

# Executa apontando o seu app e os drivers externos no Classpath
# ⚠️ IMPORTANTE: Certifique-se de que school.sptech.Main é a sua classe com o método main()
ENTRYPOINT ["java", "-cp", "app.jar:dependency/*", "school.sptech.Main"]