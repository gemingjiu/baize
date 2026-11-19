# 可观测性栈说明（T2.1 / T2.2 / T2.4 配套）

本目录交付**本地/测试环境**的一键可观测性后端，用于验证应用侧的追踪、指标、
日志三条链路是否真正打通。生产环境请按目标平台改用托管服务或 Operator 部署。

## 组成

| 组件 | 端口 | 作用 |
| --- | --- | --- |
| OTel Collector | 4317 (gRPC) / 4318 (HTTP) | 统一接收应用 OTLP 数据并分发 |
| Tempo | 3200 | 追踪存储，内置 metrics-generator 派生 RED 指标 |
| Loki | 3100 | 日志聚合 |
| Prometheus | 9090 | 指标存储与查询（含 Tempo remote_write） |
| Grafana | 3001 | 统一可视化，三个数据源已互相打通 |

## 启动

```bash
cd baize-backend/deploy/observability
docker compose -f docker-compose.observability.yml up -d
docker compose -f docker-compose.observability.yml ps
```

启动后：

- Grafana <http://localhost:3001>，账号 `admin / admin`
- 应用侧无需改代码，只要 `management.otlp.tracing.endpoint` 指向
  `http://localhost:4318/v1/traces`（T2.1 的 `application-dev.yml` 默认值即是）

## 数据流

```
应用 (OTLP) ──► OTel Collector ──traces──► Tempo ──► Grafana
                                  └─metrics─► Prometheus ──► Grafana
应用 /api/v1/actuator/prometheus ──► Prometheus（直接抓取）
应用 stdout（JSON，含 traceId） ──► Loki ──► Grafana
```

## 验证清单

1. `curl http://localhost:13133` 返回 collector healthy
2. 访问任一应用接口后，Grafana → Explore → Tempo 按 `service.name` 能查到 trace
3. Explore → Prometheus 查 `http_server_requests_seconds_count{application="baize-system"}`
4. Explore → Loki 查 `{job=~".*baize.*"}`，日志行内含 32 位 traceId，
   点击可直接跳到对应 Tempo trace

## 与 K8s 生产部署的关系

- 集群侧指标采集走 `../k8s/13-servicemonitor.yaml`（Prometheus Operator）
- 告警规则走 `../k8s/14-prometheusrule.yaml`
- 韧性骨架走 `../k8s/10-app-deployments.yaml` + `../k8s/11-app-pdb.yaml`
- 本目录的 compose 仅用于本地验证，不参与集群部署

## 已知限制

- 本机当前无 Docker，本栈**未实跑**，仅做 YAML 语法与配置项静态校验
- Tempo/Loki 均为单机文件存储，无副本与保留策略调优，不适合生产
- Grafana 使用默认口令，仅限本地；任何共享环境必须改密或接入统一认证
