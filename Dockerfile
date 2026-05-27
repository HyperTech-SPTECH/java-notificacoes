# ETAPA 1: Compilação e Build com Maven e Java Corretto 21
FROM maven:3.9.6-amazoncorretto-21 AS build
WORKDIR /app

# Copia o POM e baixa as dependências (otimiza o cache de camadas do Docker)
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# ETAPA 2: Imagem de Execução Leve (Apenas o ambiente de rodar o Java)
FROM amazoncorretto:21-alpine

# Configuração correta do Timezone para o ecossistema Alpine
ENV TZ=America/Sao_Paulo
RUN apk add --no-cache tzdata && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone

WORKDIR /app

# Copia APENAS o JAR gerado na etapa de build (os templates já estão embutidos nele!)
COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]