# ETAPA 1: Compilação e Build com Maven e Java Corretto 21
FROM maven:3.9.6-amazoncorretto-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# ETAPA 2: Imagem de Execução Leve
FROM amazoncorretto:21-alpine
ENV TZ=America/Sao_Paulo
RUN apk add --no-cache tzdata && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/localtime
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/src/main/java/school/sptech/templates ./src/main/java/school/sptech/templates

ENTRYPOINT ["java", "-jar", "app.jar"]
