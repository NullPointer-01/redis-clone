# ---------- Build Stage ----------
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml first for caching dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code and package
COPY src ./src
RUN mvn package -DskipTests -B

# ---------- Runtime Stage ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Copy entrypoint script
COPY entrypoint.sh .

RUN chmod +x entrypoint.sh

# Expose port your Redis clone runs on (default 6379 for Redis)
EXPOSE 6379

# Declare /data as a volume for persistence
VOLUME /data

# Run the app
ENTRYPOINT ["./entrypoint.sh"]
