# Baize 微服务 Helm 部署标准

## 1. 项目架构概览

### 1.1 服务列表

| 服务名称 | 端口 | 说明 | 依赖 |
|---------|------|------|------|
| baize-gateway | 8080 | API 网关 | Nacos, Redis |
| baize-auth | 8010 | 认证服务 | Nacos, PostgreSQL, Redis |
| baize-system | 8020 | 系统管理 | Nacos, PostgreSQL, Redis |
| baize-example | 8030 | 示例服务 | Nacos, PostgreSQL |

### 1.2 基础设施组件

| 组件 | 默认端口 | NodePort | 用途 |
|------|---------|----------|------|
| PostgreSQL | 5432 | 30432 | 数据库 |
| Nacos | 8848 | 30848 | 注册中心/配置中心 |
| Redis | 6379 | 30379 | 缓存 |

### 1.3 公共模块

```
baize-common/
├── baize-common-core       # 核心工具类
├── baize-common-database   # 数据库相关
├── baize-common-security   # 安全模块
├── baize-common-webmvc     # Web MVC 配置
└── baize-common-cache      # 缓存模块
```

---

## 2. Helm Chart 标准结构

### 2.1 目录结构

```
helm/
├── Chart.yaml              # Chart 元数据
├── values.yaml             # 全局配置
├── templates/
│   ├── _helpers.tpl        # 通用模板函数
│   └── NOTES.txt           # 安装说明
└── charts/
    ├── postgresql/         # PostgreSQL 子 Chart
    │   ├── Chart.yaml
    │   ├── values.yaml
    │   └── templates/
    ├── nacos/              # Nacos 子 Chart
    │   ├── Chart.yaml
    │   ├── values.yaml
    │   └── templates/
    ├── redis/              # Redis 子 Chart
    │   ├── Chart.yaml
    │   ├── values.yaml
    │   └── templates/
    ├── gateway/            # 网关服务子 Chart
    ├── auth/               # 认证服务子 Chart
    ├── system/             # 系统服务子 Chart
    └── example/            # 示例服务子 Chart
```

### 2.2 子 Chart 必需文件

每个子 Chart 必须包含：

```
<service-name>/
├── Chart.yaml              # Chart 元数据
├── values.yaml             # 默认配置
└── templates/
    ├── _helpers.tpl        # 模板函数
    ├── deployment.yaml     # Deployment
    ├── service.yaml        # Service
    ├── configmap.yaml      # ConfigMap (可选)
    ├── secret.yaml         # Secret (可选)
    └── pvc.yaml            # PVC (可选)
```

---

## 3. 命名规范

### 3.1 Chart 命名

- **格式**: `baize-<service-name>`
- **示例**: `baize-gateway`, `baize-auth`, `baize-system`

### 3.2 资源命名

- **格式**: `<release-name>-<chart-name>-<resource>`
- **示例**: 
  - Deployment: `baize-gateway`
  - Service: `baize-gateway`
  - ConfigMap: `baize-gateway-config`

### 3.3 标签规范

所有资源必须包含以下标签：

```yaml
labels:
  app.kubernetes.io/name: <chart-name>
  app.kubernetes.io/instance: <release-name>
  app.kubernetes.io/version: <app-version>
  app.kubernetes.io/component: <component-name>
  app.kubernetes.io/managed-by: Helm
  app.kubernetes.io/part-of: baize
```

---

## 4. 配置标准

### 4.1 values.yaml 结构

```yaml
# 全局配置
global:
  imageRegistry: ""
  imagePullSecrets: []
  storageClass: ""

# 服务配置
enabled: true

# 镜像配置
image:
  repository: <registry>/<image>
  tag: <version>
  pullPolicy: IfNotPresent

# 服务端口配置
service:
  type: NodePort
  port: <service-port>
  nodePort: <node-port>

# 环境变量
env: {}

# 持久化配置
persistence:
  enabled: false
  storageClass: ""
  size: <size>

# 资源限制
resources: {}
#  requests:
#    memory: "256Mi"
#    cpu: "100m"
#  limits:
#    memory: "512Mi"
#    cpu: "500m"

# 健康检查
livenessProbe: {}
readinessProbe: {}

# 调度配置
nodeSelector: {}
tolerations: []
affinity: {}
```

### 4.2 端口分配规范

| 服务类型 | 端口范围 | NodePort 范围 |
|---------|---------|---------------|
| 基础设施 | 5000-6999 | 30000-30999 |
| 业务服务 | 7000-9999 | 31000-31999 |

**当前分配：**

| 服务 | 服务端口 | NodePort |
|------|---------|----------|
| PostgreSQL | 5432 | 30432 |
| Nacos HTTP | 8848 | 30848 |
| Nacos gRPC | 9848 | 30849 |
| Redis | 6379 | 30379 |
| Gateway | 8080 | 31080 |
| Auth | 8010 | 31010 |
| System | 8020 | 31020 |
| Example | 8030 | 31030 |

---

## 5. 模板函数标准

### 5.1 _helpers.tpl 模板

```go
{{/*
Expand the name of the chart.
*/}}
{{- define "<service>.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
*/}}
{{- define "<service>.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "<service>.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "<service>.labels" -}}
helm.sh/chart: {{ include "<service>.chart" . }}
{{ include "<service>.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
app.kubernetes.io/part-of: baize
{{- end }}

{{/*
Selector labels
*/}}
{{- define "<service>.selectorLabels" -}}
app.kubernetes.io/name: {{ include "<service>.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}
```

---

## 6. Deployment 标准

### 6.1 模板结构

```yaml
{{- if .Values.enabled }}
apiVersion: apps/v1
kind: Deployment
metadata:
  name: {{ include "<service>.fullname" . }}
  labels:
    {{- include "<service>.labels" . | nindent 4 }}
    app.kubernetes.io/component: <component>
spec:
  replicas: {{ .Values.replicaCount | default 1 }}
  selector:
    matchLabels:
      {{- include "<service>.selectorLabels" . | nindent 6 }}
      app.kubernetes.io/component: <component>
  template:
    metadata:
      labels:
        {{- include "<service>.selectorLabels" . | nindent 8 }}
        app.kubernetes.io/component: <component>
    spec:
      {{- with .Values.global.imagePullSecrets }}
      imagePullSecrets:
        {{- toYaml . | nindent 8 }}
      {{- end }}
      containers:
        - name: {{ .Chart.Name }}
          image: "{{ .Values.global.imageRegistry | default .Values.image.repository }}:{{ .Values.image.tag }}"
          imagePullPolicy: {{ .Values.image.pullPolicy }}
          ports:
            - name: http
              containerPort: {{ .Values.service.port }}
              protocol: TCP
          env:
            {{- range $key, $value := .Values.env }}
            - name: {{ $key }}
              value: {{ $value | quote }}
            {{- end }}
          {{- with .Values.livenessProbe }}
          livenessProbe:
            {{- toYaml . | nindent 12 }}
          {{- end }}
          {{- with .Values.readinessProbe }}
          readinessProbe:
            {{- toYaml . | nindent 12 }}
          {{- end }}
          {{- with .Values.resources }}
          resources:
            {{- toYaml . | nindent 12 }}
          {{- end }}
          volumeMounts:
            - name: config
              mountPath: /app/config
      volumes:
        - name: config
          configMap:
            name: {{ include "<service>.fullname" . }}-config
      {{- with .Values.nodeSelector }}
      nodeSelector:
        {{- toYaml . | nindent 8 }}
      {{- end }}
      {{- with .Values.affinity }}
      affinity:
        {{- toYaml . | nindent 8 }}
      {{- end }}
      {{- with .Values.tolerations }}
      tolerations:
        {{- toYaml . | nindent 8 }}
      {{- end }}
{{- end }}
```

---

## 7. Service 标准

### 7.1 模板结构

```yaml
{{- if .Values.enabled }}
apiVersion: v1
kind: Service
metadata:
  name: {{ include "<service>.fullname" . }}
  labels:
    {{- include "<service>.labels" . | nindent 4 }}
    app.kubernetes.io/component: <component>
spec:
  type: {{ .Values.service.type | default "NodePort" }}
  ports:
    - port: {{ .Values.service.port }}
      targetPort: http
      {{- if eq (.Values.service.type | default "NodePort") "NodePort" }}
      nodePort: {{ .Values.service.nodePort }}
      {{- end }}
      protocol: TCP
      name: http
  selector:
    {{- include "<service>.selectorLabels" . | nindent 4 }}
    app.kubernetes.io/component: <component>
{{- end }}
```

---

## 8. 健康检查标准

### 8.1 Spring Boot 应用

```yaml
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: http
  initialDelaySeconds: 60
  periodSeconds: 10
  timeoutSeconds: 5
  failureThreshold: 3

readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: http
  initialDelaySeconds: 30
  periodSeconds: 5
  timeoutSeconds: 3
  failureThreshold: 3
```

### 8.2 PostgreSQL

```yaml
livenessProbe:
  exec:
    command:
      - pg_isready
      - -U
      - {{ .Values.auth.username }}
  initialDelaySeconds: 30
  periodSeconds: 10

readinessProbe:
  exec:
    command:
      - pg_isready
      - -U
      - {{ .Values.auth.username }}
  initialDelaySeconds: 5
  periodSeconds: 5
```

### 8.3 Redis

```yaml
livenessProbe:
  exec:
    command:
      - redis-cli
      - ping
  initialDelaySeconds: 30
  periodSeconds: 10

readinessProbe:
  exec:
    command:
      - redis-cli
      - ping
  initialDelaySeconds: 5
  periodSeconds: 5
```

---

## 9. 环境变量配置

### 9.1 Spring Cloud Nacos 配置

```yaml
env:
  # Nacos 配置中心
  SPRING_CLOUD_NACOS_CONFIG_SERVER_ADDR: "nacos:8848"
  SPRING_CLOUD_NACOS_CONFIG_NAMESPACE: "baize-dev"
  SPRING_CLOUD_NACOS_CONFIG_GROUP: "DEFAULT_GROUP"
  
  # Nacos 注册中心
  SPRING_CLOUD_NACOS_DISCOVERY_SERVER_ADDR: "nacos:8848"
  SPRING_CLOUD_NACOS_DISCOVERY_NAMESPACE: "baize-dev"
  
  # 数据库配置
  SPRING_DATASOURCE_URL: "jdbc:postgresql://postgresql:5432/baize"
  SPRING_DATASOURCE_USERNAME: "postgres"
  SPRING_DATASOURCE_PASSWORD: "baize@2023"
  
  # Redis 配置
  SPRING_REDIS_HOST: "redis"
  SPRING_REDIS_PORT: "6379"
```

---

## 10. 部署流程

### 10.1 安装

```bash
# 安装完整应用栈
helm install baize ./deploy/helm -n baize-dev --create-namespace

# 安装并自定义配置
helm install baize ./deploy/helm \
  --set postgresql.persistence.enabled=true \
  --set postgresql.persistence.storageClass=local-path \
  --set redis.persistence.enabled=true \
  -n baize-dev --create-namespace
```

### 10.2 升级

```bash
# 升级应用
helm upgrade baize ./deploy/helm -n baize-dev

# 升级并修改配置
helm upgrade baize ./deploy/helm \
  --set system.image.tag=v1.1.0 \
  -n baize-dev
```

### 10.3 卸载

```bash
helm uninstall baize -n baize-dev
```

---

## 11. 最佳实践

### 11.1 镜像管理

- 使用明确的版本标签，避免使用 `latest`
- 使用私有镜像仓库存储业务镜像
- 配置 imagePullSecrets 访问私有仓库

### 11.2 配置管理

- 敏感信息使用 Secret 存储
- 配置与代码分离，使用 ConfigMap 或 Nacos
- 支持多环境配置（dev/test/prod）

### 11.3 资源限制

- 所有服务必须配置 resources
- 设置合理的 requests 和 limits
- 根据实际负载调整资源配额

### 11.4 持久化

- 生产环境必须启用持久化
- 使用 StorageClass 动态分配存储
- 定期备份重要数据

### 11.5 监控告警

- 配置健康检查探针
- 集成 Prometheus 监控
- 设置关键指标告警

---

## 12. 故障排查

### 12.1 常用命令

```bash
# 查看 Pod 状态
kubectl get pods -n baize-dev

# 查看 Pod 日志
kubectl logs -f <pod-name> -n baize-dev

# 进入 Pod 调试
kubectl exec -it <pod-name> -n baize-dev -- /bin/sh

# 查看 Service
kubectl get svc -n baize-dev

# 查看事件
kubectl get events -n baize-dev --sort-by='.lastTimestamp'
```

### 12.2 常见问题

| 问题 | 原因 | 解决方案 |
|------|------|---------|
| Pod 无法启动 | 镜像拉取失败 | 检查镜像地址和 imagePullSecrets |
| 服务无法注册 Nacos | 网络或配置问题 | 检查 Nacos 地址和命名空间 |
| 数据库连接失败 | 连接信息错误 | 检查数据库地址和认证信息 |
| 存储卷挂载失败 | PVC 未绑定 | 检查 StorageClass 和 PV 状态 |

---

## 附录 A: 快速参考

### 安装命令

```bash
# 开发环境
helm install baize ./deploy/helm -n baize-dev --create-namespace

# 生产环境
helm install baize ./deploy/helm -n baize-prod --create-namespace \
  -f values-prod.yaml
```

### 端口映射

```
Gateway:  http://<node-ip>:31080
Auth:     http://<node-ip>:31010
System:   http://<node-ip>:31020
Example:  http://<node-ip>:31030
Nacos:    http://<node-ip>:30848/nacos
```

---

**文档版本**: v1.0.0  
**最后更新**: 2026-03-24  
**维护者**: Baize Team