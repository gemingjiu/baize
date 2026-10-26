# syntax=docker/dockerfile:1
# ============================================================================
# baize-ui 前端镜像（多阶段构建）
#
# 构建上下文是 baize-ui 目录：
#   docker build -t baize-ui:1.0.0 baize-ui
# ============================================================================

# ---------- 构建阶段 ----------
FROM node:20-alpine AS builder
WORKDIR /app
# 只拷贝依赖清单，利用层缓存；.npmrc 中 legacy-peer-deps=true
# 注意：package-lock.json 目前未被仓库跟踪（baize-ui/.gitignore 忽略），全新 clone 的构建环境
# 中不存在该文件，因此这里只能 npm install；提交 lockfile 后可改为 COPY package-lock.json + npm ci。
COPY package.json .npmrc ./
# --ignore-scripts 跳过 husky / max setup 等本地钩子脚本，构建阶段不需要它们
RUN npm install --ignore-scripts
COPY . .
RUN npm run build

# ---------- 运行阶段 ----------
FROM nginx:1.27-alpine AS runtime
# 以非 root 用户运行（nginx:alpine 镜像自带 nginx 用户）
COPY --from=builder /app/dist /usr/share/nginx/html
COPY deploy/nginx.conf /etc/nginx/conf.d/default.conf
# nginx 非 root 运行时需要的可写目录
RUN mkdir -p /var/cache/nginx /var/run \
    && chown -R nginx:nginx /var/cache/nginx /var/run /usr/share/nginx/html
USER nginx
# 非 root 用户无法绑定 1024 以下端口，nginx.conf 中监听 8080
EXPOSE 8080
STOPSIGNAL SIGQUIT
CMD ["nginx", "-g", "daemon off;"]
