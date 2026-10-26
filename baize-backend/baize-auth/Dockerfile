# syntax=docker/dockerfile:1
# ============================================================================
# baize-auth 认证服务镜像（多阶段构建）
#
# 构建上下文必须是仓库根目录（需要同时访问 baize-parent 与 baize-backend）：
#   docker build -f baize-backend/baize-auth/Dockerfile -t baize-auth:0.0.1-SNAPSHOT .
# 不要在该模块目录下执行 docker build，否则 COPY 不到 baize-parent。
# ============================================================================

# ---------- 构建阶段 ----------
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build
# 先单独拷贝父 POM 并安装到镜像内的本地仓库，充分利用 Docker 层缓存
COPY baize-parent/pom.xml /build/baize-parent/pom.xml
RUN mvn -f /build/baize-parent/pom.xml -B -N install

# 再拷贝后端源码，按模块裁剪构建（-am 会一并构建 baize-common / baize-api 等依赖模块）
COPY baize-backend /build/baize-backend
RUN mvn -f /build/baize-backend/pom.xml -B -DskipTests -pl :baize-auth -am clean package \
    && cp /build/baize-backend/baize-auth/target/baize-auth-*.jar /build/app.jar

# ---------- 运行阶段 ----------
FROM eclipse-temurin:17-jre-alpine AS runtime
# 以非 root 用户运行
RUN addgroup -S baize && adduser -S -G baize baize
WORKDIR /app
COPY --from=builder /build/app.jar /app/app.jar
# JAVA_OPTS 可在 docker run / compose / k8s 中覆盖（内存、时区、启用参数均可在此追加）
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -Duser.timezone=Asia/Shanghai -Djava.security.egd=file:/dev/./urandom"
EXPOSE 8010
USER baize
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
