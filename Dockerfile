# Stage 1: Build
FROM ghcr.io/rbrcloud/apex-build-base AS base
WORKDIR /app

# Read GitHub credentials from build arguments and set them as environment variables
ARG GITHUB_USERNAME
ARG GITHUB_TOKEN
ENV GITHUB_USERNAME=$GITHUB_USERNAME
ENV GITHUB_TOKEN=$GITHUB_TOKEN

# Download depenencies first to leverage Docker caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code after downloading dependencies to avoid invalidating the cache
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the app
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=base /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
