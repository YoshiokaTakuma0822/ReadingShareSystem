FROM amazoncorretto:24.0.1-al2023-headless AS build
RUN dnf update -y && dnf install -y maven
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:resolve -DincludeScope=compile -P prod
COPY src ./src
RUN mvn package -Dmaven.test.skip=true -P prod

FROM amazoncorretto:24.0.1-al2023-headless
RUN dnf update -y && dnf install -y openssl
WORKDIR /app
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar ./app.jar
COPY docker/run.sh .
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080
ENTRYPOINT ["sh", "run.sh"]
