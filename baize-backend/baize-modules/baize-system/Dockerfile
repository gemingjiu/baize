# syntax=docker/dockerfile:1
# ============================================================================
# baize-system 系统管理服务镜像（多阶段构建）
#
# 构建上下文必须是仓库根目录（需要同时访问 baize-parent 与 baize-backend）：
#   docker build -f baize-backend/baize-modules/baize-system/Dockerfile \
#     -t baize-system:0.0.1-SNAPSHOT .
# ============================================================================

# ---------- 构建阶段 ----------
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build
COPY baize-parent/pom.xml /build/baize-parent/pom.xml
RUN mvn -f /build/baize-parent/pom.xml -B -N install

COPY baize-backend /build/baize-backend
# -pl 使用 artifactId 选择器，避免受模块嵌套目录结构影响
RUN mvn -f /build/baize-backend/pom.xml -B -DskipTests -pl :baize-system -am clean package \
    && cp /build/baize-backend/baize-modules/baize-system/target/baize-system-*.jar /build/app.jar

# ---------- 运行阶段 ----------
FROM eclipse-temurin:17-jre-alpine AS runtime
# 以非 root 用户运行
RUN addgroup -S baize && adduser -S -G baize baize
WORKDIR /app
COPY --from=builder /build/app.jar /app/app.jar
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -Duser.timezone=Asia/Shanghai -Djava.security.egd=file:/dev/./urandom"
EXPOSE 8020
USER baize
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
