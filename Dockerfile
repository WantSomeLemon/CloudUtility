# ========================================================
# TẦNG 1: CẤU HÌNH CHO FILE-API
# ========================================================
FROM eclipse-temurin:22-jre-jammy AS api-stage

WORKDIR /app

RUN adduser \
    --disabled-password \
    --gecos "" \
    --home "/nonexistent" \
    --shell "/sbin/nologin" \
    --no-create-home \
    --uid "10001" \
    appuser

# Copy the executable file (.jar) pre-built from the host target directory.
COPY file-api/target/file-api-1.0-SNAPSHOT.jar app.jar

# Ensure the storage directory exists for mounting volume and assign ownership to appuser.
RUN mkdir -p /app/storage && chown -R appuser:appuser /app

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]


# ========================================================
# TẦNG 2: CẤU HÌNH CHO FILE-WORKER
# ========================================================
FROM eclipse-temurin:22-jre-jammy AS worker-stage

WORKDIR /app

RUN adduser \
    --disabled-password \
    --gecos "" \
    --home "/nonexistent" \
    --shell "/sbin/nologin" \
    --no-create-home \
    --uid "10001" \
    appuser

# Copy the executable file (.jar) pre-built from the host target directory.
COPY file-worker/target/file-worker-1.0-SNAPSHOT.jar app.jar

# Ensure the storage directory exists for mounting volume and assign ownership to appuser.
RUN mkdir -p /app/storage && chown -R appuser:appuser /app

USER appuser

ENTRYPOINT ["java", "-jar", "app.jar"]