FROM eclipse-temurin:17-jre
ARG JAR=build/libs/gateway-0.0.1-SNAPSHOT.jar
COPY ${JAR} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
